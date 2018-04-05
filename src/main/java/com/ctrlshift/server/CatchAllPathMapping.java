package com.ctrlshift.server;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

final class CatchAllPathMapping extends AbstractPathMapping {

    static final CatchAllPathMapping INSTANCE = new CatchAllPathMapping();

    private static final Optional<String> PREFIX_PATH_OPT = Optional.of("/");
    private static final String LOGGER_NAME = loggerName("/"); // "__ROOT__"

    private CatchAllPathMapping() {}

    @Override
    protected PathMappingResult doApply(PathMappingContext mappingCtx) {
        return PathMappingResult.of(mappingCtx.path(), mappingCtx.query());
    }

    @Override
    public Set<String> paramNames() {
        return ImmutableSet.of();
    }

    @Override
    public String loggerName() {
        return LOGGER_NAME;
    }

    @Override
    public String meterTag() {
        return "catch-all";
    }

    @Override
    public Optional<String> prefix() {
        return PREFIX_PATH_OPT;
    }

    @Override
    public String toString() {
        return "catchAll";
    }
}