package com.ctrlshift.client;

import java.net.URI;
import java.nio.charset.Charset;

import com.ctrlshift.commons.AggregatedHttpMessage;
import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.HttpMethod;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;

/**
 * An HTTP client.
 */
public interface HttpClient extends ClientBuilderParams {

    /**
     * Creates a new HTTP client that connects to the specified {@code uri} using the default
     * {@link ClientFactory}.
     *
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptionValue}s
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(String uri, ClientOptionValue<?>... options) {
        return of(ClientFactory.DEFAULT, uri, options);
    }

    /**
     * Creates a new HTTP client that connects to the specified {@code uri} using the default
     * {@link ClientFactory}.
     *
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptions}
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(String uri, ClientOptions options) {
        return of(ClientFactory.DEFAULT, uri, options);
    }

    /**
     * Creates a new HTTP client that connects to the specified {@code uri} using an alternative
     * {@link ClientFactory}.
     *
     * @param factory an alternative {@link ClientFactory}
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptionValue}s
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(ClientFactory factory, String uri, ClientOptionValue<?>... options) {
        return new HttpClientBuilder(uri).factory(factory).options(options).build();
    }

    /**
     * Creates a new HTTP client that connects to the specified {@code uri} using an alternative
     * {@link ClientFactory}.
     *
     * @param factory an alternative {@link ClientFactory}
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptions}
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(ClientFactory factory, String uri, ClientOptions options) {
        return new HttpClientBuilder(uri).factory(factory).options(options).build();
    }

    /**
     * Creates a new HTTP client that connects to the specified {@link URI} using the default
     * {@link ClientFactory}.
     *
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptionValue}s
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(URI uri, ClientOptionValue<?>... options) {
        return of(ClientFactory.DEFAULT, uri, options);
    }

    /**
     * Creates a new HTTP client that connects to the specified {@link URI} using the default
     * {@link ClientFactory}.
     *
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptions}
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(URI uri, ClientOptions options) {
        return of(ClientFactory.DEFAULT, uri, options);
    }

    /**
     * Creates a new HTTP client that connects to the specified {@link URI} using an alternative
     * {@link ClientFactory}.
     *
     * @param factory an alternative {@link ClientFactory}
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptionValue}s
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(ClientFactory factory, URI uri, ClientOptionValue<?>... options) {
        return new HttpClientBuilder(uri).factory(factory).options(options).build();
    }

    /**
     * Creates a new HTTP client that connects to the specified {@link URI} using an alternative
     * {@link ClientFactory}.
     *
     * @param factory an alternative {@link ClientFactory}
     * @param uri the URI of the server endpoint
     * @param options the {@link ClientOptions}
     *
     * @throws IllegalArgumentException if the scheme of the specified {@code uri} is not an HTTP scheme
     */
    static HttpClient of(ClientFactory factory, URI uri, ClientOptions options) {
        return new HttpClientBuilder(uri).factory(factory).options(options).build();
    }

    /**
     * Sends the specified HTTP request.
     */
    HttpResponse execute(HttpRequest req);

    /**
     * Sends the specified HTTP request.
     */
    HttpResponse execute(AggregatedHttpMessage aggregatedReq);

    /**
     * Sends an empty HTTP request with the specified headers.
     */
    default HttpResponse execute(HttpHeaders headers) {
        return execute(AggregatedHttpMessage.of(headers));
    }

    /**
     * Sends an HTTP request with the specified headers and content.
     */
    default HttpResponse execute(HttpHeaders headers, HttpData content) {
        return execute(AggregatedHttpMessage.of(headers, content));
    }

    /**
     * Sends an HTTP request with the specified headers and content.
     */
    default HttpResponse execute(HttpHeaders headers, byte[] content) {
        return execute(AggregatedHttpMessage.of(headers, HttpData.of(content)));
    }

    /**
     * Sends an HTTP request with the specified headers and content.
     */
    default HttpResponse execute(HttpHeaders headers, String content) {
        return execute(AggregatedHttpMessage.of(headers, HttpData.ofUtf8(content)));
    }

    /**
     * Sends an HTTP request with the specified headers and content.
     */
    default HttpResponse execute(HttpHeaders headers, String content, Charset charset) {
        return execute(AggregatedHttpMessage.of(headers, HttpData.of(charset, content)));
    }

    /**
     * Sends an HTTP OPTIONS request.
     */
    default HttpResponse options(String path) {
        return execute(HttpHeaders.of(HttpMethod.OPTIONS, path));
    }

    /**
     * Sends an HTTP GET request.
     */
    default HttpResponse get(String path) {
        return execute(HttpHeaders.of(HttpMethod.GET, path));
    }

    /**
     * Sends an HTTP HEAD request.
     */
    default HttpResponse head(String path) {
        return execute(HttpHeaders.of(HttpMethod.HEAD, path));
    }

    /**
     * Sends an HTTP POST request with the specified content.
     */
    default HttpResponse post(String path, HttpData content) {
        return execute(HttpHeaders.of(HttpMethod.POST, path), content);
    }

    /**
     * Sends an HTTP POST request with the specified content.
     */
    default HttpResponse post(String path, byte[] content) {
        return execute(HttpHeaders.of(HttpMethod.POST, path), content);
    }

    /**
     * Sends an HTTP POST request with the specified content.
     */
    default HttpResponse post(String path, String content) {
        return execute(HttpHeaders.of(HttpMethod.POST, path), HttpData.ofUtf8(content));
    }

    /**
     * Sends an HTTP POST request with the specified content.
     */
    default HttpResponse post(String path, String content, Charset charset) {
        return execute(HttpHeaders.of(HttpMethod.POST, path), content, charset);
    }

    /**
     * Sends an HTTP PUT request with the specified content.
     */
    default HttpResponse put(String path, HttpData content) {
        return execute(HttpHeaders.of(HttpMethod.PUT, path), content);
    }

    /**
     * Sends an HTTP PUT request with the specified content.
     */
    default HttpResponse put(String path, byte[] content) {
        return execute(HttpHeaders.of(HttpMethod.PUT, path), content);
    }

    /**
     * Sends an HTTP PUT request with the specified content.
     */
    default HttpResponse put(String path, String content) {
        return execute(HttpHeaders.of(HttpMethod.PUT, path), HttpData.ofUtf8(content));
    }

    /**
     * Sends an HTTP PUT request with the specified content.
     */
    default HttpResponse put(String path, String content, Charset charset) {
        return execute(HttpHeaders.of(HttpMethod.PUT, path), content, charset);
    }

    /**
     * Sends an HTTP PATCH request with the specified content.
     */
    default HttpResponse patch(String path, HttpData content) {
        return execute(HttpHeaders.of(HttpMethod.PATCH, path), content);
    }

    /**
     * Sends an HTTP PATCH request with the specified content.
     */
    default HttpResponse patch(String path, byte[] content) {
        return execute(HttpHeaders.of(HttpMethod.PATCH, path), content);
    }

    /**
     * Sends an HTTP PATCH request with the specified content.
     */
    default HttpResponse patch(String path, String content) {
        return execute(HttpHeaders.of(HttpMethod.PATCH, path), HttpData.ofUtf8(content));
    }

    /**
     * Sends an HTTP PATCH request with the specified content.
     */
    default HttpResponse patch(String path, String content, Charset charset) {
        return execute(HttpHeaders.of(HttpMethod.PATCH, path), content, charset);
    }

    /**
     * Sends an HTTP DELETE request.
     */
    default HttpResponse delete(String path) {
        return execute(HttpHeaders.of(HttpMethod.DELETE, path));
    }

    /**
     * Sends an HTTP TRACE request.
     */
    default HttpResponse trace(String path) {
        return execute(HttpHeaders.of(HttpMethod.TRACE, path));
    }
}