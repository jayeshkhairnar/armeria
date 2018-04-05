package com.ctrlshift.commons;

import static java.util.Objects.requireNonNull;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.net.ssl.SSLSession;

import com.ctrlshift.commons.logging.RequestLog;
import com.ctrlshift.commons.logging.RequestLogBuilder;

import io.micrometer.core.instrument.MeterRegistry;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Wraps an existing {@link RequestContext}.
 *
 * @param <T> the self type
 */
public abstract class RequestContextWrapper<T extends RequestContext> extends AbstractRequestContext {

    private final T delegate;

    /**
     * Creates a new instance.
     */
    protected RequestContextWrapper(T delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    /**
     * Returns the delegate context.
     */
    protected final T delegate() {
        return delegate;
    }

    @Override
    public SessionProtocol sessionProtocol() {
        return delegate().sessionProtocol();
    }

    @Nullable
    @Override
    public <A extends SocketAddress> A remoteAddress() {
        return delegate().remoteAddress();
    }

    @Nullable
    @Override
    public <A extends SocketAddress> A localAddress() {
        return delegate().localAddress();
    }

    @Nullable
    @Override
    public SSLSession sslSession() {
        return delegate().sslSession();
    }

    @Override
    public HttpMethod method() {
        return delegate().method();
    }

    @Override
    public String path() {
        return delegate().path();
    }

    @Override
    public String query() {
        return delegate().query();
    }

    @Override
    public <E extends Request> E request() {
        return delegate().request();
    }

    @Override
    public RequestLog log() {
        return delegate().log();
    }

    @Override
    public RequestLogBuilder logBuilder() {
        return delegate().logBuilder();
    }

    @Override
    public MeterRegistry meterRegistry() {
        return delegate().meterRegistry();
    }

    @Override
    public Iterator<Attribute<?>> attrs() {
        return delegate().attrs();
    }

    @Override
    public EventLoop eventLoop() {
        return delegate().eventLoop();
    }

    @Override
    public void onEnter(Consumer<? super RequestContext> callback) {
        delegate().onEnter(callback);
    }

    @Override
    public void onExit(Consumer<? super RequestContext> callback) {
        delegate().onExit(callback);
    }

    @Override
    public void onChild(BiConsumer<? super RequestContext, ? super RequestContext> callback) {
        delegate().onChild(callback);
    }

    @Override
    public void invokeOnEnterCallbacks() {
        delegate().invokeOnEnterCallbacks();
    }

    @Override
    public void invokeOnExitCallbacks() {
        delegate().invokeOnExitCallbacks();
    }

    @Override
    public void invokeOnChildCallbacks(RequestContext newCtx) {
        delegate().invokeOnChildCallbacks(newCtx);
    }

    @Override
    public <V> Attribute<V> attr(AttributeKey<V> key) {
        return delegate().attr(key);
    }

    @Override
    public <V> boolean hasAttr(AttributeKey<V> key) {
        return delegate().hasAttr(key);
    }

    @Override
    public ByteBufAllocator alloc() {
        return delegate().alloc();
    }
}
