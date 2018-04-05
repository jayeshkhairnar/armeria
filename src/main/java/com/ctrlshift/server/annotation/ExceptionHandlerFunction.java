package com.ctrlshift.server.annotation;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.RequestContext;
import com.ctrlshift.internal.FallthroughException;

/**
 * An interface for exception handler.
 *
 * @see ExceptionHandler
 */
@FunctionalInterface
public interface ExceptionHandlerFunction {

    /**
     * A default exception handler function. It returns an {@link HttpResponse} with
     * {@code 500 Internal Server Error} status code.
     */
    ExceptionHandlerFunction DEFAULT = new DefaultExceptionHandler();

    /**
     * Returns an {@link HttpResponse} which would be sent back to the client who sent the {@code req}.
     * Calls {@link ExceptionHandlerFunction#fallthrough()} or throws a {@link FallthroughException} if
     * this handler cannot handle the {@code cause}.
     */
    HttpResponse handleException(RequestContext ctx, HttpRequest req, Throwable cause);

    /**
     * Throws a {@link FallthroughException} in order to try to handle the {@link Throwable} by the next
     * handler.
     */
    static <T> T fallthrough() {
        // Always throw the exception quietly.
        throw FallthroughException.get();
    }
}
