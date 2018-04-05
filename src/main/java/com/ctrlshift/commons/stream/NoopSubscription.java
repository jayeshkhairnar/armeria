package com.ctrlshift.commons.stream;

import org.reactivestreams.Subscription;

final class NoopSubscription implements Subscription {

    static final NoopSubscription INSTANCE = new NoopSubscription();

    private NoopSubscription() {}

    @Override
    public void request(long n) {}

    @Override
    public void cancel() {}
}