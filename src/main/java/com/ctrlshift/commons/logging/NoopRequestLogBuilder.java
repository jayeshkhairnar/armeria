package com.ctrlshift.commons.logging;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.SerializationFormat;
import com.ctrlshift.commons.SessionProtocol;

import io.netty.channel.Channel;

final class NoopRequestLogBuilder implements RequestLogBuilder {

    @Override
    public void addChild(RequestLog child) {}

    @Override
    public void endResponseWithLastChild() {}

    @Override
    public void startRequest(Channel ch, SessionProtocol sessionProtocol, String host) {}

    @Override
    public void serializationFormat(SerializationFormat serializationFormat) {}

    @Override
    public void increaseRequestLength(long deltaBytes) {}

    @Override
    public void requestLength(long requestLength) {}

    @Override
    public void requestHeaders(HttpHeaders requestHeaders) {}

    @Override
    public void requestContent(@Nullable Object requestContent, @Nullable Object rawRequestContent) {}

    @Override
    public void deferRequestContent() {}

    @Override
    public boolean isRequestContentDeferred() {
        return false;
    }

    @Override
    public void endRequest() {}

    @Override
    public void endRequest(Throwable requestCause) {}

    @Override
    public void startResponse() {}

    @Override
    public void increaseResponseLength(long deltaBytes) {}

    @Override
    public void responseLength(long responseLength) {}

    @Override
    public void responseHeaders(HttpHeaders responseHeaders) {}

    @Override
    public void responseContent(@Nullable Object responseContent, @Nullable Object rawResponseContent) {}

    @Override
    public void deferResponseContent() {}

    @Override
    public boolean isResponseContentDeferred() {
        return false;
    }

    @Override
    public void endResponse() {}

    @Override
    public void endResponse(Throwable responseCause) {}
}