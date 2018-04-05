package com.ctrlshift.server;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrlshift.commons.ClosedSessionException;
import com.ctrlshift.commons.ContentTooLargeException;
import com.ctrlshift.commons.HttpMethod;
import com.ctrlshift.commons.MediaType;
import com.ctrlshift.commons.ProtocolViolationException;
import com.ctrlshift.internal.ArmeriaHttpUtil;
import com.ctrlshift.internal.InboundTrafficController;
import com.ctrlshift.unsafe.ByteBufHttpData;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeEvent;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.HttpConversionUtil.ExtensionHeaderNames;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;

final class Http1RequestDecoder extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(Http1RequestDecoder.class);

    private static final Http2Settings DEFAULT_HTTP2_SETTINGS = new Http2Settings();

    private final ServerConfig cfg;
    private final AsciiString scheme;
    private final InboundTrafficController inboundTrafficController;

    /** The request being decoded currently. */
    @Nullable
    private DecodedHttpRequest req;
    private int receivedRequests;
    private int sentResponses;
    private boolean discarding;

    Http1RequestDecoder(ServerConfig cfg, Channel channel, AsciiString scheme) {
        this.cfg = cfg;
        this.scheme = scheme;
        inboundTrafficController = new InboundTrafficController(channel);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (req != null) {
            // Ignored if the stream has already been closed.
            req.close(ClosedSessionException.get());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HttpObject)) {
            ctx.fireChannelRead(msg);
            return;
        }

        // this.req can be set to null by fail(), so we keep it in a local variable.
        DecodedHttpRequest req = this.req;
        try {
            if (discarding) {
                return;
            }

            if (req == null) {
                if (msg instanceof HttpRequest) {
                    final HttpRequest nettyReq = (HttpRequest) msg;
                    if (!nettyReq.decoderResult().isSuccess()) {
                        fail(ctx, HttpResponseStatus.BAD_REQUEST);
                        return;
                    }

                    final HttpHeaders nettyHeaders = nettyReq.headers();
                    final int id = ++receivedRequests;

                    // Validate the method.
                    if (!HttpMethod.isSupported(nettyReq.method().name())) {
                        fail(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
                        return;
                    }

                    // Validate the 'content-length' header.
                    final String contentLengthStr = nettyHeaders.get(HttpHeaderNames.CONTENT_LENGTH);
                    final boolean contentEmpty;
                    if (contentLengthStr != null) {
                        final long contentLength;
                        try {
                            contentLength = Long.parseLong(contentLengthStr);
                        } catch (NumberFormatException ignored) {
                            fail(ctx, HttpResponseStatus.BAD_REQUEST);
                            return;
                        }
                        if (contentLength < 0) {
                            fail(ctx, HttpResponseStatus.BAD_REQUEST);
                            return;
                        }

                        contentEmpty = contentLength == 0;
                    } else {
                        contentEmpty = true;
                    }

                    nettyHeaders.set(ExtensionHeaderNames.SCHEME.text(), scheme);

                    this.req = req = new DecodedHttpRequest(
                            ctx.channel().eventLoop(),
                            id, 1,
                            ArmeriaHttpUtil.toArmeria(nettyReq),
                            HttpUtil.isKeepAlive(nettyReq),
                            inboundTrafficController,
                            cfg.defaultMaxRequestLength());

                    // Close the request early when it is sure that there will be
                    // neither content nor trailing headers.
                    if (contentEmpty && !HttpUtil.isTransferEncodingChunked(nettyReq)) {
                        req.close();
                    }

                    ctx.fireChannelRead(req);
                } else {
                    fail(ctx, HttpResponseStatus.BAD_REQUEST);
                    return;
                }
            }

            if (req != null && msg instanceof HttpContent) {
                final HttpContent content = (HttpContent) msg;
                final DecoderResult decoderResult = content.decoderResult();
                if (!decoderResult.isSuccess()) {
                    fail(ctx, HttpResponseStatus.BAD_REQUEST);
                    req.close(new ProtocolViolationException(decoderResult.cause()));
                    return;
                }

                final ByteBuf data = content.content();
                final int dataLength = data.readableBytes();
                if (dataLength != 0) {
                    req.increaseTransferredBytes(dataLength);
                    final long maxContentLength = req.maxRequestLength();
                    if (maxContentLength > 0 && req.transferredBytes() > maxContentLength) {
                        fail(ctx, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
                        req.close(ContentTooLargeException.get());
                        return;
                    }

                    if (req.isOpen()) {
                        req.write(new ByteBufHttpData(data.retain(), false));
                    }
                }

                if (msg instanceof LastHttpContent) {
                    final HttpHeaders trailingHeaders = ((LastHttpContent) msg).trailingHeaders();
                    if (!trailingHeaders.isEmpty()) {
                        req.write(ArmeriaHttpUtil.toArmeria(trailingHeaders));
                    }

                    req.close();
                    this.req = req = null;
                }
            }
        } catch (URISyntaxException e) {
            fail(ctx, HttpResponseStatus.BAD_REQUEST);
            if (req != null) {
                req.close(e);
            }
        } catch (Throwable t) {
            fail(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            if (req != null) {
                req.close(t);
            } else {
                logger.warn("Unexpected exception:", t);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void fail(ChannelHandlerContext ctx, HttpResponseStatus status) {
        discarding = true;
        req = null;

        final ChannelFuture future;
        if (receivedRequests <= sentResponses) {
            // Just close the connection if sending an error response will make the number of the sent
            // responses exceed the number of the received requests, which doesn't make sense.
            future = ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
        } else {
            final ByteBuf content = Unpooled.copiedBuffer(status.toString(), StandardCharsets.UTF_8);
            final FullHttpResponse res = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, status, content);

            final HttpHeaders headers = res.headers();
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            headers.set(HttpHeaderNames.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8);
            headers.setInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            future = ctx.writeAndFlush(res);
        }

        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof UpgradeEvent) {
            // Generate the initial Http2Settings frame,
            // so that the next handler knows the protocol upgrade occurred as well.
            ctx.fireChannelRead(DEFAULT_HTTP2_SETTINGS);

            // Continue handling the upgrade request after the upgrade is complete.
            final FullHttpRequest nettyReq = ((UpgradeEvent) evt).upgradeRequest();

            // Remove the headers related with the upgrade.
            nettyReq.headers().remove(HttpHeaderNames.CONNECTION);
            nettyReq.headers().remove(HttpHeaderNames.UPGRADE);
            nettyReq.headers().remove(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);

            if (logger.isDebugEnabled()) {
                logger.debug("{} Handling the pre-upgrade request ({}): {} {} {} ({}B)",
                             ctx.channel(), ((UpgradeEvent) evt).protocol(),
                             nettyReq.method(), nettyReq.uri(), nettyReq.protocolVersion(),
                             nettyReq.content().readableBytes());
            }

            channelRead(ctx, nettyReq);
            channelReadComplete(ctx);
            return;
        }

        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof HttpResponse &&
            ((HttpResponse) msg).status().codeClass() != HttpStatusClass.INFORMATIONAL) {
            sentResponses++;
        }
        ctx.write(msg, promise);
    }
}
