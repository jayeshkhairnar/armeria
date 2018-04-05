package com.ctrlshift.commons;

import io.netty.util.AsciiString;

/**
 * The class of HTTP status.
 */
public enum HttpStatusClass {

    // Forked from Netty at 4875a2aad4c82204caedb2d121fb5ef03c09fe44

    /**
     * The informational class (1xx).
     */
    INFORMATIONAL(100, 200, "Informational"),
    /**
     * The success class (2xx).
     */
    SUCCESS(200, 300, "Success"),
    /**
     * The redirection class (3xx).
     */
    REDIRECTION(300, 400, "Redirection"),
    /**
     * The client error class (4xx).
     */
    CLIENT_ERROR(400, 500, "Client Error"),
    /**
     * The server error class (5xx).
     */
    SERVER_ERROR(500, 600, "Server Error"),
    /**
     * The unknown class.
     */
    UNKNOWN(0, 0, "Unknown Status") {
        @Override
        public boolean contains(int code) {
            return code < 100 || code >= 600;
        }
    };

    /**
     * Returns the class of the specified HTTP status code.
     */
    public static HttpStatusClass valueOf(int code) {
        if (INFORMATIONAL.contains(code)) {
            return INFORMATIONAL;
        }
        if (SUCCESS.contains(code)) {
            return SUCCESS;
        }
        if (REDIRECTION.contains(code)) {
            return REDIRECTION;
        }
        if (CLIENT_ERROR.contains(code)) {
            return CLIENT_ERROR;
        }
        if (SERVER_ERROR.contains(code)) {
            return SERVER_ERROR;
        }
        return UNKNOWN;
    }

    private final int min;
    private final int max;
    private final AsciiString defaultReasonPhrase;

    HttpStatusClass(int min, int max, String defaultReasonPhrase) {
        this.min = min;
        this.max = max;
        this.defaultReasonPhrase = AsciiString.cached(defaultReasonPhrase);
    }

    /**
     * Returns {@code true} if and only if the specified HTTP status code falls into this class.
     */
    public boolean contains(int code) {
        return code >= min && code < max;
    }

    /**
     * Returns the default reason phrase of this HTTP status class.
     */
    AsciiString defaultReasonPhrase() {
        return defaultReasonPhrase;
    }
}
