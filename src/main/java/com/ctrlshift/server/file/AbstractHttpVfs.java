package com.ctrlshift.server.file;

public abstract class AbstractHttpVfs implements HttpVfs {

    /**
     * Returns the {@link #meterTag()} of this {@link HttpVfs}.
     */
    @Override
    public String toString() {
        return meterTag();
    }
}
