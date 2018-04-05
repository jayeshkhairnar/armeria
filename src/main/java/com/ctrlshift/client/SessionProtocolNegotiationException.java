package com.ctrlshift.client;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javax.annotation.Nullable;

import com.ctrlshift.commons.SessionProtocol;

/**
 * An exception triggered when failed to negotiate the desired {@link SessionProtocol} with a server.
 */
public final class SessionProtocolNegotiationException extends RuntimeException {

    private static final long serialVersionUID = 5788454584691399858L;

    private final SessionProtocol expected;
    @Nullable
    private final SessionProtocol actual;

    /**
     * Creates a new instance with the specified expected {@link SessionProtocol}.
     */
    public SessionProtocolNegotiationException(SessionProtocol expected, @Nullable String reason) {
        super("expected: " + requireNonNull(expected, "expected") + ", reason: " + reason);
        this.expected = expected;
        actual = null;
    }

    /**
     * Creates a new instance with the specified expected and actual {@link SessionProtocol}s.
     */
    public SessionProtocolNegotiationException(SessionProtocol expected,
                                               @Nullable SessionProtocol actual, @Nullable String reason) {

        super("expected: " + requireNonNull(expected, "expected") +
              ", actual: " + requireNonNull(actual, "actual") + ", reason: " + reason);
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * Returns the expected {@link SessionProtocol}.
     */
    public SessionProtocol expected() {
        return expected;
    }

    /**
     * Returns the actual {@link SessionProtocol}.
     */
    public Optional<SessionProtocol> actual() {
        return Optional.ofNullable(actual);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
