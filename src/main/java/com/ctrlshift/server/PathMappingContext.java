package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpMethod;
import com.ctrlshift.commons.MediaType;

/**
 * Holds the parameters which are required to find a service available to handle the request.
 */
public interface PathMappingContext {

    /**
     * Returns the {@link VirtualHost} instance which belongs to this {@link PathMappingContext}.
     */
    VirtualHost virtualHost();

    /**
     * Returns the virtual host name of the request.
     */
    String hostname();

    /**
     * Returns {@link HttpMethod} of the request.
     */
    HttpMethod method();

    /**
     * Returns the absolute path retrieved from the request,
     * as defined in <a href="https://tools.ietf.org/html/rfc3986">RFC3986</a>.
     */
    String path();

    /**
     * Returns the query retrieved from the request,
     * as defined in <a href="https://tools.ietf.org/html/rfc3986">RFC3986</a>.
     */
    @Nullable
    String query();

    /**
     * Returns {@link MediaType} specified by 'Content-Type' header of the request.
     */
    @Nullable
    MediaType consumeType();

    /**
     * Returns a list of {@link MediaType} that is ordered by client-side preferences.
     * It only includes the media types supported by the virtual host. {@code null} would be returned
     * if the virtual host does not support media type negotiation.
     */
    @Nullable
    List<MediaType> produceTypes();

    /**
     * Returns an identifier of this {@link PathMappingContext} instance.
     * It would be used as a cache key to reduce pattern list traversal.
     */
    List<Object> summary();

    /**
     * Delays throwing a {@link Throwable} until reaching the end of the service list.
     */
    void delayThrowable(Throwable cause);

    /**
     * Returns a delayed {@link Throwable} set before via {@link #delayThrowable(Throwable)}.
     */
    Optional<Throwable> delayedThrowable();

    /**
     * Returns a wrapped {@link PathMappingContext} which holds the specified {@code path}.
     * It is usually used to find a {@link Service} with a prefix-stripped path.
     */
    default PathMappingContext overridePath(String path) {
        requireNonNull(path, "path");
        return new PathMappingContextWrapper(this) {
            @Override
            public String path() {
                return path;
            }
        };
    }
}