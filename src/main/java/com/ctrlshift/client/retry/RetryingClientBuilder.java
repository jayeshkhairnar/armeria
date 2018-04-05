package com.ctrlshift.client.retry;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.function.Function;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import com.ctrlshift.client.Client;
import com.ctrlshift.commons.Flags;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * Builds a new {@link RetryingClient} or its decorator function.
 *
 * @param <T> the type of {@link RetryingClientBuilder}
 * @param <U> the type of the {@link Client} that this builder builds or decorates
 * @param <I> the type of outgoing {@link Request} of the {@link Client}
 * @param <O> the type of incoming {@link Response} of the {@link Client}
 */
public abstract class RetryingClientBuilder<
        T extends RetryingClientBuilder<T, U, I, O>, U extends RetryingClient<I, O>,
        I extends Request, O extends Response> {

    final RetryStrategy<I, O> retryStrategy;
    int maxTotalAttempts = Flags.defaultMaxTotalAttempts();
    long responseTimeoutMillisForEachAttempt = Flags.defaultResponseTimeoutMillis();

    /**
     * Creates a new builder with the specified retry strategy.
     */
    protected RetryingClientBuilder(RetryStrategy<I, O> retryStrategy) {
        this.retryStrategy = requireNonNull(retryStrategy, "retryStrategy");
    }

    @SuppressWarnings("unchecked")
    final T self() {
        return (T) this;
    }

    /**
     * Sets the maximum number of total attempts. If unspecified, the value from
     * {@link Flags#defaultMaxTotalAttempts()} will be used.
     *
     * @return {@link T} to support method chaining.
     */
    public T maxTotalAttempts(int maxTotalAttempts) {
        checkArgument(maxTotalAttempts > 0,
                      "maxTotalAttempts: %s (expected: > 0)", maxTotalAttempts);
        this.maxTotalAttempts = maxTotalAttempts;
        return self();
    }

    /**
     * Sets the response timeout for each attempt in milliseconds. When requests in {@link RetryingClient}
     * are made, corresponding responses are timed out by this value. {@code 0} disables the timeout.
     * It will be set by the default value in {@link Flags#defaultResponseTimeoutMillis()}, if the client
     * dose not specify.
     *
     * @return {@link T} to support method chaining.
     *
     * @see <a href="https://line.github.io/armeria/advanced-retry.html#per-attempt-timeout">Per-attempt
     *      timeout</a>
     */
    public T responseTimeoutMillisForEachAttempt(long responseTimeoutMillisForEachAttempt) {
        checkArgument(responseTimeoutMillisForEachAttempt >= 0,
                      "responseTimeoutMillisForEachAttempt: %s (expected: >= 0)",
                      responseTimeoutMillisForEachAttempt);
        this.responseTimeoutMillisForEachAttempt = responseTimeoutMillisForEachAttempt;
        return self();
    }

    /**
     * Sets the response timeout for each attempt. When requests in {@link RetryingClient} are made,
     * corresponding responses are timed out by this value. {@code 0} disables the timeout.
     *
     * @return {@link T} to support method chaining.
     *
     * @see <a href="https://line.github.io/armeria/advanced-retry.html#per-attempt-timeout">Per-attempt
     *      timeout</a>
     */
    public T responseTimeoutForEachAttempt(Duration responseTimeoutForEachAttempt) {
        checkArgument(
                !requireNonNull(responseTimeoutForEachAttempt, "responseTimeoutForEachAttempt").isNegative(),
                "responseTimeoutForEachAttempt: %s (expected: >= 0)", responseTimeoutForEachAttempt);
        return responseTimeoutMillisForEachAttempt(responseTimeoutForEachAttempt.toMillis());
    }

    /**
     * Returns a newly-created {@link RetryingClient} based on the properties of this builder.
     */
    abstract U build(Client<I, O> delegate);

    /**
     * Returns a newly-created decorator that decorates a {@link Client} with a new {@link RetryingClient}
     * based on the properties of this builder.
     */
    abstract Function<Client<I, O>, U> newDecorator();

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                          .add("retryStrategy", retryStrategy)
                          .add("maxTotalAttempts", maxTotalAttempts)
                          .add("responseTimeoutMillisForEachAttempt", responseTimeoutMillisForEachAttempt);
    }
}