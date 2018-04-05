package com.ctrlshift.client;

import com.ctrlshift.commons.SerializationFormat;

/**
 * Creates a new {@link ClientFactory} dynamically via Java SPI (Service Provider Interface).
 */
@FunctionalInterface
public interface ClientFactoryProvider {
    /**
     * Creates a new {@link ClientFactory}.
     *
     * @param httpClientFactory the core {@link ClientFactory} which is capable of handling the
     *                          {@link SerializationFormat#NONE "none"} serialization format.
     */
    ClientFactory newFactory(ClientFactory httpClientFactory);
}
