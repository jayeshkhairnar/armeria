package com.ctrlshift.client.retry;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.HttpStatus;
import com.ctrlshift.internal.HttpHeaderSubscriber;

/**
 * Provides a {@link RetryStrategy} that decides to retry the request based on the {@link HttpStatus} of
 * its response or the {@link Exception} raised while processing the {@link HttpResponse}.
 */
final class HttpStatusBasedRetryStrategy implements RetryStrategy<HttpRequest, HttpResponse> {

    private final BiFunction<HttpStatus, Throwable, Backoff> backoffFunction;

    /**
     * Creates a new instance.
     */
    HttpStatusBasedRetryStrategy(BiFunction<HttpStatus, Throwable, Backoff> backoffFunction) {
        this.backoffFunction = requireNonNull(backoffFunction, "backoffFunction");
    }

    @Override
    public CompletableFuture<Backoff> shouldRetry(HttpRequest request, HttpResponse response) {
        final CompletableFuture<HttpHeaders> future = new CompletableFuture<>();
        final HttpHeaderSubscriber subscriber = new HttpHeaderSubscriber(future);
        response.completionFuture().whenComplete(subscriber);
        response.subscribe(subscriber);

        return future.handle((headers, thrown) -> backoffFunction
                .apply(headers != null ? headers.status() : null, thrown));
    }
}