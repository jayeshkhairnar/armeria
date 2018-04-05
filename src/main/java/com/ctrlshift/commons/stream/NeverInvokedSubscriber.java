package com.ctrlshift.commons.stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

final class NeverInvokedSubscriber<T> implements Subscriber<T> {

    private static final NeverInvokedSubscriber<Object> INSTANCE = new NeverInvokedSubscriber<>();

    @SuppressWarnings("unchecked")
    static <T> NeverInvokedSubscriber<T> get() {
        return (NeverInvokedSubscriber<T>) INSTANCE;
    }

    @Override
    public void onSubscribe(Subscription s) {
        throw new IllegalStateException("onSubscribe(" + s + ')');
    }

    @Override
    public void onNext(T t) {
        throw new IllegalStateException("onNext(" + t + ')');
    }

    @Override
    public void onError(Throwable t) {
        throw new IllegalStateException("onError(" + t + ')', t);
    }

    @Override
    public void onComplete() {
        throw new IllegalStateException("onComplete()");
    }
}