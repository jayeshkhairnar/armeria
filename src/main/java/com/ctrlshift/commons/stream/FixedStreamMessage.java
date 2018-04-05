package com.ctrlshift.commons.stream;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import javax.annotation.Nullable;

import org.reactivestreams.Subscriber;



import com.ctrlshift.commons.Flags;

import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * An {@link AbstractStreamMessage} which only publishes a fixed number of objects known at construction time.
 */
abstract class FixedStreamMessage<T> extends AbstractStreamMessage<T> {

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<FixedStreamMessage, SubscriptionImpl>
            subscriptionUpdater = AtomicReferenceFieldUpdater.newUpdater(
            FixedStreamMessage.class, SubscriptionImpl.class, "subscription");

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<FixedStreamMessage, CloseEvent>
            closeEventUpdater = AtomicReferenceFieldUpdater.newUpdater(
            FixedStreamMessage.class, CloseEvent.class, "closeEvent");

    @SuppressWarnings("unused")
    @Nullable
    private volatile SubscriptionImpl subscription; // set only via subscriptionUpdater

    @Nullable
    private volatile CloseEvent closeEvent;

    private int requested;

    abstract void cleanupObjects();

    abstract void doRequest(SubscriptionImpl subscription, long n);

    @Nullable
    final CloseEvent closeEvent() {
        return closeEvent;
    }

    final void cleanup(SubscriptionImpl subscription) {
        final CloseEvent closeEvent = this.closeEvent;
        this.closeEvent = null;
        if (closeEvent != null) {
            notifySubscriberOfCloseEvent(subscription, closeEvent);
            // Close event will cleanup.
            return;
        }
        cleanupObjects();
    }

    final int requested() {
        return requested;
    }

    final void setRequested(int n) {
        requested = n;
    }

    @Override
    final void request(long n) {
        final SubscriptionImpl subscription = this.subscription;
        // A user cannot access subscription without subscribing.
        assert subscription != null;

        if (subscription.needsDirectInvocation()) {
            doRequest(subscription, n);
        } else {
            subscription.executor().execute(() -> doRequest(subscription, n));
        }
    }

    @Override
    final long demand() {
        return requested;
    }

    @Override
    public final boolean isOpen() {
        // Fixed streams are closed on construction.
        return false;
    }

    @Override
    final void subscribe(SubscriptionImpl subscription) {
        final Subscriber<Object> subscriber = subscription.subscriber();
        final Executor executor = subscription.executor();

        if (!subscriptionUpdater.compareAndSet(this, null, subscription)) {
            failLateSubscriber(this.subscription, subscriber);
            return;
        }

        if (subscription.needsDirectInvocation()) {
            subscriber.onSubscribe(subscription);
        } else {
            executor.execute(() -> subscriber.onSubscribe(subscription));
        }
    }

    @Override
    final void notifySubscriberOfCloseEvent(SubscriptionImpl subscription, CloseEvent event) {
        try {
            event.notifySubscriber(subscription, completionFuture());
        } finally {
            subscription.clearSubscriber();
            cleanup(subscription);
        }
    }

    @Override
    final void cancel() {
        cancelOrAbort(true);
    }

    @Override
    public final void abort() {
        final SubscriptionImpl currentSubscription = subscription;
        if (currentSubscription != null) {
            cancelOrAbort(false);
            return;
        }

        final SubscriptionImpl newSubscription = new SubscriptionImpl(
                this, AbortingSubscriber.get(), ImmediateEventExecutor.INSTANCE, false);
        subscriptionUpdater.compareAndSet(this, null, newSubscription);
        cancelOrAbort(false);
    }

    private void cancelOrAbort(boolean cancel) {
        final CloseEvent closeEvent;
        if (cancel) {
            closeEvent = Flags.verboseExceptions() ?
                         new CloseEvent(CancelledSubscriptionException.get()) : CANCELLED_CLOSE;
        } else {
            closeEvent = Flags.verboseExceptions() ?
                         new CloseEvent(AbortedStreamException.get()) : ABORTED_CLOSE;
        }
        if (closeEventUpdater.compareAndSet(this, null, closeEvent)) {
            if (subscription.needsDirectInvocation()) {
                cleanup(subscription);
            } else {
                subscription.executor().execute(() -> cleanup(subscription));
            }
        }
    }
}