package com.ctrlshift.commons.logging;

public class RequestLogAvailabilityException extends RuntimeException {

    private static final long serialVersionUID = 565184697223252595L;

    /**
     * Creates a new instance.
     */
    public RequestLogAvailabilityException() {
        super();
    }

    /**
     * Creates a new instance with the specified {@code message}.
     */
    public RequestLogAvailabilityException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with the specified {@code message} and {@code cause}.
     */
    public RequestLogAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance with the specified {@code cause}.
     */
    public RequestLogAvailabilityException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance with the specified {@code message}, {@code cause}, suppression enabled or
     * disabled, and writable stack trace enabled or disabled.
     */
    protected RequestLogAvailabilityException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
