package com.ctrlshift.commons.util;

import io.netty.util.AbstractConstant;

/**
 * A configuration option.
 *
 * @param <T> the type of the value of the option
 *
 * @see AbstractOptionValue
 * @see AbstractOptions
 */
@SuppressWarnings({ "rawtypes", "UnusedDeclaration" })
public abstract class AbstractOption<T> extends AbstractConstant {

    /**
     * Creates a new instance.
     *
     * @param id the integral ID of this option
     * @param name the name of this option
     */
    protected AbstractOption(int id, String name) {
        super(id, name);
    }
}
