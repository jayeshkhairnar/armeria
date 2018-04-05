package com.ctrlshift.server;

import java.io.OutputStream;

import com.ctrlshift.commons.metric.MeterIdPrefix;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Finds a mapping that matches a given {@link PathMappingContext}.
 */
public interface Router<V> {

    /**
     * Finds the value of mapping that matches the specified {@link PathMappingContext}.
     *
     * @return a {@link PathMapped} that wraps the matching value if there's a match.
     *         {@link PathMapped#empty()} if there's no match.
     */
    PathMapped<V> find(PathMappingContext mappingCtx);

    /**
     * Registers the stats of this {@link Router} to the specified {@link MeterRegistry}.
     *
     * @return whether the stats of this {@link Router} has been registered
     */
    default boolean registerMetrics(MeterRegistry registry, MeterIdPrefix idPrefix) {
        return false;
    }

    /**
     * Dumps the content of this {@link Router}.
     */
    void dump(OutputStream output);
}