package com.ctrlshift.client;

import com.ctrlshift.commons.util.AbstractOptionValue;

/**
 * A value of a {@link ClientOption}.
 *
 * @param <T> the type of the option value
 */
public final class ClientOptionValue<T> extends AbstractOptionValue<ClientOption<T>, T> {

    ClientOptionValue(ClientOption<T> constant, T value) {
        super(constant, value);
    }
}
