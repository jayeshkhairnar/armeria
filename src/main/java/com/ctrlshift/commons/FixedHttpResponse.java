package com.ctrlshift.commons;

import com.ctrlshift.commons.stream.OneElementFixedStreamMessage;
import com.ctrlshift.commons.stream.RegularFixedStreamMessage;
import com.ctrlshift.commons.stream.TwoElementFixedStreamMessage;

/**
 * An {@link HttpResponse} optimized for when all the {@link HttpObject}s that will be published are known at
 * construction time.
 */
final class FixedHttpResponse {

    static final class OneElementFixedHttpResponse
            extends OneElementFixedStreamMessage<HttpObject> implements HttpResponse {
        OneElementFixedHttpResponse(HttpObject obj) {
            super(obj);
        }
    }

    static final class TwoElementFixedHttpResponse
            extends TwoElementFixedStreamMessage<HttpObject> implements HttpResponse {
        TwoElementFixedHttpResponse(HttpObject obj1, HttpObject obj2) {
            super(obj1, obj2);
        }
    }

    static final class RegularFixedHttpResponse
            extends RegularFixedStreamMessage<HttpObject> implements HttpResponse {
        RegularFixedHttpResponse(HttpObject... objs) {
            super(objs);
        }
    }

    private FixedHttpResponse() {}
}
