package com.ctrlshift.server.annotation;

import com.ctrlshift.commons.AggregatedHttpMessage;
import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.MediaType;
import com.ctrlshift.server.ServiceRequestContext;

/**
 * A default implementation of a {@link RequestConverterFunction} which converts a binary body of
 * the {@link AggregatedHttpMessage} to one of {@code byte[]} or {@link HttpData}.
 */
public class ByteArrayRequestConverterFunction implements RequestConverterFunction {

    /**
     * Converts the specified {@link AggregatedHttpMessage} to an object of {@code expectedResultType}.
     * This converter allows only {@code byte[]} and {@link HttpData} as its return type, and
     * {@link AggregatedHttpMessage} would be consumed only if it does not have a {@code Content-Type} header
     * or if it has {@code Content-Type: application/octet-stream} or {@code Content-Type: application/binary}.
     */
    @Override
    public Object convertRequest(ServiceRequestContext ctx, AggregatedHttpMessage request,
                                 Class<?> expectedResultType) throws Exception {
        final MediaType mediaType = request.headers().contentType();
        if (mediaType == null ||
            mediaType.is(MediaType.OCTET_STREAM) ||
            mediaType.is(MediaType.APPLICATION_BINARY)) {

            if (expectedResultType == byte[].class) {
                return request.content().array();
            }
            if (expectedResultType == HttpData.class) {
                return request.content();
            }
        }
        return RequestConverterFunction.fallthrough();
    }
}
