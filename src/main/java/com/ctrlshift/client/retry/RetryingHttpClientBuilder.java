package com.ctrlshift.client.retry;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.function.Function;

import com.ctrlshift.client.Client;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;

/**
 * Builds a new {@link RetryingHttpClient} or its decorator function.
 */
public class RetryingHttpClientBuilder extends RetryingClientBuilder<
        RetryingHttpClientBuilder, RetryingHttpClient, HttpRequest, HttpResponse> {

    private static final int DEFAULT_CONTENT_PREVIEW_LENGTH = 0; // no preview (0 byte)

    private boolean useRetryAfter;

    private int contentPreviewLength = DEFAULT_CONTENT_PREVIEW_LENGTH;

    /**
     * Creates a new builder with the specified retry strategy.
     */
    public RetryingHttpClientBuilder(RetryStrategy<HttpRequest, HttpResponse> retryStrategy) {
        super(retryStrategy);
    }

    /**
     * Whether retry should be attempted according to the {@code retryHeader} from the server or not.
     * The web server may request a client to retry after specific time with {@code retryAfter} header.
     * If you want to follow the direction from the server not by your {@link Backoff}, invoke this method
     * with the {@code useRetryAfter} with {@code true} to request after the specified delay.
     *
     * @param useRetryAfter {@code true} if you want to retry after using the {@code retryAfter} header.
     *                      {@code false} otherwise
     * @return {@link RetryingHttpClientBuilder} to support method chaining
     */
    public RetryingHttpClientBuilder useRetryAfter(boolean useRetryAfter) {
        this.useRetryAfter = useRetryAfter;
        return self();
    }

    /**
     * Sets the length of content to look up whether retry or not. If the total length of content exceeds
     * this and there's no retry condition matched, it will hand over the stream to the client.
     * @param contentPreviewLength the content length to preview. Unlike other length related arguments,
     *                             {@code 0} does not disable the length limit. It means that it will not
     *                             look up the content
     * @return {@link RetryingHttpClientBuilder} to support method chaining
     */
    public RetryingHttpClientBuilder contentPreviewLength(int contentPreviewLength) {
        checkArgument(contentPreviewLength >= 0,
                      "contentPreviewLength: %s (expected: >= 0)", contentPreviewLength);
        this.contentPreviewLength = contentPreviewLength;
        return self();
    }

    /**
     * Returns a newly-created {@link RetryingHttpClient} based on the properties of this builder.
     */
    @Override
    public RetryingHttpClient build(Client<HttpRequest, HttpResponse> delegate) {
        return new RetryingHttpClient(delegate, retryStrategy, maxTotalAttempts,
                                      responseTimeoutMillisForEachAttempt, useRetryAfter, contentPreviewLength);
    }

    /**
     * Returns a newly-created decorator that decorates a {@link Client} with a new {@link RetryingHttpClient}
     * based on the properties of this builder.
     */
    @Override
    public Function<Client<HttpRequest, HttpResponse>, RetryingHttpClient> newDecorator() {
        return delegate -> new RetryingHttpClient(
                delegate, retryStrategy, maxTotalAttempts, responseTimeoutMillisForEachAttempt,
                useRetryAfter, contentPreviewLength);
    }

    @Override
    public String toString() {
        return toStringHelper().add("useRetryAfter", useRetryAfter).toString();
    }
}
