package com.ctrlshift.client;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ctrlshift.client.ClientDecoration.Entry;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.Response;
import com.ctrlshift.commons.RpcRequest;
import com.ctrlshift.commons.RpcResponse;

/**
 * Creates a new {@link ClientDecoration} using the builder pattern.
 */
public final class ClientDecorationBuilder {

    private final List<Entry<?, ?>> entries = new ArrayList<>();

    /**
     * Adds a new decorator {@link Function}.
     *
     * @param requestType the type of the {@link Request} that the {@code decorator} is interested in
     * @param responseType the type of the {@link Response} that the {@code decorator} is interested in
     * @param decorator the {@link Function} that transforms a {@link Client} to another
     * @param <T> the type of the {@link Client} being decorated
     * @param <R> the type of the {@link Client} produced by the {@code decorator}
     * @param <I> the {@link Request} type of the {@link Client} being decorated
     * @param <O> the {@link Response} type of the {@link Client} being decorated
     */
    public <T extends Client<I, O>, R extends Client<I, O>, I extends Request, O extends Response>
    ClientDecorationBuilder add(Class<I> requestType, Class<O> responseType, Function<T, R> decorator) {

        requireNonNull(requestType, "requestType");
        requireNonNull(responseType, "responseType");
        requireNonNull(decorator, "decorator");

        if (!(requestType == HttpRequest.class && responseType == HttpResponse.class ||
              requestType == RpcRequest.class && responseType == RpcResponse.class)) {
            throw new IllegalArgumentException(
                    "requestType and responseType must be HttpRequest and HttpResponse or " +
                    "RpcRequest and RpcResponse: " + requestType.getName() + " and " + responseType.getName());
        }

        entries.add(new Entry<>(requestType, responseType, decorator));
        return this;
    }

    /**
     * Adds a new {@link DecoratingClientFunction}.
     *
     * @param requestType the type of the {@link Request} that the {@code decorator} is interested in
     * @param responseType the type of the {@link Response} that the {@code decorator} is interested in
     * @param decorator the {@link DecoratingClientFunction} that intercepts an invocation
     * @param <I> the {@link Request} type of the {@link Client} being decorated
     * @param <O> the {@link Response} type of the {@link Client} being decorated
     */
    public <I extends Request, O extends Response> ClientDecorationBuilder add(
            Class<I> requestType, Class<O> responseType, DecoratingClientFunction<I, O> decorator) {

        requireNonNull(requestType, "requestType");
        requireNonNull(responseType, "responseType");
        requireNonNull(decorator, "decorator");

        entries.add(new Entry<>(requestType, responseType,
                                delegate -> new FunctionalDecoratingClient<>(delegate, decorator)));
        return this;
    }

    /**
     * Returns a newly-created {@link ClientDecoration} based on the decorators added to this builder.
     */
    public ClientDecoration build() {
        return new ClientDecoration(entries);
    }
}
