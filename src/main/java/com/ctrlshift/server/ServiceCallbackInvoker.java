package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

/**
 * A helper class that invokes the callback methods in {@link Service}.
 */
public final class ServiceCallbackInvoker {

    /**
     * Invokes {@link Service#serviceAdded(ServiceConfig)}.
     */
    public static void invokeServiceAdded(ServiceConfig cfg, Service<?, ?> service) {
        requireNonNull(cfg, "cfg");
        requireNonNull(service, "service");

        try {
            service.serviceAdded(cfg);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "failed to invoke serviceAdded() on: " + service, e);
        }
    }

    private ServiceCallbackInvoker() {}
}
