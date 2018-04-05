package com.ctrlshift.commons.stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * A {@link Subscriber} that discards all elements silently.
 */
public final class NoopSubscriber<T> implements Subscriber<T> {

    private static final NoopSubscriber<?> INSTANCE = new NoopSubscriber<>();

    /**
     * Returns a singleton {@link NoopSubscriber}.
     */
    @SuppressWarnings("unchecked")
    public static <T> NoopSubscriber<T> get() {
        return (NoopSubscriber<T>) INSTANCE;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T t) {}

    @Override
    public void onError(Throwable t) {}

    @Override
    public void onComplete() {}
}
