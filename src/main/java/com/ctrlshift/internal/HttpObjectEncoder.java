package com.ctrlshift.internal;

import com.ctrlshift.commons.ClosedSessionException;
import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.HttpObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.util.ReferenceCountUtil;

/**
 * Converts an {@link HttpObject} into a protocol-specific object and writes it into a {@link Channel}.
 */
public abstract class HttpObjectEncoder {

    private volatile boolean closed;

    /**
     * Writes an {@link HttpHeaders}.
     */
    public final ChannelFuture writeHeaders(
            ChannelHandlerContext ctx, int id, int streamId, HttpHeaders headers, boolean endStream) {

        assert ctx.channel().eventLoop().inEventLoop();

        if (closed) {
            return newFailedFuture(ctx);
        }

        return doWriteHeaders(ctx, id, streamId, headers, endStream);
    }

    protected abstract ChannelFuture doWriteHeaders(
            ChannelHandlerContext ctx, int id, int streamId, HttpHeaders headers, boolean endStream);

    /**
     * Writes an {@link HttpData}.
     */
    public final ChannelFuture writeData(
            ChannelHandlerContext ctx, int id, int streamId, HttpData data, boolean endStream) {

        assert ctx.channel().eventLoop().inEventLoop();

        if (closed) {
            ReferenceCountUtil.safeRelease(data);
            return newFailedFuture(ctx);
        }

        return doWriteData(ctx, id, streamId, data, endStream);
    }

    protected abstract ChannelFuture doWriteData(
            ChannelHandlerContext ctx, int id, int streamId, HttpData data, boolean endStream);

    /**
     * Resets the specified stream. If the session protocol does not support multiplexing or the connection
     * is in unrecoverable state, the connection will be closed. For example, in an HTTP/1 connection, this
     * will lead the connection to be closed immediately or after the previous requests that are not reset.
     */
    public final ChannelFuture writeReset(ChannelHandlerContext ctx, int id, int streamId, Http2Error error) {

        if (closed) {
            return newFailedFuture(ctx);
        }

        return doWriteReset(ctx, id, streamId, error);
    }

    protected abstract ChannelFuture doWriteReset(
            ChannelHandlerContext ctx, int id, int streamId, Http2Error error);

    /**
     * Releases the resources related with this encoder and fails any unfinished writes.
     */
    public void close() {
        if (closed) {
            return;
        }

        closed = true;
        doClose();
    }

    protected abstract void doClose();

    private static ChannelFuture newFailedFuture(ChannelHandlerContext ctx) {
        return ctx.newFailedFuture(ClosedSessionException.get());
    }

    protected static ByteBuf toByteBuf(ChannelHandlerContext ctx, HttpData data) {
        if (data instanceof ByteBufHolder) {
            return ((ByteBufHolder) data).content();
        }
        final ByteBuf buf = ctx.alloc().directBuffer(data.length(), data.length());
        buf.writeBytes(data.array(), data.offset(), data.length());
        return buf;
    }
}
