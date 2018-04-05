package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * A decorating {@link Service} which implements its {@link #serve(ServiceRequestContext, Request)} method
 * using a given function.
 *
 * @see Service#decorate(DecoratingServiceFunction)
 */
final class FunctionalDecoratingService<I extends Request, O extends Response>
        extends SimpleDecoratingService<I, O> {

    private final DecoratingServiceFunction<I, O> function;

    /**
     * Creates a new instance with the specified function.
     */
    FunctionalDecoratingService(Service<I, O> delegate,
                                DecoratingServiceFunction<I, O> function) {
        super(delegate);
        this.function = requireNonNull(function, "function");
    }

    @Override
    public O serve(ServiceRequestContext ctx, I req) throws Exception {
        return function.serve(delegate(), ctx, req);
    }

    @Override
    public String toString() {
        return FunctionalDecoratingService.class.getSimpleName() + '(' + delegate() + ", " + function + ')';
    }
}
