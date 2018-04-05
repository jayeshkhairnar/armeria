package com.ctrlshift.commons.stream;

import javax.annotation.Nullable;

import io.netty.util.ReferenceCountUtil;

/**
 * A {@link FixedStreamMessage} that only publishes one object.
 */
public class OneElementFixedStreamMessage<T> extends FixedStreamMessage<T> {

    @Nullable
    private T obj;

    protected OneElementFixedStreamMessage(T obj) {
        this.obj = obj;
    }

    @Override
    final void cleanupObjects() {
        if (obj != null) {
            try {
                onRemoval(obj);
            } finally {
                ReferenceCountUtil.safeRelease(obj);
            }
            obj = null;
        }
    }

    @Override
    final void doRequest(SubscriptionImpl subscription, long unused) {
        if (requested() != 0) {
            // Already have demand, so don't need to do anything, the current demand will complete the
            // stream.
            return;
        }
        setRequested(1);
        doNotify(subscription);
    }

    @Override
    public final boolean isEmpty() {
        return false;
    }

    private void doNotify(SubscriptionImpl subscription) {
        // Only called with correct demand, so no need to even check it.
        assert obj != null;
        final T published = prepareObjectForNotification(subscription, obj);
        obj = null;
        // Not possible to have re-entrant onNext with only one item, so no need to keep track of it.
        subscription.subscriber().onNext(published);
        notifySubscriberOfCloseEvent(subscription, SUCCESSFUL_CLOSE);
    }
}
