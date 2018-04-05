package com.ctrlshift.client.retry;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.ctrlshift.client.Client;
import com.ctrlshift.client.ClientRequestContext;
import com.ctrlshift.client.ResponseTimeoutException;
import com.ctrlshift.client.SimpleDecoratingClient;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.RequestContext;
import com.ctrlshift.commons.Response;
import com.ctrlshift.commons.util.SafeCloseable;

import io.netty.util.AttributeKey;

/**
 * A {@link Client} decorator that handles failures of remote invocation and retries requests.
 *
 * @param <I> the {@link Request} type
 * @param <O> the {@link Response} type
 */
public abstract class RetryingClient<I extends Request, O extends Response>
        extends SimpleDecoratingClient<I, O> {

    private static final AttributeKey<State> STATE =
            AttributeKey.valueOf(RetryingClient.class, "STATE");

    private final RetryStrategy<I, O> retryStrategy;
    private final int maxTotalAttempts;
    private final long responseTimeoutMillisForEachAttempt;

    /**
     * Creates a new instance that decorates the specified {@link Client}.
     */
    protected RetryingClient(Client<I, O> delegate, RetryStrategy<I, O> retryStrategy,
                             int maxTotalAttempts, long responseTimeoutMillisForEachAttempt) {
        super(delegate);
        this.retryStrategy = requireNonNull(retryStrategy, "retryStrategy");
        checkArgument(maxTotalAttempts > 0, "maxTotalAttempts: %s (expected: > 0)", maxTotalAttempts);
        this.maxTotalAttempts = maxTotalAttempts;

        checkArgument(responseTimeoutMillisForEachAttempt >= 0,
                      "responseTimeoutMillisForEachAttempt: %s (expected: >= 0)",
                      responseTimeoutMillisForEachAttempt);
        this.responseTimeoutMillisForEachAttempt = responseTimeoutMillisForEachAttempt;
    }

    @Override
    public O execute(ClientRequestContext ctx, I req) throws Exception {
        final State state =
                new State(maxTotalAttempts, responseTimeoutMillisForEachAttempt, ctx.responseTimeoutMillis());
        ctx.attr(STATE).set(state);
        return doExecute(ctx, req);
    }

    /**
     * Invoked by {@link #execute(ClientRequestContext, Request)}
     * after the deadline for response timeout is set.
     */
    protected abstract O doExecute(ClientRequestContext ctx, I req) throws Exception;

    /**
     * Executes the delegate with a new derived {@link ClientRequestContext}.
     */
    protected final O executeDelegate(ClientRequestContext ctx, I req) throws Exception {
        final ClientRequestContext derivedContext = ctx.newDerivedContext(req);
        ctx.logBuilder().addChild(derivedContext.log());
        try (SafeCloseable ignore = RequestContext.push(derivedContext, false)) {
            return delegate().execute(derivedContext, req);
        }
    }

    /**
     * This should be called when retrying is finished.
     */
    protected static void onRetryingComplete(ClientRequestContext ctx) {
        ctx.logBuilder().endResponseWithLastChild();
    }

    protected RetryStrategy<I, O> retryStrategy() {
        return retryStrategy;
    }

    /**
     * Resets the {@link ClientRequestContext#responseTimeoutMillis()}.
     *
     * @return {@code true} if the response timeout is set, {@code false} if it can't be set due to the timeout
     */
    protected final boolean setResponseTimeout(ClientRequestContext ctx) {
        requireNonNull(ctx, "ctx");
        final long responseTimeoutMillis = ctx.attr(STATE).get().responseTimeoutMillis();
        if (responseTimeoutMillis < 0) {
            return false;
        }
        ctx.setResponseTimeoutMillis(responseTimeoutMillis);
        return true;
    }

    /**
     * Returns the next delay which retry will be made after. The delay will be:
     *
     * <p>{@code Math.min(responseTimeoutMillis, Backoff.nextDelayMillis(int))}
     *
     * @return the number of milliseconds to wait for before attempting a retry
     * @throws RetryGiveUpException if the current attempt number is greater than {@code maxTotalAttempts} or
     *                              {@link Backoff#nextDelayMillis(int)} returns -1
     * @throws ResponseTimeoutException if the remaining response timeout is equal to or less than 0
     */
    protected final long getNextDelay(ClientRequestContext ctx, Backoff backoff) {
        return getNextDelay(ctx, backoff, -1);
    }

    /**
     * Returns the next delay which retry will be made after. The delay will be:
     *
     * <p>{@code Math.min(responseTimeoutMillis, Math.max(Backoff.nextDelayMillis(int),
     * millisAfterFromServer))}
     *
     * @return the number of milliseconds to wait for before attempting a retry
     * @throws RetryGiveUpException if the current attempt number is greater than {@code maxTotalAttempts} or
     *                              {@link Backoff#nextDelayMillis(int)} returns -1
     * @throws ResponseTimeoutException if the remaining response timeout is equal to or less than 0
     */
    protected final long getNextDelay(ClientRequestContext ctx, Backoff backoff, long millisAfterFromServer) {
        requireNonNull(ctx, "ctx");
        requireNonNull(backoff, "backoff");
        final State state = ctx.attr(STATE).get();
        final int currentAttemptNo = state.currentAttemptNoWith(backoff);

        if (currentAttemptNo < 0) {
            // Exceeded the default number of max attempt.
            throw RetryGiveUpException.get();
        }

        long nextDelay = backoff.nextDelayMillis(currentAttemptNo);
        if (nextDelay < 0) {
            // Exceeded the number of max attempt in the backoff.
            throw RetryGiveUpException.get();
        }

        nextDelay = Math.max(nextDelay, millisAfterFromServer);

        if (state.timeoutForWholeRetryEnabled() && nextDelay > state.actualResponseTimeoutMillis()) {
            // Do not wait until the timeout occurs, but throw the Exception as soon as possible.
            throw ResponseTimeoutException.get();
        }

        return nextDelay;
    }

    private static class State {

        private final int maxTotalAttempts;
        private final long responseTimeoutMillisForEachAttempt;
        private final long responseTimeoutMillis;
        private final long deadlineNanos;

        @Nullable
        private Backoff lastBackoff;
        private int currentAttemptNoWithLastBackoff;
        private int totalAttemptNo;

        State(int maxTotalAttempts, long responseTimeoutMillisForEachAttempt, long responseTimeoutMillis) {
            this.maxTotalAttempts = maxTotalAttempts;
            this.responseTimeoutMillisForEachAttempt = responseTimeoutMillisForEachAttempt;
            this.responseTimeoutMillis = responseTimeoutMillis;
            if (responseTimeoutMillis > 0) {
                deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(responseTimeoutMillis);
            } else {
                // Response timeout is disabled.
                deadlineNanos = 0;
            }
            totalAttemptNo = 1;
        }

        /**
         * Returns the smaller value between {@link #responseTimeoutMillisForEachAttempt} and
         * remaining {@link #responseTimeoutMillis}.
         *
         * @return 0 if the response timeout for both of each request and whole retry is disabled or
         *         -1 if the elapsed time from the first request has passed {@code responseTimeoutMillis}
         */
        long responseTimeoutMillis() {
            if (!timeoutForWholeRetryEnabled()) {
                return responseTimeoutMillisForEachAttempt;
            }

            final long actualResponseTimeoutMillis = actualResponseTimeoutMillis();

            // Consider 0 or less than 0 of actualResponseTimeoutMillis as timed out.
            if (actualResponseTimeoutMillis <= 0) {
                return -1;
            }

            if (responseTimeoutMillisForEachAttempt > 0) {
                return Math.min(responseTimeoutMillisForEachAttempt, actualResponseTimeoutMillis);
            }

            return actualResponseTimeoutMillis;
        }

        boolean timeoutForWholeRetryEnabled() {
            return responseTimeoutMillis != 0;
        }

        long actualResponseTimeoutMillis() {
            return TimeUnit.NANOSECONDS.toMillis(deadlineNanos - System.nanoTime());
        }

        int currentAttemptNoWith(Backoff backoff) {
            if (totalAttemptNo++ >= maxTotalAttempts) {
                return -1;
            }
            if (lastBackoff != backoff) {
                lastBackoff = backoff;
                currentAttemptNoWithLastBackoff = 1;
            }
            return currentAttemptNoWithLastBackoff++;
        }
    }
}
