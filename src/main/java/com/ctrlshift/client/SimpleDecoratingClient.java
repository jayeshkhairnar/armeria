package com.ctrlshift.client;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;
import com.ctrlshift.server.Service;

/**
 * Decorates a {@link Client}. Use {@link DecoratingClient} if your {@link Service} has different
 * {@link Request} or {@link Response} type from the {@link Client} being decorated.
 *
 * @param <I> the {@link Request} type of the {@link Client} being decorated
 * @param <O> the {@link Response} type of the {@link Client} being decorated
 */
public abstract class SimpleDecoratingClient<I extends Request, O extends Response>
        extends DecoratingClient<I, O, I, O> {

    /**
     * Creates a new instance that decorates the specified {@link Client}.
     */
    protected SimpleDecoratingClient(Client<I, O> delegate) {
        super(delegate);
    }
}
