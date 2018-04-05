package com.ctrlshift.client.retry;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import com.google.common.base.MoreObjects;

/**
 * Wraps an existing {@link Backoff}.
 */
public class BackoffWrapper implements Backoff {
    private final Backoff delegate;

    protected BackoffWrapper(Backoff delegate) {
        this.delegate = checkNotNull(delegate, "delegate");
    }

    @Override
    public long nextDelayMillis(int numAttemptsSoFar) {
        return delegate.nextDelayMillis(numAttemptsSoFar);
    }

    protected Backoff delegate() {
        return delegate;
    }

    @Override
    public final <T> Optional<T> as(Class<T> backoffType) {
        final Optional<T> result = Backoff.super.as(backoffType);
        return result.isPresent() ? result : delegate.as(backoffType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("delegate", delegate).toString();
    }
}
