package com.ctrlshift.server.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.HttpStatus;
import com.ctrlshift.commons.RequestContext;

/**
 * A default exception handler function. It returns an {@link HttpResponse} with
 * {@code 500 Internal Server Error} status code.
 */
final class DefaultExceptionHandler implements ExceptionHandlerFunction {
    private static Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public HttpResponse handleException(RequestContext ctx, HttpRequest req, Throwable cause) {
        logger.warn("No exception handler exists for the cause. " +
                    DefaultExceptionHandler.class.getName() + " is handling it.",
                    cause);
        return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}