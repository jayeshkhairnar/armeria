package com.ctrlshift.commons.logging;

import java.util.EventListener;
import java.util.Objects;

/**
 * Invoked when {@link RequestLog} meets the {@link RequestLogAvailability} specified with
 * {@link RequestLog#addListener(RequestLogListener, RequestLogAvailability)}.
 */
@FunctionalInterface
public interface RequestLogListener extends EventListener {

    /**
     * Invoked when {@link RequestLog} meets the {@link RequestLogAvailability} specified with
     * {@link RequestLog#addListener(RequestLogListener, RequestLogAvailability)}.
     */
    void onRequestLog(RequestLog log) throws Exception;

    /**
     * Returns a composed listener that calls this listener first and then the specified one.
     */
    default RequestLogListener andThen(RequestLogListener other) {
        Objects.requireNonNull(other, "other");

        final RequestLogListener first = this;
        final RequestLogListener second = other;

        return log -> {
            RequestLogListenerInvoker.invokeOnRequestLog(first, log);
            second.onRequestLog(log);
        };
    }
}
