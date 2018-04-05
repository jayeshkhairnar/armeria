package com.ctrlshift.server;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * A {@link Service} that decorates another {@link Service}. Use {@link DecoratingService} if your
 * {@link Service} has different {@link Request} or {@link Response} type from the {@link Service} being
 * decorated.
 *
 * @param <I> the {@link Request} type of the {@link Service} being decorated
 * @param <O> the {@link Response} type of the {@link Service} being decorated
 *
 * @see Service#decorate(DecoratingServiceFunction)
 */
public abstract class SimpleDecoratingService<I extends Request, O extends Response>
        extends DecoratingService<I, O, I, O> {

    /**
     * Creates a new instance that decorates the specified {@link Service}.
     */
    protected SimpleDecoratingService(Service<I, O> delegate) {
        super(delegate);
    }
}
