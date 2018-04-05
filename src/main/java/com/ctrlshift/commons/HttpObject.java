package com.ctrlshift.commons;

public interface HttpObject {

    /**
     * Gets whether the stream should be ended when writing this object. This can be useful for
     * {@link HttpHeaders}-only responses or to more efficiently close the stream along with the last piece of
     * {@link HttpData}. This only has meaning for {@link HttpObject} writers, not readers.
     */
    boolean isEndOfStream();
}
