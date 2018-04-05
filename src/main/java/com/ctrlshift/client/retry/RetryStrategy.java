package com.ctrlshift.client.retry;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.ctrlshift.client.ResponseTimeoutException;
import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.HttpStatus;
import com.ctrlshift.commons.HttpStatusClass;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * Determines whether a failed request should be retried.
 * @param <I> the request type
 * @param <O> the response type
 */
@FunctionalInterface
public interface RetryStrategy<I extends Request, O extends Response> {

    Backoff defaultBackoff = Backoff.of(Flags.defaultBackoffSpec());

    /**
     * A {@link RetryStrategy} that defines a retry should not be performed.
     */
    static <I extends Request, O extends Response> RetryStrategy<I, O> never() {
        return (request, response) -> CompletableFuture.completedFuture(null);
    }

    /**
     * Returns the {@link RetryStrategy} that retries the request with the {@link #defaultBackoff}
     * when the response matches {@link HttpStatusClass#SERVER_ERROR} or an {@link Exception} is raised.
     */
    static RetryStrategy<HttpRequest, HttpResponse> onServerErrorStatus() {
        return onServerErrorStatus(defaultBackoff);
    }

    /**
     * Returns the {@link RetryStrategy} that retries the request with the specified {@code backoff}
     * when the response matches {@link HttpStatusClass#SERVER_ERROR} or an {@link Exception} is raised.
     */
    static RetryStrategy<HttpRequest, HttpResponse> onServerErrorStatus(Backoff backoff) {
        requireNonNull(backoff, "backoff");
        return onStatus((status, thrown) -> {
            if (thrown != null || (status != null && status.codeClass() == HttpStatusClass.SERVER_ERROR)) {
                return backoff;
            }
            return null;
        });
    }

    /**
     * Returns the {@link RetryStrategy} that decides to retry the request using the specified
     * {@code backoffFunction}.
     *
     * @param backoffFunction the {@link BiFunction} that returns the {@link Backoff} or {@code null}
     *                        according to the {@link HttpStatus} and {@link Throwable}
     */
    static RetryStrategy<HttpRequest, HttpResponse> onStatus(
            BiFunction<HttpStatus, Throwable, Backoff> backoffFunction) {
        return new HttpStatusBasedRetryStrategy(backoffFunction);
    }

    /**
     * Returns a {@link CompletableFuture} that contains {@link Backoff} which will be used for retry.
     * If the condition does not match, this will return {@code null} to stop retry attempt.
     * Note that {@link ResponseTimeoutException} is not retriable for the whole retry, but each attempt.
     *
     * @see RetryingClientBuilder#responseTimeoutMillisForEachAttempt(long)
     *
     * @see <a href="https://line.github.io/armeria/advanced-retry.html#per-attempt-timeout">Per-attempt
     *      timeout</a>
     */
    @Nullable
    CompletableFuture<Backoff> shouldRetry(I request, O response);
}
