package com.ctrlshift.commons.util;

import static java.util.Objects.requireNonNull;

/**
 * A holder of a value of an {@link AbstractOption}.
 *
 * @param <O> the {@link AbstractOption} that this option value is created by
 * @param <V> the type of the value of the option {@code 'O'}
 *
 * @see AbstractOption
 * @see AbstractOptions
 */
public abstract class AbstractOptionValue<O extends AbstractOption<V>, V> {

    private final O option;
    private final V value;

    /**
     * Creates a new instance with the specified {@code option} and {@code value}.
     */
    protected AbstractOptionValue(O option, V value) {
        this.option = requireNonNull(option, "option");
        this.value = requireNonNull(value, "value");
    }

    /**
     * Returns the option that this option value holder belongs to.
     */
    public O option() {
        return option;
    }

    /**
     * Returns the value of this option value holder has.
     */
    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return option.toString() + '=' + value;
    }
}