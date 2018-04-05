package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import com.ctrlshift.commons.AggregatedHttpMessage;
import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.HttpStatus;

/**
 * A {@link RuntimeException} that is raised to send an HTTP response with the content specified
 * by a user. This class holds an {@link HttpResponse} which would be sent back to the client who
 * sent the corresponding request.
 *
 * @see HttpStatusException
 */
public class HttpResponseException extends RuntimeException {

    /**
     * Returns a new {@link HttpResponseException} instance with the specified HTTP status code.
     */
    public static HttpResponseException of(int statusCode) {
        return of(HttpStatus.valueOf(statusCode));
    }

    /**
     * Returns a new {@link HttpResponseException} instance with the specified {@link HttpStatus}.
     */
    public static HttpResponseException of(HttpStatus httpStatus) {
        requireNonNull(httpStatus, "httpStatus");
        return new HttpResponseException(HttpResponse.of(httpStatus));
    }

    /**
     * Returns a new {@link HttpResponseException} instance with the specified {@link AggregatedHttpMessage}.
     */
    public static HttpResponseException of(AggregatedHttpMessage httpMessage) {
        return of(HttpResponse.of(requireNonNull(httpMessage, "httpMessage")));
    }

    /**
     * Returns a new {@link HttpResponseException} instance with the specified {@link HttpResponse}.
     */
    public static HttpResponseException of(HttpResponse httpResponse) {
        return new HttpResponseException(httpResponse);
    }

    private static final long serialVersionUID = 3487991462085151316L;

    private final HttpResponse httpResponse;

    /**
     * Creates a new instance with the specified {@link HttpResponse}.
     */
    protected HttpResponseException(HttpResponse httpResponse) {
        this.httpResponse = requireNonNull(httpResponse, "httpResponse");
    }

    /**
     * Returns the {@link HttpResponse} which would be sent back to the client who sent the
     * corresponding request.
     */
    public HttpResponse httpResponse() {
        return httpResponse;
    }

    @Override
    public Throwable fillInStackTrace() {
        if (Flags.verboseExceptions()) {
            return super.fillInStackTrace();
        }
        return this;
    }
}
