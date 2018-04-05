package com.ctrlshift.commons.logging;

public enum RequestLogAvailability {
    // Request availability
    /**
     * Request processing started. The following properties become available:
     * <ul>
     *   <li>{@link RequestLog#requestStartTimeMillis()}</li>
     *   <li>{@link RequestLog#channel()}</li>
     *   <li>{@link RequestLog#sessionProtocol()}</li>
     *   <li>{@link RequestLog#host()}</li>
     * </ul>
     */
    REQUEST_START(1, 1),
    /**
     * {@link RequestLog#scheme()} and {@link RequestLog#serializationFormat()} are available,
     * as well as all the properties mentioned in {@link #REQUEST_START}.
     */
    SCHEME(1 | 2, 2),
    /**
     * {@link RequestLog#requestHeaders()} is available, as well as all the properties mentioned in
     * {@link #REQUEST_START}.
     */
    REQUEST_HEADERS(1 | 4, 4),
    /**
     * {@link RequestLog#requestContent()} is available, as well as all the properties mentioned in
     * {@link #REQUEST_START}.
     */
    REQUEST_CONTENT(1 | 8, 8),
    /**
     * {@link RequestLog#requestLength()}, {@link RequestLog#requestCause()} and
     * {@link RequestLog#requestDurationNanos()} are available, as well as all the properties mentioned in
     * {@link #REQUEST_START}, {@link #SCHEME}, {@link #REQUEST_HEADERS} and {@link #REQUEST_CONTENT}.
     */
    REQUEST_END(1 | 2 | 4 | 8 | 16, 1 | 2 | 4 | 8 | 16),

    // Response availability
    /**
     * {@link RequestLog#responseStartTimeMillis()} is available.
     */
    RESPONSE_START(1 << 16, 1 << 16),
    /**
     * {@link RequestLog#responseHeaders()} is available, as well as all the properties mentioned in
     * {@link #RESPONSE_START}.
     */
    RESPONSE_HEADERS((1 | 2) << 16, 2 << 16),
    /**
     * {@link RequestLog#responseContent()} is available, as well as all the properties mentioned in
     * {@link #RESPONSE_START}.
     */
    RESPONSE_CONTENT((1 | 4) << 16, 4 << 16),
    /**
     * {@link RequestLog#responseLength()}, {@link RequestLog#responseCause()},
     * {@link RequestLog#responseDurationNanos()} and {@link RequestLog#totalDurationNanos()} are available,
     * as well as all the properties mentioned in {@link #RESPONSE_START}, {@link #RESPONSE_HEADERS} and
     * {@link #RESPONSE_CONTENT}.
     */
    RESPONSE_END((1 | 2 | 4 | 8) << 16, (1 | 2 | 4 | 8) << 16),

    // Everything
    /**
     * All the properties mentioned in {@link #REQUEST_END} and {@link #RESPONSE_END} are available.
     */
    COMPLETE(1 | 2 | 4 | 8 | 16 | (1 | 2 | 4 | 8) << 16, /* unused */ 0);

    private final int getterFlags;
    private final int setterFlags;

    RequestLogAvailability(int getterFlags, int setterFlags) {
        this.getterFlags = getterFlags;
        this.setterFlags = setterFlags;
    }

    int getterFlags() {
        return getterFlags;
    }

    int setterFlags() {
        return setterFlags;
    }
}
