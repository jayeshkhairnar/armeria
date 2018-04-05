package com.ctrlshift.server;

import javax.annotation.Nullable;

import com.ctrlshift.commons.DefaultHttpRequest;
import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.HttpObject;
import com.ctrlshift.internal.InboundTrafficController;

import io.netty.channel.EventLoop;

final class DecodedHttpRequest extends DefaultHttpRequest {

    private final EventLoop eventLoop;
    private final int id;
    private final int streamId;
    private final boolean keepAlive;
    private final InboundTrafficController inboundTrafficController;
    private final long defaultMaxRequestLength;
    @Nullable
    private ServiceRequestContext ctx;
    private long transferredBytes;

    DecodedHttpRequest(EventLoop eventLoop, int id, int streamId, HttpHeaders headers, boolean keepAlive,
                       InboundTrafficController inboundTrafficController, long defaultMaxRequestLength) {

        super(headers);

        this.eventLoop = eventLoop;
        this.id = id;
        this.streamId = streamId;
        this.keepAlive = keepAlive;
        this.inboundTrafficController = inboundTrafficController;
        this.defaultMaxRequestLength = defaultMaxRequestLength;
    }

    void init(ServiceRequestContext ctx) {
        this.ctx = ctx;
        ctx.logBuilder().requestHeaders(headers());
    }

    int id() {
        return id;
    }

    int streamId() {
        return streamId;
    }

    /**
     * Returns whether to keep the connection alive after this request is handled.
     */
    boolean isKeepAlive() {
        return keepAlive;
    }

    long maxRequestLength() {
        return ctx != null ? ctx.maxRequestLength() : defaultMaxRequestLength;
    }

    long transferredBytes() {
        return transferredBytes;
    }

    void increaseTransferredBytes(long delta) {
        if (transferredBytes > Long.MAX_VALUE - delta) {
            transferredBytes = Long.MAX_VALUE;
        } else {
            transferredBytes += delta;
        }
    }

    @Override
    protected EventLoop defaultSubscriberExecutor() {
        return eventLoop;
    }

    @Override
    public boolean tryWrite(HttpObject obj) {
        final boolean published = super.tryWrite(obj);
        if (published && obj instanceof HttpData) {
            final int length = ((HttpData) obj).length();
            inboundTrafficController.inc(length);
            assert ctx != null : "uninitialized DecodedHttpRequest must be aborted.";
            ctx.logBuilder().requestLength(transferredBytes);
        }
        return published;
    }

    @Override
    protected void onRemoval(HttpObject obj) {
        if (obj instanceof HttpData) {
            final int length = ((HttpData) obj).length();
            inboundTrafficController.dec(length);
        }
    }
}
