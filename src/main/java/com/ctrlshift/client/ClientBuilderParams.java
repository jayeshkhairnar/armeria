package com.ctrlshift.client;

import java.net.URI;

/**
 * Provides the construction parameters of a client.
 */
public interface ClientBuilderParams {
    /**
     * Returns the {@link ClientFactory} who created the client.
     */
    ClientFactory factory();

    /**
     * Returns the endpoint URI of the client.
     */
    URI uri();

    /**
     * Returns the type of the client.
     */
    Class<?> clientType();

    /**
     * Returns the options of the client.
     */
    ClientOptions options();
}
