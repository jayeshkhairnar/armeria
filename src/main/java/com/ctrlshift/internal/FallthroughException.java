package com.ctrlshift.internal;

import com.ctrlshift.commons.util.Exceptions;
import com.ctrlshift.server.annotation.ExceptionHandlerFunction;
import com.ctrlshift.server.annotation.RequestConverterFunction;
import com.ctrlshift.server.annotation.ResponseConverterFunction;

/**
 * A {@link RuntimeException} raised for falling through to the next something. It would be raised from
 * {@link ExceptionHandlerFunction}, {@link RequestConverterFunction} and/or {@link ResponseConverterFunction}.
 */
public final class FallthroughException extends RuntimeException {

    private static final long serialVersionUID = 3856883467407862925L;

    private static final FallthroughException
            INSTANCE = Exceptions.clearTrace(new FallthroughException());

    /**
     * Returns a singleton {@link FallthroughException}.
     */
    public static FallthroughException get() {
        return INSTANCE;
    }

    /**
     * Creates a new instance.
     */
    private FallthroughException() {}
}
