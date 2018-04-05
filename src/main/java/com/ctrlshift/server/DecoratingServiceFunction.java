package com.ctrlshift.server;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * A functional interface that enables building a {@link SimpleDecoratingService} with
 * {@link Service#decorate(DecoratingServiceFunction)}.
 *
 * @param <I> the {@link Request} type
 * @param <O> the {@link Response} type
 */
@FunctionalInterface
public interface DecoratingServiceFunction<I extends Request, O extends Response> {
    /**
     * Serves an incoming {@link Request}.
     *
     * @param delegate the {@link Service} being decorated by this function
     * @param ctx the context of the received {@link Request}
     * @param req the received {@link Request}
     *
     * @return the {@link Response}
     */
    O serve(Service<I, O> delegate, ServiceRequestContext ctx, I req) throws Exception;
}
