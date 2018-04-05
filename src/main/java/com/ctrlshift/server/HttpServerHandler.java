package com.ctrlshift.server;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.ctrlshift.commons.SessionProtocol.H1;
import static com.ctrlshift.commons.SessionProtocol.H1C;
import static com.ctrlshift.commons.SessionProtocol.H2;
import static com.ctrlshift.commons.SessionProtocol.H2C;
import static com.ctrlshift.commons.util.Functions.voidFunction;
import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.ctrlshift.commons.AggregatedHttpMessage;
import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.HttpHeaderNames;
import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.HttpMethod;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.HttpStatus;
import com.ctrlshift.commons.MediaType;
import com.ctrlshift.commons.NonWrappingRequestContext;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.RequestContext;
import com.ctrlshift.commons.SessionProtocol;
import com.ctrlshift.commons.logging.DefaultRequestLog;
import com.ctrlshift.commons.logging.RequestLog;
import com.ctrlshift.commons.logging.RequestLogAvailability;
import com.ctrlshift.commons.logging.RequestLogBuilder;
import com.ctrlshift.commons.metric.NoopMeterRegistry;
import com.ctrlshift.commons.stream.ClosedPublisherException;
import com.ctrlshift.commons.stream.StreamMessage;
import com.ctrlshift.commons.util.CompletionActions;
import com.ctrlshift.commons.util.Exceptions;
import com.ctrlshift.commons.util.SafeCloseable;
import com.ctrlshift.internal.AbstractHttp2ConnectionHandler;
import com.ctrlshift.internal.ArmeriaHttpUtil;
import com.ctrlshift.internal.Http1ObjectEncoder;
import com.ctrlshift.internal.Http2ObjectEncoder;
import com.ctrlshift.internal.HttpObjectEncoder;
import com.ctrlshift.internal.PathAndQuery;
import com.ctrlshift.internal.logging.LoggingUtil;

import io.micrometer.core.instrument.MeterRegistry;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import io.netty.handler.ssl.SslHandler;

final class HttpServerHandler extends ChannelInboundHandlerAdapter implements HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    private static final MediaType ERROR_CONTENT_TYPE = MediaType.PLAIN_TEXT_UTF_8;

    private static final Set<HttpMethod> ALLOWED_METHODS =
            Sets.immutableEnumSet(HttpMethod.DELETE, HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS,
                                  HttpMethod.PATCH, HttpMethod.POST, HttpMethod.PUT, HttpMethod.TRACE);

    private static final String ALLOWED_METHODS_STRING =
            ALLOWED_METHODS.stream().map(HttpMethod::name).collect(Collectors.joining(","));

    private static final ChannelFutureListener CLOSE = future -> {
        final Throwable cause = future.cause();
        final Channel ch = future.channel();
        if (cause != null) {
            logException(ch, cause);
        }
        safeClose(ch);
    };

    static final ChannelFutureListener CLOSE_ON_FAILURE = future -> {
        final Throwable cause = future.cause();
        if (cause != null && !(cause instanceof ClosedPublisherException)) {
            final Channel ch = future.channel();
            logException(ch, cause);
            safeClose(ch);
        }
    };

    private static void logException(Channel ch, Throwable cause) {
        final HttpServer server = HttpServer.get(ch);
        if (server != null) {
            Exceptions.logIfUnexpected(logger, ch, server.protocol(), cause);
        } else {
            Exceptions.logIfUnexpected(logger, ch, cause);
        }
    }

    static void safeClose(Channel ch) {
        if (!ch.isActive()) {
            return;
        }

        // Do not call Channel.close() if AbstractHttp2ConnectionHandler.close() has been invoked
        // already. Otherwise, it can trigger a bad cycle:
        //
        //   1. Channel.close() triggers AbstractHttp2ConnectionHandler.close().
        //   2. AbstractHttp2ConnectionHandler.close() triggers Http2Stream.close().
        //   3. Http2Stream.close() fails the promise of its pending writes.
        //   4. The failed promise notifies this listener (CLOSE_ON_FAILURE).
        //   5. This listener calls Channel.close().
        //   6. Repeat from step 1.
        //

        final AbstractHttp2ConnectionHandler h2handler =
                ch.pipeline().get(AbstractHttp2ConnectionHandler.class);

        if (h2handler == null || !h2handler.isClosing()) {
            ch.close();
        }
    }

    private final ServerConfig config;
    private final GracefulShutdownSupport gracefulShutdownSupport;

    private SessionProtocol protocol;
    @Nullable
    private HttpObjectEncoder responseEncoder;

    private int unfinishedRequests;
    private boolean isReading;
    private boolean handledLastRequest;

    private final Consumer<RequestLog> accessLogWriter;
    private final IdentityHashMap<HttpResponse, Boolean> unfinishedResponses;

    HttpServerHandler(ServerConfig config,
                      GracefulShutdownSupport gracefulShutdownSupport,
                      SessionProtocol protocol) {

        assert protocol == H1 || protocol == H1C || protocol == H2;

        this.config = requireNonNull(config, "config");
        this.gracefulShutdownSupport = requireNonNull(gracefulShutdownSupport, "gracefulShutdownSupport");

        this.protocol = requireNonNull(protocol, "protocol");
        if (protocol == H1 || protocol == H1C) {
            responseEncoder = new Http1ObjectEncoder(true, protocol.isTls());
        }

        unfinishedResponses = new IdentityHashMap<>();
        accessLogWriter = config.accessLogWriter();
    }

    @Override
    public SessionProtocol protocol() {
        return protocol;
    }

    @Override
    public int unfinishedRequests() {
        return unfinishedRequests;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        unfinishedResponses.keySet().forEach(StreamMessage::abort);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (responseEncoder != null) {
            responseEncoder.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        isReading = true; // Cleared in channelReadComplete()

        if (msg instanceof Http2Settings) {
            handleHttp2Settings(ctx, (Http2Settings) msg);
        } else {
            handleRequest(ctx, (DecodedHttpRequest) msg);
        }
    }

    private void handleHttp2Settings(ChannelHandlerContext ctx, Http2Settings h2settings) {
        if (h2settings.isEmpty()) {
            logger.trace("{} HTTP/2 settings: <empty>", ctx.channel());
        } else {
            logger.debug("{} HTTP/2 settings: {}", ctx.channel(), h2settings);
        }

        if (protocol == H1) {
            protocol = H2;
        } else if (protocol == H1C) {
            protocol = H2C;
        }

        final Http2ConnectionHandler handler = ctx.pipeline().get(Http2ConnectionHandler.class);
        if (responseEncoder == null) {
            responseEncoder = new Http2ObjectEncoder(handler.encoder());
        } else if (responseEncoder instanceof Http1ObjectEncoder) {
            responseEncoder.close();
            responseEncoder = new Http2ObjectEncoder(handler.encoder());
        }
    }

    private void handleRequest(ChannelHandlerContext ctx, DecodedHttpRequest req) throws Exception {
        // Ignore the request received after the last request,
        // because we are going to close the connection after sending the last response.
        if (handledLastRequest) {
            return;
        }

        // If we received the message with keep-alive disabled,
        // we should not accept a request anymore.
        if (!req.isKeepAlive()) {
            handledLastRequest = true;
        }

        final HttpHeaders headers = req.headers();
        if (!ALLOWED_METHODS.contains(headers.method())) {
            respond(ctx, req, HttpStatus.METHOD_NOT_ALLOWED,
                    new IllegalArgumentException("Request method is not allowed: " +
                                                 headers.method().name()));
            return;
        }

        // Handle 'OPTIONS * HTTP/1.1'.
        final String originalPath = headers.path();
        if (originalPath == null) {
            respond(ctx, req, HttpStatus.BAD_REQUEST,
                    new IllegalArgumentException("Request path is missing."));
            return;
        }
        if (originalPath.isEmpty() || originalPath.charAt(0) != '/') {
            if (headers.method() == HttpMethod.OPTIONS && "*".equals(originalPath)) {
                handleOptions(ctx, req);
            } else {
                respond(ctx, req, HttpStatus.BAD_REQUEST,
                        new IllegalArgumentException("Request path is invalid: " + originalPath));
            }
            return;
        }

        // Validate and split path and query.
        final PathAndQuery pathAndQuery = PathAndQuery.parse(originalPath);
        if (pathAndQuery == null) {
            // Reject requests without a valid path.
            respond(ctx, req, HttpStatus.NOT_FOUND,
                    new IllegalArgumentException("Request path is invalid: " + originalPath));
            return;
        }

        final String hostname = hostname(ctx, headers);
        final VirtualHost host = config.findVirtualHost(hostname);

        final PathMappingContext mappingCtx = DefaultPathMappingContext.of(
                host, hostname, pathAndQuery.path(), pathAndQuery.query(), headers,
                host.producibleMediaTypes());
        // Find the service that matches the path.
        final PathMapped<ServiceConfig> mapped;
        try {
            mapped = host.findServiceConfig(mappingCtx);
        } catch (HttpStatusException cause) {
            // We do not need to handle HttpResponseException here because we do not use it internally.
            respond(ctx, req, pathAndQuery, cause.httpStatus(), cause);
            return;
        } catch (Throwable cause) {
            logger.warn("{} Unexpected exception: {}", ctx.channel(), req, cause);
            respond(ctx, req, pathAndQuery, HttpStatus.INTERNAL_SERVER_ERROR, cause);
            return;
        }
        if (!mapped.isPresent()) {
            // No services matched the path.
            handleNonExistentMapping(ctx, req, host, pathAndQuery, mappingCtx);
            return;
        }

        // Decode the request and create a new invocation context from it to perform an invocation.
        final PathMappingResult mappingResult = mapped.mappingResult();
        final ServiceConfig serviceCfg = mapped.value();
        final Service<HttpRequest, HttpResponse> service = serviceCfg.service();

        final Channel channel = ctx.channel();
        final DefaultServiceRequestContext reqCtx = new DefaultServiceRequestContext(
                serviceCfg, channel, serviceCfg.server().meterRegistry(),
                protocol, mappingCtx, mappingResult, req, getSSLSession(channel));

        try (SafeCloseable ignored = RequestContext.push(reqCtx)) {
            final RequestLogBuilder logBuilder = reqCtx.logBuilder();
            HttpResponse serviceResponse;
            try {
                req.init(reqCtx);
                serviceResponse = service.serve(reqCtx, req);
            } catch (HttpResponseException cause) {
                serviceResponse = cause.httpResponse();
            } catch (Throwable cause) {
                try {
                    if (cause instanceof HttpStatusException) {
                        respond(ctx, req, ((HttpStatusException) cause).httpStatus(), reqCtx, cause);
                    } else {
                        logger.warn("{} Unexpected exception: {}, {}", reqCtx, service, req, cause);
                        respond(ctx, req, HttpStatus.INTERNAL_SERVER_ERROR, reqCtx, cause);
                    }
                } finally {
                    logBuilder.endRequest(cause);
                    logBuilder.endResponse(cause);
                }
                return;
            }
            final HttpResponse res = serviceResponse;

            final EventLoop eventLoop = channel.eventLoop();

            // Keep track of the number of unfinished requests and
            // clean up the request stream when response stream ends.
            gracefulShutdownSupport.inc();
            unfinishedRequests++;
            unfinishedResponses.put(res, true);

            if (service.shouldCachePath(pathAndQuery.path(), pathAndQuery.query(), mapped.mapping())) {
                reqCtx.log().addListener(log -> {
                    final HttpStatus status = log.responseHeaders().status();
                    if (status != null && status.code() >= 200 && status.code() < 400) {
                        pathAndQuery.storeInCache(originalPath);
                    }
                }, RequestLogAvailability.COMPLETE);
            }

            req.completionFuture().handle(voidFunction((ret, cause) -> {
                if (cause == null) {
                    logBuilder.endRequest();
                } else {
                    logBuilder.endRequest(cause);
                }
            })).exceptionally(CompletionActions::log);

            res.completionFuture().handle(voidFunction((ret, cause) -> {
                req.abort();
                // NB: logBuilder.endResponse() is called by HttpResponseSubscriber below.
                eventLoop.execute(() -> {
                    gracefulShutdownSupport.dec();
                    unfinishedResponses.remove(res);
                    if (--unfinishedRequests == 0 && handledLastRequest) {
                        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(CLOSE);
                    }
                });
            })).exceptionally(CompletionActions::log);

            assert responseEncoder != null;
            final HttpResponseSubscriber resSubscriber =
                    new HttpResponseSubscriber(ctx, responseEncoder, reqCtx, req, accessLogWriter);
            reqCtx.setRequestTimeoutChangeListener(resSubscriber);
            res.subscribe(resSubscriber, eventLoop, true);
        }
    }

    private void handleOptions(ChannelHandlerContext ctx, DecodedHttpRequest req) {
        respond(ctx, req,
                AggregatedHttpMessage.of(
                        HttpHeaders.of(HttpStatus.OK)
                                   .set(HttpHeaderNames.ALLOW, ALLOWED_METHODS_STRING)),
                newEarlyRespondingRequestContext(ctx, req, req.path(), null), null);
    }

    private void handleNonExistentMapping(ChannelHandlerContext ctx, DecodedHttpRequest req,
                                          VirtualHost host, PathAndQuery pathAndQuery,
                                          PathMappingContext mappingCtx) {

        final String path = mappingCtx.path();
        if (path.charAt(path.length() - 1) != '/') {
            // Handle the case where /path doesn't exist but /path/ exists.
            final String pathWithSlash = path + '/';
            if (host.findServiceConfig(mappingCtx.overridePath(pathWithSlash)).isPresent()) {
                final String location;
                final String originalPath = req.path();
                if (path.length() == originalPath.length()) {
                    location = pathWithSlash;
                } else {
                    location = pathWithSlash + originalPath.substring(path.length());
                }
                redirect(ctx, req, pathAndQuery, location);
                return;
            }
        }

        respond(ctx, req, HttpStatus.NOT_FOUND, null);
    }

    private String hostname(ChannelHandlerContext ctx, HttpHeaders headers) {
        final String hostname = headers.authority();
        if (hostname == null) {
            // Fill the authority with the default host name and current port, just in case the client did not
            // send it.
            final String defaultHostname = config.defaultVirtualHost().defaultHostname();
            final int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
            headers.authority(defaultHostname + ':' + port);
            return defaultHostname;
        }

        final int hostnameColonIdx = hostname.lastIndexOf(':');
        if (hostnameColonIdx < 0) {
            return hostname;
        }

        return hostname.substring(0, hostnameColonIdx);
    }

    private void redirect(ChannelHandlerContext ctx, DecodedHttpRequest req,
                          PathAndQuery pathAndQuery, String location) {
        respond(ctx, req,
                AggregatedHttpMessage.of(
                        HttpHeaders.of(HttpStatus.TEMPORARY_REDIRECT)
                                   .set(HttpHeaderNames.LOCATION, location)),
                newEarlyRespondingRequestContext(ctx, req, pathAndQuery.path(), pathAndQuery.query()),
                null);
    }

    private void respond(ChannelHandlerContext ctx, DecodedHttpRequest req,
                         HttpStatus status, @Nullable Throwable cause) {
        respond(ctx, req, status,
                newEarlyRespondingRequestContext(ctx, req, req.path(), null), cause);
    }

    private void respond(ChannelHandlerContext ctx, DecodedHttpRequest req, PathAndQuery pathAndQuery,
                         HttpStatus status, @Nullable Throwable cause) {
        respond(ctx, req, status,
                newEarlyRespondingRequestContext(ctx, req, pathAndQuery.path(), pathAndQuery.query()), cause);
    }

    private void respond(ChannelHandlerContext ctx, DecodedHttpRequest req, HttpStatus status,
                         RequestContext reqCtx, @Nullable Throwable cause) {

        if (status.code() < 400) {
            respond(ctx, req, AggregatedHttpMessage.of(HttpHeaders.of(status)), reqCtx, cause);
            return;
        }

        final HttpData content;
        if (req.method() == HttpMethod.HEAD || ArmeriaHttpUtil.isContentAlwaysEmpty(status)) {
            content = HttpData.EMPTY_DATA;
        } else {
            content = status.toHttpData();
        }

        respond(ctx, req,
                AggregatedHttpMessage.of(
                        HttpHeaders.of(status)
                                   .contentType(ERROR_CONTENT_TYPE),
                        content), reqCtx, cause);
    }

    private void respond(ChannelHandlerContext ctx, DecodedHttpRequest req, AggregatedHttpMessage res,
                         RequestContext reqCtx, @Nullable Throwable cause) {
        if (!handledLastRequest) {
            addKeepAliveHeaders(req, res);
            respond0(ctx, req, res, reqCtx, cause).addListener(CLOSE_ON_FAILURE);
        } else {
            // Note that it is perfectly fine not to set the 'content-length' header to the last response
            // of an HTTP/1 connection. We set it anyway to work around overly strict HTTP clients that always
            // require a 'content-length' header for non-chunked responses.
            setContentLength(req, res);
            respond0(ctx, req, res, reqCtx, cause).addListener(CLOSE);
        }

        if (!isReading) {
            ctx.flush();
        }
    }

    private ChannelFuture respond0(ChannelHandlerContext ctx,
                                   DecodedHttpRequest req, AggregatedHttpMessage res,
                                   RequestContext reqCtx, @Nullable Throwable cause) {

        // No need to consume further since the response is ready.
        req.close();

        final boolean trailingHeadersEmpty = res.trailingHeaders().isEmpty();
        final boolean contentAndTrailingHeadersEmpty = res.content().isEmpty() && trailingHeadersEmpty;

        final RequestLogBuilder logBuilder = reqCtx.logBuilder();

        logBuilder.startResponse();
        assert responseEncoder != null;
        ChannelFuture future = responseEncoder.writeHeaders(
                ctx, req.id(), req.streamId(), res.headers(), contentAndTrailingHeadersEmpty);
        logBuilder.responseHeaders(res.headers());
        if (!contentAndTrailingHeadersEmpty) {
            future = responseEncoder.writeData(
                    ctx, req.id(), req.streamId(), res.content(), trailingHeadersEmpty);
            logBuilder.increaseResponseLength(res.content().length());
            if (!trailingHeadersEmpty) {
                future = responseEncoder.writeHeaders(
                        ctx, req.id(), req.streamId(), res.trailingHeaders(), true);
            }
        }

        future.addListener(f -> {
            if (cause == null && f.isSuccess()) {
                logBuilder.endResponse();
            } else {
                // Respect the first specified cause.
                logBuilder.endResponse(firstNonNull(cause, f.cause()));
            }
            accessLogWriter.accept(reqCtx.log());
        });
        return future;
    }

    /**
     * Sets the keep alive header as per:
     * - https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
     */
    private void addKeepAliveHeaders(HttpRequest req, AggregatedHttpMessage res) {
        if (protocol == H1 || protocol == H1C) {
            res.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
        } else {
            // Do not add the 'connection' header for HTTP/2 responses.
            // See https://tools.ietf.org/html/rfc7540#section-8.1.2.2
        }

        setContentLength(req, res);
    }

    /**
     * Sets the 'content-length' header to the response.
     */
    private static void setContentLength(HttpRequest req, AggregatedHttpMessage res) {
        // https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.4
        // prohibits to send message body for below cases.
        // and in those cases, content should be empty.
        if (req.method() == HttpMethod.HEAD || ArmeriaHttpUtil.isContentAlwaysEmpty(res.status())) {
            return;
        }
        res.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, res.content().length());
    }

    @Nullable
    private static SSLSession getSSLSession(Channel channel) {
        final SslHandler sslHandler = channel.pipeline().get(SslHandler.class);
        return sslHandler != null ? sslHandler.engine().getSession() : null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        isReading = false;
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof SslCloseCompletionEvent ||
            evt instanceof ChannelInputShutdownReadComplete) {
            // Expected events
            return;
        }

        logger.warn("{} Unexpected user event: {}", ctx.channel(), evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Exceptions.logIfUnexpected(logger, ctx.channel(), protocol, cause);
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }

    private EarlyRespondingRequestContext newEarlyRespondingRequestContext(ChannelHandlerContext ctx,
                                                                           DecodedHttpRequest req,
                                                                           String path,
                                                                           @Nullable String query) {
        final Channel channel = ctx.channel();
        final EarlyRespondingRequestContext reqCtx =
                new EarlyRespondingRequestContext(channel, NoopMeterRegistry.get(), protocol(),
                                                  req.method(), path, query, req);
        final String host = LoggingUtil.remoteHost(req.headers(), ctx.channel());

        final RequestLogBuilder logBuilder = reqCtx.logBuilder();
        logBuilder.startRequest(channel, protocol(), host);
        logBuilder.requestHeaders(req.headers());

        return reqCtx;
    }

    private static final class EarlyRespondingRequestContext extends NonWrappingRequestContext {

        private final Channel channel;
        private final DefaultRequestLog requestLog;

        EarlyRespondingRequestContext(Channel channel, MeterRegistry meterRegistry,
                                      SessionProtocol sessionProtocol, HttpMethod method, String path,
                                      @Nullable String query, Request request) {
            super(meterRegistry, sessionProtocol, method, path, query, request);
            this.channel = requireNonNull(channel, "channel");
            requestLog = new DefaultRequestLog(this);
        }

        @Override
        public RequestContext newDerivedContext() {
            return newDerivedContext(request());
        }

        @Override
        public RequestContext newDerivedContext(Request request) {
            // There are no attributes which should be copied to a new instance.
            return new EarlyRespondingRequestContext(channel, meterRegistry(), sessionProtocol(),
                                                     method(), path(), query(), request);
        }

        @Override
        protected Channel channel() {
            return channel;
        }

        @Override
        public RequestLog log() {
            return requestLog;
        }

        @Override
        public RequestLogBuilder logBuilder() {
            return requestLog;
        }

        @Override
        public EventLoop eventLoop() {
            return channel.eventLoop();
        }
    }
}