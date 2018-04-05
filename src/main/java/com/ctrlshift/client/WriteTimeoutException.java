package com.ctrlshift.client;

import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.TimeoutException;
import com.ctrlshift.commons.util.Exceptions;

/**
 * A {@link TimeoutException} raised when a client failed to send a request to the wire within timeout.
 */
public final class WriteTimeoutException extends TimeoutException {

    private static final long serialVersionUID = 2556616197251937869L;

    private static final WriteTimeoutException INSTANCE = Exceptions.clearTrace(new WriteTimeoutException());

    /**
     * Returns a {@link WriteTimeoutException} which may be a singleton or a new instance, depending on
     * whether {@link Flags#verboseExceptions() the verbose exception mode} is enabled.
     */
    public static WriteTimeoutException get() {
        return Flags.verboseExceptions() ? new WriteTimeoutException() : INSTANCE;
    }

    /**
     * Creates a new instance.
     */
    private WriteTimeoutException() {}
}