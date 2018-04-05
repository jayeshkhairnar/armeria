package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

/**
 * Builds a new {@link VirtualHost}.
 *
 * <p>This class can only be created through the {@link ServerBuilder#withDefaultVirtualHost()} or
 * {@link ServerBuilder#withVirtualHost(String)} method of the {@link ServerBuilder}.
 *
 * <p>Call {@link #and()} method can also return to {@link ServerBuilder}.
 *
 * @see ServerBuilder
 * @see PathMapping
 * @see VirtualHostBuilder
 */
public final class ChainedVirtualHostBuilder extends AbstractVirtualHostBuilder<ChainedVirtualHostBuilder> {

    private final ServerBuilder serverBuilder;

    /**
     * Creates a new {@link ChainedVirtualHostBuilder} whose hostname pattern is {@code "*"} (match-all).
     *
     * @param serverBuilder the parent {@link ServerBuilder} to be returned by {@link #and()}.
     */
    ChainedVirtualHostBuilder(ServerBuilder serverBuilder) {
        requireNonNull(serverBuilder, "serverBuilder");
        this.serverBuilder = serverBuilder;
    }

    /**
     * Creates a new {@link ChainedVirtualHostBuilder} with the specified hostname pattern.
     *
     * @param hostnamePattern the hostname pattern of this virtual host.
     * @param serverBuilder the parent {@link ServerBuilder} to be returned by {@link #and()}.
     */
    ChainedVirtualHostBuilder(String hostnamePattern, ServerBuilder serverBuilder) {
        super(hostnamePattern);

        requireNonNull(serverBuilder, "serverBuilder");
        this.serverBuilder = serverBuilder;
    }

    /**
     * Creates a new {@link ChainedVirtualHostBuilder} with
     * the default host name and the specified hostname pattern.
     *
     * @param defaultHostname the default hostname of this virtual host.
     * @param hostnamePattern the hostname pattern of this virtual host.
     * @param serverBuilder the parent {@link ServerBuilder} to be returned by {@link #and()}.
     */
    ChainedVirtualHostBuilder(String defaultHostname, String hostnamePattern, ServerBuilder serverBuilder) {
        super(defaultHostname, hostnamePattern);

        requireNonNull(serverBuilder, "serverBuilder");
        this.serverBuilder = serverBuilder;
    }

    /**
     * Returns the parent {@link ServerBuilder}.
     *
     * @return serverBuilder the parent {@link ServerBuilder}.
     */
    public ServerBuilder and() {
        return serverBuilder;
    }
}
