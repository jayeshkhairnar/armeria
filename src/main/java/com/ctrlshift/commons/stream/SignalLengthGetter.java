package com.ctrlshift.commons.stream;

/**
 * A function that accepts one signal and produces the length of the signal.
 * @param <T> the type of the signal
 */
@FunctionalInterface
public interface SignalLengthGetter<T> {

    /**
     * Returns the length of {@code obj}.
     */
    int length(T obj);
}