package com.ctrlshift.client;

import javax.annotation.Nullable;

import com.ctrlshift.commons.DefaultHttpResponse;
import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.HttpObject;
import com.ctrlshift.internal.InboundTrafficController;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.EventExecutor;

final class DecodedHttpResponse extends DefaultHttpResponse {

    private final EventLoop eventLoop;
    @Nullable
    private InboundTrafficController inboundTrafficController;
    private long writtenBytes;

    DecodedHttpResponse(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    void init(InboundTrafficController inboundTrafficController) {
        this.inboundTrafficController = inboundTrafficController;
    }

    long writtenBytes() {
        return writtenBytes;
    }

    @Override
    protected EventExecutor defaultSubscriberExecutor() {
        return eventLoop;
    }

    @Override
    public boolean tryWrite(HttpObject obj) {
        final boolean published = super.tryWrite(obj);
        if (published && obj instanceof HttpData) {
            final int length = ((HttpData) obj).length();
            assert inboundTrafficController != null;
            inboundTrafficController.inc(length);
            writtenBytes += length;
        }
        return published;
    }

    @Override
    protected void onRemoval(HttpObject obj) {
        if (obj instanceof HttpData) {
            final int length = ((HttpData) obj).length();
            assert inboundTrafficController != null;
            inboundTrafficController.dec(length);
        }
    }
}