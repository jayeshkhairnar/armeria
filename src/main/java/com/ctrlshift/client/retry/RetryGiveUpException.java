package com.ctrlshift.client.retry;

import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.util.Exceptions;

/**
 * A {@link RuntimeException} that is raised when a {@link RetryingClient} gives up retrying due to the
 * result of {@link Backoff#nextDelayMillis(int)}.
 */
public final class RetryGiveUpException extends RuntimeException {

    private static final long serialVersionUID = -3816065469543230534L;

    private static final RetryGiveUpException INSTANCE = Exceptions.clearTrace(new RetryGiveUpException());

    /**
     * Returns a {@link RetryGiveUpException} which may be a singleton or a new instance, depending
     * on whether {@link Flags#verboseExceptions() the verbose exception mode} is enabled.
     */
    public static RetryGiveUpException get() {
        return Flags.verboseExceptions() ? new RetryGiveUpException() : INSTANCE;
    }

    private RetryGiveUpException() {}
}