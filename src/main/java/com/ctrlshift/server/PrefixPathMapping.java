package com.ctrlshift.server;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

final class PrefixPathMapping extends AbstractPathMapping {

    static final String PREFIX = "prefix:";
    static final int PREFIX_LEN = PREFIX.length();

    private final String prefix;
    private final boolean stripPrefix;
    private final String loggerName;
    private final String meterTag;
    private final Optional<String> triePath;
    private final String strVal;

    PrefixPathMapping(String prefix, boolean stripPrefix) {
        prefix = ensureAbsolutePath(prefix, "prefix");
        if (!prefix.endsWith("/")) {
            prefix += '/';
        }

        this.prefix = prefix;
        this.stripPrefix = stripPrefix;
        loggerName = loggerName(prefix);
        meterTag = PREFIX + prefix;
        triePath = Optional.of(prefix + '*');
        strVal = PREFIX + prefix + " (stripPrefix: " + stripPrefix + ')';
    }

    @Override
    protected PathMappingResult doApply(PathMappingContext mappingCtx) {
        final String path = mappingCtx.path();
        if (!path.startsWith(prefix)) {
            return PathMappingResult.empty();
        }

        return PathMappingResult.of(stripPrefix ? path.substring(prefix.length() - 1) : path,
                                    mappingCtx.query());
    }

    @Override
    public Set<String> paramNames() {
        return ImmutableSet.of();
    }

    @Override
    public String loggerName() {
        return loggerName;
    }

    @Override
    public String meterTag() {
        return meterTag;
    }

    @Override
    public Optional<String> triePath() {
        return triePath;
    }

    @Override
    public int hashCode() {
        return stripPrefix ? prefix.hashCode() : -prefix.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PrefixPathMapping)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final PrefixPathMapping that = (PrefixPathMapping) obj;
        return stripPrefix == that.stripPrefix && prefix.equals(that.prefix);
    }

    @Override
    public Optional<String> prefix() {
        return Optional.of(prefix);
    }

    @Override
    public String toString() {
        return strVal;
    }
}
