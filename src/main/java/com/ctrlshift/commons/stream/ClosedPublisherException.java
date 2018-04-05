package com.ctrlshift.commons.stream;

import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.util.Exceptions;

/**
 * A {@link RuntimeException} that is raised when a {@link StreamWriter} attempts to write an object to a
 * closed {@link StreamMessage}.
 */
public final class ClosedPublisherException extends RuntimeException {

    private static final long serialVersionUID = -7665826869012452735L;

    private static final ClosedPublisherException INSTANCE =
            Exceptions.clearTrace(new ClosedPublisherException());

    /**
     * Returns a {@link ClosedPublisherException} which may be a singleton or a new instance, depending on
     * whether {@link Flags#verboseExceptions() the verbose exception mode} is enabled.
     */
    public static ClosedPublisherException get() {
        return Flags.verboseExceptions() ? new ClosedPublisherException() : INSTANCE;
    }

    private ClosedPublisherException() {}
}
