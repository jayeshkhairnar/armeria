package com.ctrlshift.client;

import static com.ctrlshift.internal.ArmeriaHttpUtil.concatPaths;

import javax.annotation.Nullable;

import com.ctrlshift.commons.AggregatedHttpMessage;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.HttpResponseWriter;
import com.ctrlshift.commons.SessionProtocol;
import com.ctrlshift.internal.PathAndQuery;

import io.micrometer.core.instrument.MeterRegistry;
import io.netty.channel.EventLoop;

final class DefaultHttpClient extends UserClient<HttpRequest, HttpResponse> implements HttpClient {

    DefaultHttpClient(ClientBuilderParams params, Client<HttpRequest, HttpResponse> delegate,
                      MeterRegistry meterRegistry, SessionProtocol sessionProtocol, Endpoint endpoint) {
        super(params, delegate, meterRegistry, sessionProtocol, endpoint);
    }

    @Override
    public HttpResponse execute(HttpRequest req) {
        return execute(null, req);
    }

    private HttpResponse execute(@Nullable EventLoop eventLoop, HttpRequest req) {
        final String concatPaths = concatPaths(uri().getRawPath(), req.path());
        req.path(concatPaths);

        final PathAndQuery pathAndQuery = PathAndQuery.parse(concatPaths);
        if (pathAndQuery == null) {
            req.abort();
            return HttpResponse.ofFailure(new IllegalArgumentException("invalid path: " + concatPaths));
        }

        return execute(eventLoop, req.method(), pathAndQuery.path(), pathAndQuery.query(), null, req, cause -> {
            final HttpResponseWriter res = HttpResponse.streaming();
            res.close(cause);
            return res;
        });
    }

    @Override
    public HttpResponse execute(AggregatedHttpMessage aggregatedReq) {
        return execute(null, aggregatedReq);
    }

    HttpResponse execute(@Nullable EventLoop eventLoop, AggregatedHttpMessage aggregatedReq) {
        return execute(eventLoop, HttpRequest.of(aggregatedReq));
    }
}
