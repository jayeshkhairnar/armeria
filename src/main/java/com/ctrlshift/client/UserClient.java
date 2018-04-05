package com.ctrlshift.client;

import java.net.URI;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpMethod;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.RequestContext;
import com.ctrlshift.commons.Response;
import com.ctrlshift.commons.SessionProtocol;
import com.ctrlshift.commons.logging.RequestLogAvailability;
import com.ctrlshift.commons.util.ReleasableHolder;
import com.ctrlshift.commons.util.SafeCloseable;

import io.micrometer.core.instrument.MeterRegistry;
import io.netty.channel.EventLoop;

/**
 * A base class for implementing a user's entry point for sending a {@link Request}.
 *
 * <p>It provides the utility methods for easily forwarding a {@link Request} from a user to a {@link Client}.
 *
 * <p>Note that this class is not a subtype of {@link Client}, although its name may mislead.
 *
 * @param <I> the request type
 * @param <O> the response type
 */
public abstract class UserClient<I extends Request, O extends Response> implements ClientBuilderParams {

    private final ClientBuilderParams params;
    private final Client<I, O> delegate;
    private final MeterRegistry meterRegistry;
    private final SessionProtocol sessionProtocol;
    private final Endpoint endpoint;

    /**
     * Creates a new instance.
     *
     * @param params the parameters used for constructing the client
     * @param delegate the {@link Client} that will process {@link Request}s
     * @param meterRegistry the {@link MeterRegistry} that collects various stats
     * @param sessionProtocol the {@link SessionProtocol} of the {@link Client}
     * @param endpoint the {@link Endpoint} of the {@link Client}
     */
    protected UserClient(ClientBuilderParams params, Client<I, O> delegate, MeterRegistry meterRegistry,
                         SessionProtocol sessionProtocol, Endpoint endpoint) {
        this.params = params;
        this.delegate = delegate;
        this.meterRegistry = meterRegistry;
        this.sessionProtocol = sessionProtocol;
        this.endpoint = endpoint;
    }

    @Override
    public ClientFactory factory() {
        return params.factory();
    }

    @Override
    public URI uri() {
        return params.uri();
    }

    @Override
    public Class<?> clientType() {
        return params.clientType();
    }

    @Override
    public final ClientOptions options() {
        return params.options();
    }

    /**
     * Returns the {@link Client} that will process {@link Request}s.
     */
    @SuppressWarnings("unchecked")
    protected final <U extends Client<I, O>> U delegate() {
        return (U) delegate;
    }

    /**
     * Returns the {@link SessionProtocol} of the {@link #delegate()}.
     */
    protected final SessionProtocol sessionProtocol() {
        return sessionProtocol;
    }

    /**
     * Returns the {@link Endpoint} of the {@link #delegate()}.
     */
    protected final Endpoint endpoint() {
        return endpoint;
    }

    /**
     * Executes the specified {@link Request} via {@link #delegate()}.
     *
     * @param method the method of the {@link Request}
     * @param path the path part of the {@link Request} URI
     * @param query the query part of the {@link Request} URI
     * @param fragment the fragment part of the {@link Request} URI
     * @param req the {@link Request}
     * @param fallback the fallback response {@link Function} to use when
     *                 {@link Client#execute(ClientRequestContext, Request)} of {@link #delegate()} throws
     *                 an exception instead of returning an error response
     */
    protected final O execute(HttpMethod method, String path, @Nullable String query, @Nullable String fragment,
                              I req, Function<Throwable, O> fallback) {
        return execute(null, method, path, query, fragment, req, fallback);
    }

    /**
     * Executes the specified {@link Request} via {@link #delegate()}.
     *
     * @param eventLoop the {@link EventLoop} to execute the {@link Request}
     * @param method the method of the {@link Request}
     * @param path the path part of the {@link Request} URI
     * @param query the query part of the {@link Request} URI
     * @param fragment the fragment part of the {@link Request} URI
     * @param req the {@link Request}
     * @param fallback the fallback response {@link Function} to use when
     *                 {@link Client#execute(ClientRequestContext, Request)} of {@link #delegate()} throws
     */
    protected final O execute(@Nullable EventLoop eventLoop,
                              HttpMethod method, String path, @Nullable String query, @Nullable String fragment,
                              I req, Function<Throwable, O> fallback) {

        final ClientRequestContext ctx;
        if (eventLoop == null) {
            final ReleasableHolder<EventLoop> releasableEventLoop = factory().acquireEventLoop(endpoint);
            ctx = new DefaultClientRequestContext(
                    releasableEventLoop.get(), meterRegistry, sessionProtocol, endpoint,
                    method, path, query, fragment, options(), req);
            ctx.log().addListener(log -> releasableEventLoop.release(), RequestLogAvailability.COMPLETE);
        } else {
            ctx = new DefaultClientRequestContext(eventLoop, meterRegistry, sessionProtocol, endpoint,
                                                  method, path, query, fragment, options(), req);
        }

        try (SafeCloseable ignored = RequestContext.push(ctx)) {
            return delegate().execute(ctx, req);
        } catch (Throwable cause) {
            ctx.logBuilder().endResponse(cause);
            return fallback.apply(cause);
        }
    }
}
