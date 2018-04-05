package com.ctrlshift.commons;

public class TimeoutException extends RuntimeException {
    private static final long serialVersionUID = 2887898788270995289L;

    /**
     * Creates a new exception.
     */
    public TimeoutException() {}

    /**
     * Creates a new instance with the specified {@code message}.
     */
    public TimeoutException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with the specified {@code message} and {@code cause}.
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance with the specified {@code cause}.
     */
    public TimeoutException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance with the specified {@code message}, {@code cause}, suppression enabled or
     * disabled, and writable stack trace enabled or disabled.
     */
    protected TimeoutException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
