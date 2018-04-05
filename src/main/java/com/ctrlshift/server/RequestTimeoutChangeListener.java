package com.ctrlshift.server;

@FunctionalInterface
public interface RequestTimeoutChangeListener {
    /**
     * Invoked when the request timeout of the current request has been changed.
     *
     * @param newRequestTimeoutMillis the new timeout value in milliseconds. {@code 0} if disabled.
     */
    void onRequestTimeoutChange(long newRequestTimeoutMillis);
}
