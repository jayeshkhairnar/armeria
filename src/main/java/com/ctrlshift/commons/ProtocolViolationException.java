package com.ctrlshift.commons;

public class ProtocolViolationException extends RuntimeException {
    private static final long serialVersionUID = 4674394621849790490L;

    /**
     * Creates a new exception.
     */
    public ProtocolViolationException() {}

    /**
     * Creates a new instance with the specified {@code message}.
     */
    public ProtocolViolationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with the specified {@code message} and {@code cause}.
     */
    public ProtocolViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance with the specified {@code cause}.
     */
    public ProtocolViolationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance with the specified {@code message}, {@code cause}, suppression enabled or
     * disabled, and writable stack trace enabled or disabled.
     */
    protected ProtocolViolationException(String message, Throwable cause, boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Throwable fillInStackTrace() {
        if (Flags.verboseExceptions()) {
            super.fillInStackTrace();
        }
        return this;
    }
}