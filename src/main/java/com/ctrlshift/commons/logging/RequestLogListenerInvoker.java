package com.ctrlshift.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrlshift.commons.RequestContext;
import com.ctrlshift.commons.util.SafeCloseable;

/**
 * Utility methods that invokes the callback methods of {@link RequestLogListener} safely.
 *
 * <p>They catch the exceptions raised by the callback methods and log them at WARN level.
 */
public final class RequestLogListenerInvoker {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogListenerInvoker.class);

    /**
     * Invokes {@link RequestLogListener#onRequestLog(RequestLog)}.
     */
    public static void invokeOnRequestLog(RequestLogListener listener, RequestLog log) {
        try (SafeCloseable ignored = RequestContext.push(log.context())) {
            listener.onRequestLog(log);
        } catch (Throwable e) {
            logger.warn("onRequestLog() failed with an exception:", e);
        }
    }

    private RequestLogListenerInvoker() {}
}