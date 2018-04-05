package com.ctrlshift.server;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;

/**
 * An HTTP/2 {@link Service}.
 *
 * <p>This interface is merely a shortcut to {@code Service<HttpRequest, HttpResponse>} at the moment.
 */
@FunctionalInterface
public interface HttpService extends Service<HttpRequest, HttpResponse> {
    @Override
    HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception;
}
