package com.ctrlshift.commons;

public abstract class AbstractHttpData implements HttpData {

    /**
     * Gets the {@link byte} value at the given {@code index} relative to the {@link HttpData}'s
     * {@link #offset()}.
     */
    protected abstract byte getByte(int index);

    @Override
    public int hashCode() {
        int hash = 1;
        for (int i = 0; i < length(); i++) {
            hash = hash * 31 + getByte(i);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractHttpData)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final AbstractHttpData that = (AbstractHttpData) obj;
        if (length() != that.length()) {
            return false;
        }

        for (int i = 0; i < length(); i++) {
            if (getByte(i) != that.getByte(i)) {
                return false;
            }
        }

        return true;
    }
}
