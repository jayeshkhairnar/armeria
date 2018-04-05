package com.ctrlshift.commons;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

final class HttpResponseAggregator extends HttpMessageAggregator {

    enum State {
        WAIT_NON_INFORMATIONAL,
        WAIT_CONTENT,
        WAIT_TRAILERS
    }

    @Nullable
    private List<HttpHeaders> informationals; // needs aggregation as well
    @Nullable
    private HttpHeaders headers;
    private HttpHeaders trailingHeaders;
    private State state = State.WAIT_NON_INFORMATIONAL;

    HttpResponseAggregator(CompletableFuture<AggregatedHttpMessage> future) {
        super(future);
        trailingHeaders = HttpHeaders.EMPTY_HEADERS;
    }

    @Override
    @SuppressWarnings("checkstyle:FallThrough")
    protected void onHeaders(HttpHeaders headers) {
        switch (state) {
            case WAIT_NON_INFORMATIONAL:
                final HttpStatus status = headers.status();
                if (status != null && status.codeClass() != HttpStatusClass.INFORMATIONAL) {
                    state = State.WAIT_CONTENT;
                    // Fall through
                } else {
                    if (informationals == null) {
                        informationals = new ArrayList<>(2);
                        informationals.add(headers);
                    } else if (status != null) {
                        // A new informational headers
                        informationals.add(headers);
                    } else {
                        // Append to the last informational headers
                        informationals.get(informationals.size() - 1).add(headers);
                    }
                    break;
                }
            case WAIT_CONTENT:
                if (this.headers == null) {
                    this.headers = headers;
                } else {
                    this.headers.add(headers);
                }
                break;
            case WAIT_TRAILERS:
                if (!headers.isEmpty()) {
                    if (trailingHeaders.isEmpty()) {
                        trailingHeaders = headers;
                    } else {
                        trailingHeaders.add(headers);
                    }
                }
                break;
        }
    }

    @Override
    protected void onData(HttpData data) {
        state = State.WAIT_TRAILERS;
        super.onData(data);
    }

    @Override
    protected AggregatedHttpMessage onSuccess(HttpData content) {
        checkState(headers != null, "An aggregated message does not have headers.");
        return AggregatedHttpMessage.of(firstNonNull(informationals, Collections.emptyList()),
                                        headers, content, trailingHeaders);
    }

    @Override
    protected void onFailure() {
        headers = null;
        trailingHeaders = HttpHeaders.EMPTY_HEADERS;
    }
}