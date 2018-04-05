package com.ctrlshift.commons.util;

public interface ReleasableHolder<T> {

    /**
     * Returns the resource.
     */
    T get();

    /**
     * Releases the resource.
     */
    void release();
}