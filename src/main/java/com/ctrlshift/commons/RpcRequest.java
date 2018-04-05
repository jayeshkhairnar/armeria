package com.ctrlshift.commons;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * An RPC {@link Request}.
 */
public interface RpcRequest extends Request {

    /**
     * Creates a new instance with no parameter.
     */
    static RpcRequest of(Class<?> serviceType, String method) {
        return new DefaultRpcRequest(serviceType, method, Collections.emptyList());
    }

    /**
     * Creates a new instance with a single parameter.
     */
    static RpcRequest of(Class<?> serviceType, String method, @Nullable Object parameter) {
        return new DefaultRpcRequest(serviceType, method, Collections.singletonList(parameter));
    }

    /**
     * Creates a new instance with the specified parameters.
     */
    static RpcRequest of(Class<?> serviceType, String method, Iterable<?> params) {
        requireNonNull(params, "params");

        if (!(params instanceof Collection)) {
            return new DefaultRpcRequest(serviceType, method, params);
        }

        final Collection<?> paramCollection = (Collection<?>) params;
        switch (paramCollection.size()) {
            case 0:
                return of(serviceType, method);
            case 1:
                if (paramCollection instanceof List) {
                    return of(serviceType, method, ((List<?>) paramCollection).get(0));
                } else {
                    return of(serviceType, method, paramCollection.iterator().next());
                }
            default:
                return new DefaultRpcRequest(serviceType, method, paramCollection.toArray());
        }
    }

    /**
     * Creates a new instance with the specified parameters.
     */
    static RpcRequest of(Class<?> serviceType, String method, Object... params) {
        requireNonNull(params, "params");
        switch (params.length) {
            case 0:
                return of(serviceType, method);
            case 1:
                return of(serviceType, method, params[0]);
            default:
                return new DefaultRpcRequest(serviceType, method, params);
        }
    }

    /**
     * Returns the type of the service this {@link RpcRequest} is called upon.
     */
    Class<?> serviceType();

    /**
     * Returns the method name.
     */
    String method();

    /**
     * Returns the parameters.
     */
    List<Object> params();
}
