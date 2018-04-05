package com.ctrlshift.client;

import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.TimeoutException;
import com.ctrlshift.commons.util.Exceptions;

/**
 * A {@link TimeoutException} raised when a response has not been received from a server within timeout.
 */
public final class ResponseTimeoutException extends TimeoutException {

    private static final long serialVersionUID = 2556616197251937869L;

    private static final ResponseTimeoutException INSTANCE =
            Exceptions.clearTrace(new ResponseTimeoutException());

    /**
     * Returns a {@link ResponseTimeoutException} which may be a singleton or a new instance, depending on
     * whether {@link Flags#verboseExceptions() the verbose exception mode} is enabled.
     */
    public static ResponseTimeoutException get() {
        return Flags.verboseExceptions() ? new ResponseTimeoutException() : INSTANCE;
    }

    /**
     * Creates a new instance.
     */
    private ResponseTimeoutException() {}
}
