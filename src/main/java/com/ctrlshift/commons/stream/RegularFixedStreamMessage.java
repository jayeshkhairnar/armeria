package com.ctrlshift.commons.stream;

import org.reactivestreams.Subscriber;

import io.netty.util.ReferenceCountUtil;

/**
 * A {@link FixedStreamMessage} that publishes an arbitrary number of objects. It is recommended to use
 * {@link EmptyFixedStreamMessage}, {@link OneElementFixedStreamMessage}, or
 * {@link TwoElementFixedStreamMessage} when publishing less than three objects.
 */
public class RegularFixedStreamMessage<T> extends FixedStreamMessage<T> {

    private final T[] objs;

    private int fulfilled;

    private boolean inOnNext;

    protected RegularFixedStreamMessage(T[] objs) {
        this.objs = objs.clone();
    }

    @Override
    final void cleanupObjects() {
        while (fulfilled < objs.length) {
            T obj = objs[fulfilled];
            objs[fulfilled++] = null;
            try {
                onRemoval(obj);
            } finally {
                ReferenceCountUtil.safeRelease(obj);
            }
        }
    }

    @Override
    final void doRequest(SubscriptionImpl subscription, long n) {
        final int oldDemand = requested();
        if (oldDemand >= objs.length) {
            // Already enough demand to finish the stream so don't need to do anything.
            return;
        }
        // As objs.length is fixed, we can safely cap the demand to it here.
        if (n >= objs.length) {
            setRequested(objs.length);
        } else {
            // As objs.length is an int, large demand will always fall into the above branch and there is no
            // chance of overflow, so just simply add the demand.
            setRequested((int) Math.min(oldDemand + n, objs.length));
        }
        if (requested() > oldDemand) {
            doNotify(subscription);
        }
    }

    private void doNotify(SubscriptionImpl subscription) {
        if (inOnNext) {
            // Do not let Subscriber.onNext() reenter, because it can lead to weird-looking event ordering
            // for a Subscriber implemented like the following:
            //
            //   public void onNext(Object e) {
            //       subscription.request(1);
            //       ... Handle 'e' ...
            //   }
            //
            // Note that we do not call this method again, because we are already in the notification loop
            // and it will consume the element we've just added in addObjectOrEvent() from the queue as
            // expected.
            //
            // We do not need to worry about synchronizing the access to 'inOnNext' because the subscriber
            // methods must be on the same thread, or synchronized, according to Reactive Streams spec.
            return;
        }

        final Subscriber<Object> subscriber = subscription.subscriber();
        for (;;) {
            if (closeEvent() != null) {
                cleanup(subscription);
                return;
            }

            if (fulfilled == objs.length) {
                notifySubscriberOfCloseEvent(subscription, SUCCESSFUL_CLOSE);
                return;
            }

            final int requested = requested();

            if (fulfilled == requested) {
                break;
            }

            while (fulfilled < requested) {
                if (closeEvent() != null) {
                    cleanup(subscription);
                    return;
                }

                T o = objs[fulfilled];
                objs[fulfilled++] = null;
                o = prepareObjectForNotification(subscription, o);
                inOnNext = true;
                try {
                    subscriber.onNext(o);
                } finally {
                    inOnNext = false;
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}