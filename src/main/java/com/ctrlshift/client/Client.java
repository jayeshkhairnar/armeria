package com.ctrlshift.client;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;
import com.ctrlshift.commons.RpcRequest;
import com.ctrlshift.commons.RpcResponse;

/**
 * Sends a {@link Request} to a remote {@link Endpoint}.
 *
 * <p>Note that this interface is not a user's entry point for sending a {@link Request}. It is rather
 * a generic request processor interface implemented by a {@link DecoratingClient}, which intercepts
 * a {@link Request}. A user is supposed to make his or her {@link Request} via the object returned by
 * a {@link ClientBuilder} or {@link Clients}, which usually does not implement this interface.
 *
 * @param <I> the type of outgoing {@link Request}. Must be {@link HttpRequest} or {@link RpcRequest}.
 * @param <O> the type of incoming {@link Response}. Must be {@link HttpResponse} or {@link RpcResponse}.
 *
 * @see UserClient
 */
@FunctionalInterface
public interface Client<I extends Request, O extends Response> {
    /**
     * Sends a {@link Request} to a remote {@link Endpoint}, as specified in
     * {@link ClientRequestContext#endpoint()}.
     *
     * @return the {@link Response} to the specified {@link Request}
     */
    O execute(ClientRequestContext ctx, I req) throws Exception;
}