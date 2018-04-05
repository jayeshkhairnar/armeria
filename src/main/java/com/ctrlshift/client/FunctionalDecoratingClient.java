package com.ctrlshift.client;

import static java.util.Objects.requireNonNull;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * A decorating {@link Client} which implements its {@link #execute(ClientRequestContext, Request)} method
 * using a given function.
 *
 * @see ClientBuilder#decorator(Class, Class, DecoratingClientFunction)
 */
final class FunctionalDecoratingClient<I extends Request, O extends Response>
        extends SimpleDecoratingClient<I, O> {

    private final DecoratingClientFunction<I, O> function;

    /**
     * Creates a new instance with the specified function.
     */
    FunctionalDecoratingClient(Client<I, O> delegate,
                               DecoratingClientFunction<I, O> function) {
        super(delegate);
        this.function = requireNonNull(function, "function");
    }

    @Override
    public O execute(ClientRequestContext ctx, I req) throws Exception {
        return function.execute(delegate(), ctx, req);
    }

    @Override
    public String toString() {
        return FunctionalDecoratingClient.class.getSimpleName() + '(' + delegate() + ", " + function + ')';
    }
}