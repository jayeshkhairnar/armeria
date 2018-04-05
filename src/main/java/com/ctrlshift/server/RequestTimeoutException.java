package com.ctrlshift.server;

import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.TimeoutException;
import com.ctrlshift.commons.util.Exceptions;

/**
 * A {@link TimeoutException} raised when a request has not been received from a client within timeout.
 */
public final class RequestTimeoutException extends TimeoutException {

    private static final long serialVersionUID = 2556616197251937869L;

    private static final RequestTimeoutException INSTANCE =
            Exceptions.clearTrace(new RequestTimeoutException());

    /**
     * Returns a {@link RequestTimeoutException} which may be a singleton or a new instance, depending on
     * whether {@link Flags#verboseExceptions() the verbose exception mode} is enabled.
     */
    public static RequestTimeoutException get() {
        return Flags.verboseExceptions() ? new RequestTimeoutException() : INSTANCE;
    }

    /**
     * Creates a new instance.
     */
    private RequestTimeoutException() {}
}