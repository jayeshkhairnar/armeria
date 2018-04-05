package com.ctrlshift.commons;

import org.reactivestreams.Publisher;

import com.ctrlshift.commons.stream.PublisherBasedStreamMessage;

final class PublisherBasedHttpRequest extends PublisherBasedStreamMessage<HttpObject> implements HttpRequest {

    private final HttpHeaders headers;

    PublisherBasedHttpRequest(HttpHeaders headers, Publisher<? extends HttpObject> publisher) {
        super(publisher);
        this.headers = headers;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }
}