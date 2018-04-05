package com.ctrlshift.server.file;

import static java.util.Objects.requireNonNull;

import java.time.Clock;

/**
 * {@link HttpFileService} configuration.
 */
public final class HttpFileServiceConfig {

    private final HttpVfs vfs;
    private final Clock clock;
    private final int maxCacheEntries;
    private final int maxCacheEntrySizeBytes;
    private final boolean serveCompressedFiles;

    HttpFileServiceConfig(HttpVfs vfs, Clock clock, int maxCacheEntries, int maxCacheEntrySizeBytes,
                          boolean serveCompressedFiles) {
        this.vfs = requireNonNull(vfs, "vfs");
        this.clock = requireNonNull(clock, "clock");
        this.maxCacheEntries = validateMaxCacheEntries(maxCacheEntries);
        this.maxCacheEntrySizeBytes = validateMaxCacheEntrySizeBytes(maxCacheEntrySizeBytes);
        this.serveCompressedFiles = serveCompressedFiles;
    }

    static int validateMaxCacheEntries(int maxCacheEntries) {
        return validateNonNegativeParameter(maxCacheEntries, "maxCacheEntries");
    }

    static int validateMaxCacheEntrySizeBytes(int maxCacheEntrySizeBytes) {
        return validateNonNegativeParameter(maxCacheEntrySizeBytes, "maxCacheEntrySizeBytes");
    }

    private static int validateNonNegativeParameter(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + ": " + value + " (expected: >= 0)");
        }
        return value;
    }

    /**
     * Returns the {@link HttpVfs} that provides the static files to an {@link HttpFileService}.
     */
    public HttpVfs vfs() {
        return vfs;
    }

    /**
     * Returns the {@link Clock} the provides the current date and time to an {@link HttpFileService}.
     */
    public Clock clock() {
        return clock;
    }

    /**
     * Returns the maximum allowed number of cached file entries.
     */
    public int maxCacheEntries() {
        return maxCacheEntries;
    }

    /**
     * Returns the maximum allowed size of a cached file entry. Files bigger than this value will not be
     * cached.
     */
    public int maxCacheEntrySizeBytes() {
        return maxCacheEntrySizeBytes;
    }

    /**
     * Whether pre-compressed files should be served.
     */
    public boolean serveCompressedFiles() {
        return serveCompressedFiles;
    }

    @Override
    public String toString() {
        return toString(this, vfs(), clock(), maxCacheEntries(), maxCacheEntrySizeBytes());
    }

    static String toString(Object holder, HttpVfs vfs, Clock clock,
                           int maxCacheEntries, int maxCacheEntrySizeBytes) {

        return holder.getClass().getSimpleName() +
               "(vfs: " + vfs +
               ", clock: " + clock +
               ", maxCacheEntries: " + maxCacheEntries +
               ", maxCacheEntrySizeBytes: " + maxCacheEntrySizeBytes + ')';
    }
}
