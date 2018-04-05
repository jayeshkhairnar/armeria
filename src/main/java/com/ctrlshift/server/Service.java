package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;
import com.ctrlshift.commons.RpcRequest;
import com.ctrlshift.commons.RpcResponse;

/**
 * Handles a {@link Request} received by a {@link Server}.
 *
 * @param <I> the type of incoming {@link Request}. Must be {@link HttpRequest} or {@link RpcRequest}.
 * @param <O> the type of outgoing {@link Response}. Must be {@link HttpResponse} or {@link RpcResponse}.
 */
@FunctionalInterface
public interface Service<I extends Request, O extends Response> {

    /**
     * Invoked when this {@link Service} has been added to a {@link Server} with the specified configuration.
     * Please note that this method can be invoked more than once if this {@link Service} has been added more
     * than once.
     */
    default void serviceAdded(ServiceConfig cfg) throws Exception {}

    /**
     * Serves an incoming {@link Request}.
     *
     * @param ctx the context of the received {@link Request}
     * @param req the received {@link Request}
     *
     * @return the {@link Response}
     */
    O serve(ServiceRequestContext ctx, I req) throws Exception;

    /**
     * Undecorates this {@link Service} to find the {@link Service} which is an instance of the specified
     * {@code serviceType}. Use this method instead of an explicit downcast since most {@link Service}s are
     * decorated via {@link #decorate(Function)} and thus cannot be downcast. For example:
     * <pre>{@code
     * Service s = new MyService().decorate(LoggingService.newDecorator())
     *                            .decorate(AuthService.newDecorator());
     * MyService s1 = s.as(MyService.class);
     * LoggingService s2 = s.as(LoggingService.class);
     * AuthService s3 = s.as(AuthService.class);
     * }</pre>
     *
     * @param serviceType the type of the desired {@link Service}
     * @return the {@link Service} which is an instance of {@code serviceType} if this {@link Service}
     *         decorated such a {@link Service}. {@link Optional#empty()} otherwise.
     */
    default <T> Optional<T> as(Class<T> serviceType) {
        requireNonNull(serviceType, "serviceType");
        return serviceType.isInstance(this) ? Optional.of(serviceType.cast(this))
                                            : Optional.empty();
    }

    /**
     * Creates a new {@link Service} that decorates this {@link Service} with a new {@link Service} instance
     * of the specified {@code serviceType}. The specified {@link Class} must have a single-parameter
     * constructor which accepts this {@link Service}.
     */
    default <R extends Service<?, ?>> R decorate(Class<R> serviceType) {
        requireNonNull(serviceType, "serviceType");

        Constructor<?> constructor = null;
        for (Constructor<?> c : serviceType.getConstructors()) {
            if (c.getParameterCount() != 1) {
                continue;
            }
            if (c.getParameterTypes()[0].isAssignableFrom(getClass())) {
                constructor = c;
                break;
            }
        }

        if (constructor == null) {
            throw new IllegalArgumentException("cannot find a matching constructor: " + serviceType.getName());
        }

        try {
            return (R) constructor.newInstance(this);
        } catch (Exception e) {
            throw new IllegalStateException("failed to instantiate: " + serviceType.getName(), e);
        }
    }

    /**
     * Creates a new {@link Service} that decorates this {@link Service} with the specified {@code decorator}.
     */
    default <T extends Service<I, O>,
             R extends Service<R_I, R_O>, R_I extends Request, R_O extends Response>
    R decorate(Function<T, R> decorator) {
        @SuppressWarnings("unchecked")
        final R newService = decorator.apply((T) this);

        if (newService == null) {
            throw new NullPointerException("decorator.apply() returned null: " + decorator);
        }

        return newService;
    }

    /**
     * Creates a new {@link Service} that decorates this {@link Service} with the specified
     * {@link DecoratingServiceFunction}.
     */
    default Service<I, O> decorate(DecoratingServiceFunction<I, O> function) {
        return new FunctionalDecoratingService<>(this, function);
    }

    /**
     * Returns whether the given {@code path} and {@code query} should be cached if the service's result is
     * successful. By default, exact path mappings with no input query are cached.
     */
    default boolean shouldCachePath(String path, @Nullable String query, PathMapping pathMapping) {
        return pathMapping.exactPath().isPresent() && query == null;
    }
}
