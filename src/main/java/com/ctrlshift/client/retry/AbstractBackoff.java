package com.ctrlshift.client.retry;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A skeletal {@link Backoff} implementation.
 */
public abstract class AbstractBackoff implements Backoff {

    static void validateNumAttemptsSoFar(int numAttemptsSoFar) {
        checkArgument(numAttemptsSoFar > 0, "numAttemptsSoFar: %s (expected: > 0)", numAttemptsSoFar);
    }

    @Override
    public final long nextDelayMillis(int numAttemptsSoFar) {
        validateNumAttemptsSoFar(numAttemptsSoFar);
        return doNextDelayMillis(numAttemptsSoFar);
    }

    /**
     * Invoked by {@link #nextDelayMillis(int)} after {@code numAttemptsSoFar} is validated.
     */
    protected abstract long doNextDelayMillis(int numAttemptsSoFar);
}