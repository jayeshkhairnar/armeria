package com.ctrlshift.server.composition;

import static java.util.Objects.requireNonNull;

import java.util.regex.Pattern;

import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;
import com.ctrlshift.server.PathMapping;
import com.ctrlshift.server.Service;

/**
 * A pair of a {@link PathMapping} and a {@link Service} bound to it.
 *
 * @param <I> the {@link Request} type
 * @param <O> the {@link Response} type
 */
public final class CompositeServiceEntry<I extends Request, O extends Response> {

    /**
     * Creates a new {@link CompositeServiceEntry} whose {@link Service} is bound at the path that matches
     * the specified regular expression.
     *
     * @see PathMapping#ofRegex(Pattern)
     */
    public static <I extends Request, O extends Response>
    CompositeServiceEntry<I, O> ofRegex(Pattern regex, Service<I, O> service) {
        return new CompositeServiceEntry<>(PathMapping.ofRegex(regex), service);
    }

    /**
     * Creates a new {@link CompositeServiceEntry} whose {@link Service} is bound at the path that matches
     * the specified glob pattern.
     *
     * @see PathMapping#ofGlob(String)
     */
    public static <I extends Request, O extends Response>
    CompositeServiceEntry<I, O> ofGlob(String glob, Service<I, O> service) {
        return new CompositeServiceEntry<>(PathMapping.ofGlob(glob), service);
    }

    /**
     * Creates a new {@link CompositeServiceEntry} whose {@link Service} is bound under the specified
     * directory.
     *
     * @see PathMapping#ofPrefix(String)
     */
    public static <I extends Request, O extends Response>
    CompositeServiceEntry<I, O> ofPrefix(String pathPrefix, Service<I, O> service) {
        return new CompositeServiceEntry<>(PathMapping.ofPrefix(pathPrefix), service);
    }

    /**
     * Creates a new {@link CompositeServiceEntry} whose {@link Service} is bound at the specified exact path.
     *
     * @see PathMapping#ofExact(String)
     */
    public static <I extends Request, O extends Response>
    CompositeServiceEntry<I, O> ofExact(String exactPath, Service<I, O> service) {
        return new CompositeServiceEntry<>(PathMapping.ofExact(exactPath), service);
    }

    /**
     * Creates a new {@link CompositeServiceEntry} whose {@link Service} is bound at
     * {@linkplain PathMapping#ofCatchAll() the catch-all path mapping}.
     */
    public static <I extends Request, O extends Response>
    CompositeServiceEntry<I, O> ofCatchAll(Service<I, O> service) {
        return new CompositeServiceEntry<>(PathMapping.ofCatchAll(), service);
    }

    /**
     * Creates a new {@link CompositeServiceEntry} whose {@link Service} is bound at the specified path pattern.
     *
     * @see PathMapping#of(String)
     */
    public static <I extends Request, O extends Response>
    CompositeServiceEntry<I, O> of(String pathPattern, Service<I, O> service) {
        return new CompositeServiceEntry<>(PathMapping.of(pathPattern), service);
    }

    /**
     * Creates a new {@link CompositeServiceEntry} with the specified {@link PathMapping} and {@link Service}.
     */
    public static <I extends Request, O extends Response>
    CompositeServiceEntry<I, O> of(PathMapping pathMapping, Service<I, O> service) {
        return new CompositeServiceEntry<>(pathMapping, service);
    }

    private final PathMapping pathMapping;
    private final Service<I, O> service;

    private CompositeServiceEntry(PathMapping pathMapping, Service<I, O> service) {
        this.pathMapping = requireNonNull(pathMapping, "pathMapping");
        this.service = requireNonNull(service, "service");
    }

    /**
     * Returns the {@link PathMapping} of the {@link #service()}.
     */
    public PathMapping pathMapping() {
        return pathMapping;
    }

    /**
     * Returns the {@link Service}.
     */
    public Service<I, O> service() {
        return service;
    }

    @Override
    public String toString() {
        return pathMapping + " -> " + service;
    }
}
