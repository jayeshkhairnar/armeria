package com.ctrlshift.commons.stream;

import org.reactivestreams.Subscriber;

import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.util.Exceptions;

/**
 * A {@link RuntimeException} that is raised to signal a {@link Subscriber} that the {@link StreamMessage}
 * it subscribed to has been aborted by {@link StreamMessage#abort()}.
 */
public final class AbortedStreamException extends RuntimeException {

    private static final long serialVersionUID = -5271590540551141199L;

    private static final AbortedStreamException INSTANCE =
            Exceptions.clearTrace(new AbortedStreamException());

    /**
     * Returns a {@link AbortedStreamException} which may be a singleton or a new instance, depending on
     * whether {@link Flags#verboseExceptions() the verbose exception mode} is enabled.
     */
    public static AbortedStreamException get() {
        return Flags.verboseExceptions() ? new AbortedStreamException() : INSTANCE;
    }

    private AbortedStreamException() {}
}