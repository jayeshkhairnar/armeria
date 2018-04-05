package com.ctrlshift.commons.stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.util.Exceptions;

/**
 * A {@link RuntimeException} that is raised to notify {@link StreamMessage#completionFuture()} when a
 * {@link Subscriber} has cancelled its {@link Subscription}.
 */
public final class CancelledSubscriptionException extends RuntimeException {

    private static final long serialVersionUID = -7815958463104921571L;

    private static final CancelledSubscriptionException INSTANCE =
            Exceptions.clearTrace(new CancelledSubscriptionException());

    /**
     * Returns a {@link CancelledSubscriptionException} which may be a singleton or a new instance, depending
     * on whether {@link Flags#verboseExceptions() the verbose exception mode} is enabled.
     */
    public static CancelledSubscriptionException get() {
        return Flags.verboseExceptions() ? new CancelledSubscriptionException() : INSTANCE;
    }

    private CancelledSubscriptionException() {}
}