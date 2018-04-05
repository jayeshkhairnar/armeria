package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import com.ctrlshift.commons.metric.MeterIdPrefix;
import com.ctrlshift.commons.util.Exceptions;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * A {@link Router} implementation that enables composing multiple {@link Router}s into one.
 */
final class CompositeRouter<I, O> implements Router<O> {

    private final List<Router<I>> delegates;
    private final Function<PathMapped<I>, PathMapped<O>> resultMapper;

    CompositeRouter(Router<I> delegate, Function<PathMapped<I>, PathMapped<O>> resultMapper) {
        this(ImmutableList.of(requireNonNull(delegate, "delegate")), resultMapper);
    }

    CompositeRouter(List<Router<I>> delegates, Function<PathMapped<I>, PathMapped<O>> resultMapper) {
        this.delegates = requireNonNull(delegates, "delegates");
        this.resultMapper = requireNonNull(resultMapper, "resultMapper");
    }

    @Override
    public PathMapped<O> find(PathMappingContext mappingCtx) {
        for (Router<I> delegate : delegates) {
            final PathMapped<I> result = delegate.find(mappingCtx);
            if (result.isPresent()) {
                return resultMapper.apply(result);
            }
        }
        mappingCtx.delayedThrowable().ifPresent(Exceptions::throwUnsafely);
        return PathMapped.empty();
    }

    @Override
    public boolean registerMetrics(MeterRegistry registry, MeterIdPrefix idPrefix) {
        final int numDelegates = delegates.size();
        switch (numDelegates) {
            case 0:
                return false;
            case 1:
                return delegates.get(0).registerMetrics(registry, idPrefix);
            default:
                boolean registered = false;
                for (int i = 0; i < numDelegates; i++) {
                    final MeterIdPrefix delegateIdPrefix = idPrefix.withTags("index", String.valueOf(i));
                    if (delegates.get(i).registerMetrics(registry, delegateIdPrefix)) {
                        registered = true;
                    }
                }
                return registered;
        }
    }

    @Override
    public void dump(OutputStream output) {
        delegates.forEach(delegate -> delegate.dump(output));
    }
}