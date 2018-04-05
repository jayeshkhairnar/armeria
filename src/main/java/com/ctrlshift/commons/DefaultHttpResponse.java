package com.ctrlshift.commons;

import com.google.common.base.MoreObjects;

import com.ctrlshift.commons.stream.DefaultStreamMessage;

/**
 * Default {@link HttpResponse} instance.
 *
 * @deprecated Use {@link HttpResponse#streaming()}.
 */
@Deprecated
public class DefaultHttpResponse
        extends DefaultStreamMessage<HttpObject> implements HttpResponseWriter {

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).toString();
    }
}
