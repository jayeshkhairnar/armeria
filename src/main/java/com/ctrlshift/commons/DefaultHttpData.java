package com.ctrlshift.commons;

import com.google.common.base.MoreObjects;

/**
 * Default {@link HttpData} implementation.
 */
public final class DefaultHttpData extends AbstractHttpData {

    private final byte[] data;
    private final int offset;
    private final int length;
    private final boolean endOfStream;

    /**
     * Creates a new instance.
     */
    public DefaultHttpData(byte[] data, int offset, int length, boolean endOfStream) {
        this.data = data;
        this.offset = offset;
        this.length = length;
        this.endOfStream = endOfStream;
    }

    @Override
    public byte[] array() {
        return data;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    @SuppressWarnings("ImplicitArrayToString")
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("offset", offset)
                          .add("length", length)
                          .add("array", data.toString()).toString();
    }

    @Override
    public boolean isEndOfStream() {
        return endOfStream;
    }

    @Override
    protected byte getByte(int index) {
        return data[offset + index];
    }
}
