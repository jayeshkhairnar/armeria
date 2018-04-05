package com.ctrlshift.client.retry;

import java.util.function.Function;

import com.ctrlshift.client.Client;
import com.ctrlshift.commons.RpcRequest;
import com.ctrlshift.commons.RpcResponse;

/**
 * Builds a new {@link RetryingRpcClient} or its decorator function.
 */
public class RetryingRpcClientBuilder
        extends RetryingClientBuilder<RetryingRpcClientBuilder, RetryingRpcClient, RpcRequest, RpcResponse> {

    /**
     * Creates a new builder with the specified retry strategy.
     */
    public RetryingRpcClientBuilder(
            RetryStrategy<RpcRequest, RpcResponse> retryStrategy) {
        super(retryStrategy);
    }

    /**
     * Returns a newly-created {@link RetryingRpcClient} based on the properties of this builder.
     */
    @Override
    public RetryingRpcClient build(Client<RpcRequest, RpcResponse> delegate) {
        return new RetryingRpcClient(
                delegate, retryStrategy, maxTotalAttempts, responseTimeoutMillisForEachAttempt);
    }

    /**
     * Returns a newly-created decorator that decorates a {@link Client} with a new {@link RetryingRpcClient}
     * based on the properties of this builder.
     */
    @Override
    public Function<Client<RpcRequest, RpcResponse>, RetryingRpcClient> newDecorator() {
        return delegate ->
                new RetryingRpcClient(
                        delegate, retryStrategy, maxTotalAttempts, responseTimeoutMillisForEachAttempt);
    }
}
