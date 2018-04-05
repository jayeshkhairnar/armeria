package com.ctrlshift.commons;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import io.netty.handler.codec.Headers;

/**
 * HTTP parameters map.
 */
public interface HttpParameters extends Headers<String, String, HttpParameters> {

    /**
     * An immutable empty HTTP parameters map.
     */
    HttpParameters EMPTY_PARAMETERS = of().asImmutable();

    /**
     * Returns a new empty HTTP parameters map.
     */
    static HttpParameters of() {
        return new DefaultHttpParameters();
    }

    /**
     * Returns a new HTTP parameters map with the specified {@code Map<String, ? extends Iterable<String>>}.
     */
    static HttpParameters copyOf(Map<String, ? extends Iterable<String>> parameters) {
        requireNonNull(parameters, "parameters");
        final HttpParameters httpParameters = new DefaultHttpParameters();
        parameters.forEach((name, values) ->
                                   values.forEach(value -> httpParameters.add(name, value)));
        return httpParameters;
    }

    /**
     * Returns a copy of the specified {@code Headers<String, String, ?>}.
     */
    static HttpParameters copyOf(Headers<String, String, ?> parameters) {
        return of().set(requireNonNull(parameters, "parameters"));
    }

    /**
     * Returns the immutable view of this parameters map.
     */
    default HttpParameters asImmutable() {
        return new ImmutableHttpParameters(this);
    }
}
