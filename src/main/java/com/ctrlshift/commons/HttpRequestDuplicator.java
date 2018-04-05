package com.ctrlshift.commons;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

import com.ctrlshift.commons.stream.AbstractStreamMessageDuplicator;
import com.ctrlshift.commons.stream.StreamMessage;
import com.ctrlshift.commons.stream.StreamMessageWrapper;

import io.netty.util.concurrent.EventExecutor;

/**
 * Allows subscribing to a {@link HttpRequest} multiple times by duplicating the stream.
 *
 * <pre>{@code
 * final HttpRequest originalReq = ...
 * final HttpRequestDuplicator reqDuplicator = new HttpRequestDuplicator(originalReq);
 *
 * final HttpRequest dupReq1 = reqDuplicator.duplicateStream();
 * final HttpRequest dupReq2 = reqDuplicator.duplicateStream(true); // the last stream
 *
 * dupReq1.subscribe(new FooSubscriber() {
 *     ...
 *     // Do something according to the first few elements of the request.
 * });
 *
 * final CompletableFuture<AggregatedHttpMessage> future2 = dupReq2.aggregate();
 * future2.handle((message, cause) -> {
 *     // Do something with message.
 * }
 * }</pre>
 */
public class HttpRequestDuplicator extends AbstractStreamMessageDuplicator<HttpObject, HttpRequest> {

    private final HttpHeaders headers;

    /**
     * Creates a new instance wrapping a {@link HttpRequest} and publishing to multiple subscribers.
     * The length of request is limited by default with the server-side parameter which is
     * {@link Flags#DEFAULT_MAX_REQUEST_LENGTH}. If you are at client-side, you need to use
     * {@link #HttpRequestDuplicator(HttpRequest, long)} and the {@code long} value should be greater than
     * the length of request or {@code 0} which disables the limit.
     * @param req the request that will publish data to subscribers
     */
    public HttpRequestDuplicator(HttpRequest req) {
        this(req, Flags.defaultMaxRequestLength());
    }

    /**
     * Creates a new instance wrapping a {@link HttpRequest} and publishing to multiple subscribers.
     * @param req the request that will publish data to subscribers
     * @param maxSignalLength the maximum length of signals. {@code 0} disables the length limit
     */
    public HttpRequestDuplicator(HttpRequest req, long maxSignalLength) {
        this(req, maxSignalLength, null);
    }

    /**
     * Creates a new instance wrapping a {@link HttpRequest} and publishing to multiple subscribers.
     * @param req the request that will publish data to subscribers
     * @param maxSignalLength the maximum length of signals. {@code 0} disables the length limit
     * @param executor the executor to use for upstream signals.
     */
    public HttpRequestDuplicator(HttpRequest req, long maxSignalLength, @Nullable EventExecutor executor) {
        super(requireNonNull(req, "req"), obj -> {
            if (obj instanceof HttpData) {
                return ((HttpData) obj).length();
            }
            return 0;
        }, executor, maxSignalLength);
        headers = req.headers();
    }

    @Override
    protected HttpRequest doDuplicateStream(StreamMessage<HttpObject> delegate) {
        return new DuplicateHttpRequest(delegate);
    }

    private class DuplicateHttpRequest
            extends StreamMessageWrapper<HttpObject> implements HttpRequest {

        DuplicateHttpRequest(StreamMessage<? extends HttpObject> delegate) {
            super(delegate);
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
}