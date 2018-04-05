package com.ctrlshift.commons;

import org.reactivestreams.Subscriber;

import com.ctrlshift.commons.stream.EmptyFixedStreamMessage;
import com.ctrlshift.commons.stream.OneElementFixedStreamMessage;
import com.ctrlshift.commons.stream.RegularFixedStreamMessage;
import com.ctrlshift.commons.stream.TwoElementFixedStreamMessage;

import io.netty.util.concurrent.EventExecutor;

/**
 * An {@link HttpRequest} optimized for when all the {@link HttpObject}s that will be published are known at
 * construction time.
 */
final class FixedHttpRequest {

    static final class EmptyFixedHttpRequest
            extends EmptyFixedStreamMessage<HttpObject> implements HttpRequest {

        private final HttpHeaders headers;

        EmptyFixedHttpRequest(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public HttpHeaders headers() {
            return headers;
        }
    }

    static final class OneElementFixedHttpRequest
            extends OneElementFixedStreamMessage<HttpObject> implements HttpRequest {

        private final HttpHeaders headers;

        OneElementFixedHttpRequest(HttpHeaders headers, HttpObject obj) {
            super(obj);
            this.headers = headers;
        }

        @Override
        public HttpHeaders headers() {
            return headers;
        }
    }

    static final class TwoElementFixedHttpRequest
            extends TwoElementFixedStreamMessage<HttpObject> implements HttpRequest {

        private final HttpHeaders headers;

        TwoElementFixedHttpRequest(
                HttpHeaders headers, HttpObject obj1, HttpObject obj2) {
            super(obj1, obj2);
            this.headers = headers;
        }

        @Override
        public HttpHeaders headers() {
            return headers;
        }
    }

    static final class RegularFixedHttpRequest
            extends RegularFixedStreamMessage<HttpObject> implements HttpRequest {

        private final HttpHeaders headers;

        RegularFixedHttpRequest(HttpHeaders headers, HttpObject... objs) {
            super(objs);
            this.headers = headers;
        }

        @Override
        public HttpHeaders headers() {
            return headers;
        }
    }

    private FixedHttpRequest() {}
}