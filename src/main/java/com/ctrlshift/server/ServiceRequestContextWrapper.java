package com.ctrlshift.server;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.commons.MediaType;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.RequestContextWrapper;

/**
 * Wraps an existing {@link ServiceRequestContext}.
 */
public class ServiceRequestContextWrapper
        extends RequestContextWrapper<ServiceRequestContext> implements ServiceRequestContext {

    /**
     * Creates a new instance.
     */
    protected ServiceRequestContextWrapper(ServiceRequestContext delegate) {
        super(delegate);
    }

    @Override
    public ServiceRequestContext newDerivedContext() {
        return delegate().newDerivedContext();
    }

    @Override
    public ServiceRequestContext newDerivedContext(Request request) {
        return delegate().newDerivedContext(request);
    }

    @Override
    public Server server() {
        return delegate().server();
    }		

    @Override
    public VirtualHost virtualHost() {
        return delegate().virtualHost();
    }

    @Override
    public PathMapping pathMapping() {
        return delegate().pathMapping();
    }

    @Override
    public PathMappingContext pathMappingContext() {
        return delegate().pathMappingContext();
    }

    @Override
    public Map<String, String> pathParams() {
        return delegate().pathParams();
    }

    @Override
    public <T extends Service<HttpRequest, HttpResponse>> T service() {
        return delegate().service();
    }

    @Override
    public ExecutorService blockingTaskExecutor() {
        return delegate().blockingTaskExecutor();
    }

    @Override
    public String mappedPath() {
        return delegate().mappedPath();
    }

    @Nullable
    @Override
    public MediaType negotiatedProduceType() {
        return delegate().negotiatedProduceType();
    }

    @Override
    public Logger logger() {
        return delegate().logger();
    }

    @Override
    public long requestTimeoutMillis() {
        return delegate().requestTimeoutMillis();
    }

    @Override
    public void setRequestTimeoutMillis(long requestTimeoutMillis) {
        delegate().setRequestTimeoutMillis(requestTimeoutMillis);
    }

    @Override
    public void setRequestTimeout(Duration requestTimeout) {
        delegate().setRequestTimeout(requestTimeout);
    }

    @Override
    public void setRequestTimeoutHandler(Runnable requestTimeoutHandler) {
        delegate().setRequestTimeoutHandler(requestTimeoutHandler);
    }

    @Override
    public long maxRequestLength() {
        return delegate().maxRequestLength();
    }

    @Override
    public void setMaxRequestLength(long maxRequestLength) {
        delegate().setMaxRequestLength(maxRequestLength);
    }
}