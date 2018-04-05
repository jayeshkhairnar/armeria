package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javax.annotation.Nullable;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * A {@link Service} that decorates another {@link Service}. Use {@link SimpleDecoratingService} or
 * {@link Service#decorate(DecoratingServiceFunction)} if your {@link Service} has the same {@link Request}
 * and {@link Response} type with the {@link Service} being decorated.
 *
 * @param <T_I> the {@link Request} type of the {@link Service} being decorated
 * @param <T_O> the {@link Response} type of the {@link Service} being decorated
 * @param <R_I> the {@link Request} type of this {@link Service}
 * @param <R_O> the {@link Response} type of this {@link Service}
 */
public abstract class DecoratingService<T_I extends Request, T_O extends Response,
                                        R_I extends Request, R_O extends Response>
        implements Service<R_I, R_O> {

    private final Service<T_I, T_O> delegate;

    /**
     * Creates a new instance that decorates the specified {@link Service}.
     */
    protected DecoratingService(Service<T_I, T_O> delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    /**
     * Returns the {@link Service} being decorated.
     */
    @SuppressWarnings("unchecked")
    protected final <T extends Service<T_I, T_O>> T delegate() {
        return (T) delegate;
    }

    @Override
    public void serviceAdded(ServiceConfig cfg) throws Exception {
        ServiceCallbackInvoker.invokeServiceAdded(cfg, delegate);
    }

    @Override
    public final <T> Optional<T> as(Class<T> serviceType) {
        final Optional<T> result = Service.super.as(serviceType);
        return result.isPresent() ? result : delegate.as(serviceType);
    }

    @Override
    public boolean shouldCachePath(String path, @Nullable String query, PathMapping pathMapping) {
        return delegate.shouldCachePath(path, query, pathMapping);
    }

    @Override
    public String toString() {
        final String simpleName = getClass().getSimpleName();
        final String name = simpleName.isEmpty() ? getClass().getName() : simpleName;
        return name + '(' + delegate + ')';
    }
}
