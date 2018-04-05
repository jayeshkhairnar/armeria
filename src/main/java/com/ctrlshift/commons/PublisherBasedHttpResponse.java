package com.ctrlshift.commons;

import org.reactivestreams.Publisher;

import com.ctrlshift.commons.stream.PublisherBasedStreamMessage;

final class PublisherBasedHttpResponse extends PublisherBasedStreamMessage<HttpObject> implements HttpResponse {
    PublisherBasedHttpResponse(Publisher<? extends HttpObject> publisher) {
        super(publisher);
    }
}