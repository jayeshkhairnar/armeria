package com.ctrlshift.client;

import static java.util.Objects.requireNonNull;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * Decorates a {@link Client}. Use {@link SimpleDecoratingClient} or
 * {@link ClientBuilder#decorator(Class, Class, DecoratingClientFunction)} if your {@link Client} has the same
 * {@link Request} and {@link Response} type with the {@link Client} being decorated.
 *
 * @param <T_I> the {@link Request} type of the {@link Client} being decorated
 * @param <T_O> the {@link Response} type of the {@link Client} being decorated
 * @param <R_I> the {@link Request} type of this {@link Client}
 * @param <R_O> the {@link Response} type of this {@link Client}
 */
public abstract class DecoratingClient<T_I extends Request, T_O extends Response,
                                       R_I extends Request, R_O extends Response> implements Client<R_I, R_O> {

    private final Client<T_I, T_O> delegate;

    /**
     * Creates a new instance that decorates the specified {@link Client}.
     */
    protected DecoratingClient(Client<T_I, T_O> delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    /**
     * Returns the {@link Client} being decorated.
     */
    @SuppressWarnings("unchecked")
    protected final <T extends Client<T_I, T_O>> T delegate() {
        return (T) delegate;
    }

    @Override
    public String toString() {
        final String simpleName = getClass().getSimpleName();
        final String name = simpleName.isEmpty() ? getClass().getName() : simpleName;
        return name + '(' + delegate + ')';
    }
}
