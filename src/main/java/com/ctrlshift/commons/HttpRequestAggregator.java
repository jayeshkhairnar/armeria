package com.ctrlshift.commons;

import java.util.concurrent.CompletableFuture;

final class HttpRequestAggregator extends HttpMessageAggregator {

    private final HttpRequest request;
    private HttpHeaders trailingHeaders;

    HttpRequestAggregator(HttpRequest request, CompletableFuture<AggregatedHttpMessage> future) {
        super(future);
        this.request = request;
        trailingHeaders = HttpHeaders.EMPTY_HEADERS;
    }

    @Override
    protected void onHeaders(HttpHeaders headers) {
        if (headers.isEmpty()) {
            return;
        }

        if (trailingHeaders.isEmpty()) {
            trailingHeaders = headers;
        } else {
            trailingHeaders.add(headers);
        }
    }

    @Override
    protected AggregatedHttpMessage onSuccess(HttpData content) {
        return AggregatedHttpMessage.of(request.headers(), content, trailingHeaders);
    }

    @Override
    protected void onFailure() {
        trailingHeaders = HttpHeaders.EMPTY_HEADERS;
    }
}
