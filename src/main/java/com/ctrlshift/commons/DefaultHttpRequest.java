package com.ctrlshift.commons;

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;

import com.ctrlshift.commons.stream.DefaultStreamMessage;

/**
 * Default {@link HttpRequest} implementation.
 *
 * @deprecated Use {@link HttpRequest#streaming(HttpHeaders)}.
 */
@Deprecated
public class DefaultHttpRequest extends DefaultStreamMessage<HttpObject> implements HttpRequestWriter {

    private final HttpHeaders headers;

    /**
     * Creates a new instance with the specified headers.
     */
    public DefaultHttpRequest(HttpHeaders headers) {
        this.headers = requireNonNull(headers, "headers");
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .addValue(headers()).toString();
    }
}