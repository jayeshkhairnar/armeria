package com.ctrlshift.commons.stream;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Subscriber;

import com.google.common.base.MoreObjects;

import io.netty.util.concurrent.EventExecutor;

/**
 * Wraps a {@link StreamMessage} and forwards its method invocations to {@code delegate}.
 * @param <T> the type of elements
 */
public class StreamMessageWrapper<T> implements StreamMessage<T> {

    private final StreamMessage<? extends T> delegate;

    /**
     * Creates a new instance that wraps a {@code delegate}.
     */
    public StreamMessageWrapper(StreamMessage<? extends T> delegate) {
        requireNonNull(delegate, "delegate");
        this.delegate = delegate;
    }

    /**
     * Returns the {@link StreamMessage} being decorated.
     */
    protected final StreamMessage<? extends T> delegate() {
        return delegate;
    }

    @Override
    public boolean isOpen() {
        return delegate().isOpen();
    }

    @Override
    public boolean isEmpty() {
        return delegate().isEmpty();
    }

    @Override
    public CompletableFuture<Void> completionFuture() {
        return delegate().completionFuture();
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        delegate().subscribe(s);
    }

    @Override
    public void subscribe(Subscriber<? super T> s, boolean withPooledObjects) {
        delegate().subscribe(s, withPooledObjects);
    }

    @Override
    public void subscribe(Subscriber<? super T> s, EventExecutor executor) {
        delegate().subscribe(s, executor);
    }

    @Override
    public void subscribe(Subscriber<? super T> s, EventExecutor executor, boolean withPooledObjects) {
        delegate().subscribe(s, executor, withPooledObjects);
    }

    @Override
    public void abort() {
        delegate().abort();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("delegate", delegate()).toString();
    }
}