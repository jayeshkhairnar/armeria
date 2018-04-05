package com.ctrlshift.commons;

import com.ctrlshift.commons.stream.FilteredStreamMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

/**
 * An {@link HttpResponse} that filters objects as they are published. The filtering
 * will happen from an I/O thread, meaning the order of the filtering will match the
 * order that the {@code delegate} processes the objects in.
 */
public abstract class FilteredHttpResponse
        extends FilteredStreamMessage<HttpObject, HttpObject> implements HttpResponse {

    /**
     * Creates a new {@link FilteredHttpResponse} that filters objects published by {@code delegate}
     * before passing to a subscriber.
     */
    protected FilteredHttpResponse(HttpResponse delegate) {
        super(delegate);
    }

    /**
     * Creates a new {@link FilteredHttpResponse} that filters objects published by {@code delegate}
     * before passing to a subscriber.
     *
     * @param withPooledObjects if {@code true}, {@link #filter(Object)} receives the pooled {@link ByteBuf}
     *                          and {@link ByteBufHolder} as is, without making a copy. If you don't know what
     *                          this means, use {@link #FilteredHttpResponse(HttpResponse)}.
     */
    protected FilteredHttpResponse(HttpResponse delegate, boolean withPooledObjects) {
        super(delegate, withPooledObjects);
    }
}
