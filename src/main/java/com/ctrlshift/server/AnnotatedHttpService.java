package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.base.MoreObjects;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;

/**
 * {@link PathMapping} and their corresponding {@link BiFunction}.
 */
final class AnnotatedHttpService implements HttpService {

    /**
     * Path param extractor with placeholders, e.g., "/const1/{var1}/{var2}/const2"
     */
    private final HttpHeaderPathMapping pathMapping;

    /**
     * The {@link AnnotatedHttpServiceMethod} that will handle the request actually.
     */
    private final AnnotatedHttpServiceMethod delegate;

    /**
     * A decorator of this service.
     */
    private final Function<Service<HttpRequest, HttpResponse>,
            ? extends Service<HttpRequest, HttpResponse>> decorator;

    /**
     * Creates a new instance.
     */
    AnnotatedHttpService(HttpHeaderPathMapping pathMapping,
                         AnnotatedHttpServiceMethod delegate,
                         Function<Service<HttpRequest, HttpResponse>,
                                 ? extends Service<HttpRequest, HttpResponse>> decorator) {
        this.pathMapping = requireNonNull(pathMapping, "pathMapping");
        this.delegate = requireNonNull(delegate, "delegate");
        this.decorator = requireNonNull(decorator, "decorator");
    }

    /**
     * Returns the {@link PathMapping} of this service.
     */
    HttpHeaderPathMapping pathMapping() {
        return pathMapping;
    }

    /**
     * Returns the decorator of this service.
     */
    Function<Service<HttpRequest, HttpResponse>,
            ? extends Service<HttpRequest, HttpResponse>> decorator() {
        return decorator;
    }

    /**
     * Returns whether it's mapping overlaps with given {@link AnnotatedHttpService} instance.
     */
    boolean overlaps(AnnotatedHttpService entry) {
        return false;
        // FIXME(trustin): Make the path overlap detection work again.
        //return !Sets.intersection(methods, entry.methods).isEmpty() &&
        //       pathMapping.skeleton().equals(entry.pathMapping.skeleton());
    }

    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        return HttpResponse.from(delegate.serve(ctx, req));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("pathMapping", pathMapping)
                          .add("delegate", delegate)
                          .add("decorator", decorator)
                          .toString();
    }
}
