package com.ctrlshift.client;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.SerializationFormat;
import com.ctrlshift.commons.SessionProtocol;

/**
 * Creates a new HTTP client that connects to the specified {@link URI} using the builder pattern.
 * Use the factory methods in {@link HttpClient} if you do not have many options to override.
 * Please refer to {@link ClientBuilder} for how decorators and HTTP headers are configured
 */
public final class HttpClientBuilder extends AbstractClientOptionsBuilder<HttpClientBuilder> {

    private final URI uri;
    private ClientFactory factory = ClientFactory.DEFAULT;

    /**
     * Creates a new instance.
     *
     * @throws IllegalArgumentException if the scheme of the uri is not one of the fields
     *                                  in {@link SessionProtocol} or the uri violates RFC 2396
     */
    public HttpClientBuilder(String uri) {
        this(URI.create(requireNonNull(uri, "uri")));
    }

    /**
     * Creates a new instance.
     *
     * @throws IllegalArgumentException if the scheme of the uri is not one of the fields
     *                                  in {@link SessionProtocol}
     */
    public HttpClientBuilder(URI uri) {
        validateScheme(requireNonNull(uri, "uri").getScheme());
        this.uri = URI.create(SerializationFormat.NONE + "+" + uri.toString());
    }

    private static void validateScheme(String scheme) {
        for (SessionProtocol p : SessionProtocol.values()) {
            if (scheme.equalsIgnoreCase(p.uriText())) {
                return;
            }
        }
        throw new IllegalArgumentException("scheme : " + scheme + " (expected: one of " +
                                           ImmutableList.copyOf(SessionProtocol.values()) + ")");
    }

    /**
     * Sets the {@link ClientFactory} of the client. The default is {@link ClientFactory#DEFAULT}.
     */
    public HttpClientBuilder factory(ClientFactory factory) {
        this.factory = requireNonNull(factory, "factory");
        return this;
    }

    /**
     * Adds the specified {@code decorator}.
     *
     * @param decorator the {@link Function} that transforms a {@link Client} to another
     */
    public HttpClientBuilder decorator(
            Function<? extends Client<HttpRequest, HttpResponse>, ? extends Client<HttpRequest, HttpResponse>>
                    decorator) {
        return super.decorator(HttpRequest.class, HttpResponse.class, decorator);
    }

    /**
     * Adds the specified {@code decorator}.
     *
     * @param decorator the {@link DecoratingClientFunction} that intercepts an invocation
     */
    public HttpClientBuilder decorator(DecoratingClientFunction<HttpRequest, HttpResponse> decorator) {
        return super.decorator(HttpRequest.class, HttpResponse.class, decorator);
    }

    /**
     * Returns a newly-created HTTP client based on the properties of this builder.
     *
     * @throws IllegalArgumentException if the scheme of the {@code uri} specified in
     *                                  {@link #HttpClientBuilder(String)} or {@link #HttpClientBuilder(URI)}
     *                                  is not an HTTP scheme
     */
    public HttpClient build() {
        return factory.newClient(uri, HttpClient.class, buildOptions());
    }
}
