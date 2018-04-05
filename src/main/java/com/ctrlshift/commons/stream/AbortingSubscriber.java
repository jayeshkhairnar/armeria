package com.ctrlshift.commons.stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

final class AbortingSubscriber<T> implements Subscriber<T> {

    private static final AbortingSubscriber<Object> INSTANCE = new AbortingSubscriber<>();

    @SuppressWarnings("unchecked")
    static <T> AbortingSubscriber<T> get() {
        return (AbortingSubscriber<T>) INSTANCE;
    }

    private AbortingSubscriber() {}

    @Override
    public void onSubscribe(Subscription s) {
        s.cancel();
    }

    @Override
    public void onNext(T o) {}

    @Override
    public void onError(Throwable cause) {}

    @Override
    public void onComplete() {}
}
