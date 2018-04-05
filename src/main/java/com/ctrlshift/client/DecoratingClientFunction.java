package com.ctrlshift.client;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * A functional interface that enables building a {@link SimpleDecoratingClient} with
 * {@link ClientBuilder#decorator(Class, Class, DecoratingClientFunction)}.
 *
 * @param <I> the {@link Request} type
 * @param <O> the {@link Response} type
 */
@FunctionalInterface
public interface DecoratingClientFunction<I extends Request, O extends Response> {
    /**
     * Sends a {@link Request} to a remote {@link Endpoint}, as specified in
     * {@link ClientRequestContext#endpoint()}.
     *
     * @param delegate the {@link Client} being decorated by this function
     * @param ctx the context of the {@link Request} being sent
     * @param req the {@link Request} being sent
     *
     * @return the {@link Response} to be received
     */
    O execute(Client<I, O> delegate, ClientRequestContext ctx, I req) throws Exception;
}
