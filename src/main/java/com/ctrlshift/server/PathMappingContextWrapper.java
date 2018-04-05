package com.ctrlshift.server;

import static com.ctrlshift.server.DefaultPathMappingContext.generateSummary;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpMethod;
import com.ctrlshift.commons.MediaType;

/**
 * A wrapper class of {@link PathMappingContext}. This would be used to override a parameter of an
 * existing {@link PathMappingContext} instance.
 */
class PathMappingContextWrapper implements PathMappingContext {

    private final PathMappingContext delegate;
    @Nullable
    private List<Object> summary;

    PathMappingContextWrapper(PathMappingContext delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    @Override
    public VirtualHost virtualHost() {
        return delegate().virtualHost();
    }

    @Override
    public String hostname() {
        return delegate().hostname();
    }

    @Override
    public HttpMethod method() {
        return delegate().method();
    }

    @Override
    public String path() {
        return delegate().path();
    }

    @Nullable
    @Override
    public String query() {
        return delegate().query();
    }

    @Nullable
    @Override
    public MediaType consumeType() {
        return delegate().consumeType();
    }

    @Nullable
    @Override
    public List<MediaType> produceTypes() {
        return delegate().produceTypes();
    }

    @Override
    public List<Object> summary() {
        if (summary == null) {
            summary = generateSummary(this);
        }
        return summary;
    }

    @Override
    public void delayThrowable(Throwable cause) {
        delegate().delayThrowable(cause);
    }

    @Override
    public Optional<Throwable> delayedThrowable() {
        return delegate().delayedThrowable();
    }

    @Override
    public int hashCode() {
        return summary().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PathMappingContext && summary().equals(obj);
    }

    @Override
    public String toString() {
        return summary().toString();
    }

    protected final PathMappingContext delegate() {
        return delegate;
    }
}