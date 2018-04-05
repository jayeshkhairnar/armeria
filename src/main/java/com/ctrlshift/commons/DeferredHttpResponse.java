package com.ctrlshift.commons;

import java.util.concurrent.CompletionStage;

import com.ctrlshift.commons.stream.DeferredStreamMessage;

/**
 * An {@link HttpResponse} whose stream is published later by another {@link HttpResponse}. It is used when
 * an {@link HttpResponse} will not be instantiated early.
 *
 * @deprecated Use {@link HttpResponse#from(CompletionStage)}.
 */
@Deprecated
public class DeferredHttpResponse extends DeferredStreamMessage<HttpObject> implements HttpResponse {

    /**
     * Sets the delegate {@link HttpResponse} which will publish the stream actually.
     *
     * @throws IllegalStateException if the delegate has been set already or
     *                               if {@link #close()} or {@link #close(Throwable)} was called already.
     */
    public void delegate(HttpResponse delegate) {
        super.delegate(delegate);
    }
}
