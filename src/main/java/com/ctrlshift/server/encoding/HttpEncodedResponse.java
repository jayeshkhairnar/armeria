package com.ctrlshift.server.encoding;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.zip.DeflaterOutputStream;

import javax.annotation.Nullable;

import org.reactivestreams.Subscriber;

import com.ctrlshift.commons.FilteredHttpResponse;
import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.HttpHeaderNames;
import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.HttpObject;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.HttpStatus;
import com.ctrlshift.commons.HttpStatusClass;
import com.ctrlshift.commons.MediaType;
import com.ctrlshift.commons.stream.FilteredStreamMessage;

/**
 * A {@link FilteredStreamMessage} that applies HTTP encoding to {@link HttpObject}s as they are published.
 */
class HttpEncodedResponse extends FilteredHttpResponse {

    private final HttpEncodingType encodingType;
    private final Predicate<MediaType> encodableContentTypePredicate;
    private final int minBytesToForceChunkedAndEncoding;

    @Nullable
    private ByteArrayOutputStream encodedStream;

    @Nullable
    private DeflaterOutputStream encodingStream;

    private boolean headersSent;

    HttpEncodedResponse(
            HttpResponse delegate,
            HttpEncodingType encodingType,
            Predicate<MediaType> encodableContentTypePredicate,
            int minBytesToForceChunkedAndEncoding) {
        super(delegate);
        this.encodingType = requireNonNull(encodingType, "encodingType");
        this.encodableContentTypePredicate = requireNonNull(encodableContentTypePredicate,
                                                            "encodableContentTypePredicate");
        this.minBytesToForceChunkedAndEncoding = HttpEncodingService.validateMinBytesToForceChunkedAndEncoding(
                minBytesToForceChunkedAndEncoding);
    }

    @Override
    protected HttpObject filter(HttpObject obj) {
        if (obj instanceof HttpHeaders) {
            final HttpHeaders headers = (HttpHeaders) obj;

            // Skip informational headers.
            final HttpStatus status = headers.status();
            if (status != null && status.codeClass() == HttpStatusClass.INFORMATIONAL) {
                return obj;
            }

            if (headersSent) {
                // Trailing headers, no modification.
                return obj;
            }

            if (status == null) {
                // Follow-up headers for informational headers, no modification.
                return obj;
            }

            headersSent = true;
            if (!shouldEncodeResponse(headers)) {
                return obj;
            }

            encodedStream = new ByteArrayOutputStream();
            encodingStream = HttpEncoders.getEncodingOutputStream(encodingType, encodedStream);

            // Always use chunked encoding when compressing.
            headers.remove(HttpHeaderNames.CONTENT_LENGTH);
            switch (encodingType) {
                case GZIP:
                    headers.set(HttpHeaderNames.CONTENT_ENCODING, "gzip");
                    break;
                case DEFLATE:
                    headers.set(HttpHeaderNames.CONTENT_ENCODING, "deflate");
                    break;
            }
            headers.set(HttpHeaderNames.VARY, HttpHeaderNames.ACCEPT_ENCODING.toString());
            return headers;
        }

        if (encodingStream == null) {
            // Encoding was disabled for this response.
            return obj;
        }

        final HttpData data = (HttpData) obj;
        assert encodedStream != null;
        try {
            encodingStream.write(data.array(), data.offset(), data.length());
            encodingStream.flush();
            return HttpData.of(encodedStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error encoding HttpData, this should not happen with byte arrays.",
                    e);
        } finally {
            encodedStream.reset();
        }
    }

    @Override
    protected void beforeComplete(Subscriber<? super HttpObject> subscriber) {
        closeEncoder();
        if (encodedStream != null && encodedStream.size() > 0) {
            subscriber.onNext(HttpData.of(encodedStream.toByteArray()));
        }
    }

    @Override
    protected Throwable beforeError(Subscriber<? super HttpObject> subscriber, Throwable cause) {
        closeEncoder();
        return cause;
    }

    private void closeEncoder() {
        if (encodingStream == null) {
            return;
        }
        try {
            encodingStream.close();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error closing encodingStream, this should not happen with byte arrays.",
                    e);
        }
    }

    private boolean shouldEncodeResponse(HttpHeaders headers) {
        if (headers.contains(HttpHeaderNames.CONTENT_ENCODING)) {
            // We don't do automatic encoding if the user-supplied headers contain
            // Content-Encoding.
            return false;
        }
        if (headers.contentType() != null) {
            // Make sure the content type is worth encoding.
            try {
                final MediaType contentType = headers.contentType();
                if (!encodableContentTypePredicate.test(contentType)) {
                    return false;
                }
            } catch (IllegalArgumentException e) {
                // Don't know content type of response, don't encode.
                return false;
            }
        }
        if (headers.contains(HttpHeaderNames.CONTENT_LENGTH)) {
            // We switch to chunked encoding and compress the response if it's reasonably
            // large as the compression savings should outweigh the chunked encoding
            // overhead.
            if (headers.getInt(HttpHeaderNames.CONTENT_LENGTH) < minBytesToForceChunkedAndEncoding) {
                return false;
            }
        }
        return true;
    }
}