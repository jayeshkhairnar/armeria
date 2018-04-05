package com.ctrlshift.server;

import java.util.Set;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;

/**
 * An interface that enables getting all the {@link PathMapping} where a {@link Service} should be bound.
 *
 * @param <I> the {@link Request} type
 * @param <O> the {@link Response} type
 */
public interface ServiceWithPathMappings<I extends Request, O extends Response> extends Service<I, O> {
    /**
     * Returns the set of {@link PathMapping} to which this {@link Service} is bound.
     */
    Set<PathMapping> pathMappings();
}
