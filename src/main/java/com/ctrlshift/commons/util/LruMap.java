package com.ctrlshift.commons.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) cache {@link Map}.
 *
 * @param <T> the key type
 * @param <U> the value type
 */
public class LruMap<T, U> extends LinkedHashMap<T, U> {
    private static final long serialVersionUID = 5358379908010214089L;

    private final int maxEntries;

    /**
     * Creates a new instance with the specified maximum number of allowed entries.
     */
    public LruMap(int maxEntries) {
        super(maxEntries, 0.75f, true);
        this.maxEntries = maxEntries;
    }

    /**
     * Returns {@code true} if the {@link #size()} of this map exceeds the maximum number of allowed entries.
     * Invoked by {@link LinkedHashMap} for LRU behavior.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<T, U> eldest) {
        return size() > maxEntries;
    }
}
