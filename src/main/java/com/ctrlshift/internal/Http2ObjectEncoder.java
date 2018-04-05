package com.ctrlshift.internal;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpData;
import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.stream.ClosedPublisherException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.ReferenceCountUtil;

public final class Http2ObjectEncoder extends HttpObjectEncoder {

    private final Http2ConnectionEncoder encoder;

    public Http2ObjectEncoder(Http2ConnectionEncoder encoder) {
        this.encoder = requireNonNull(encoder, "encoder");
    }

    @Override
    protected ChannelFuture doWriteHeaders(
            ChannelHandlerContext ctx, int id, int streamId, HttpHeaders headers, boolean endStream) {

        final ChannelFuture future = validateStream(ctx, streamId);
        if (future != null) {
            return future;
        }

        return encoder.writeHeaders(
                ctx, streamId, ArmeriaHttpUtil.toNettyHttp2(headers), 0, endStream, ctx.newPromise());
    }

    @Override
    protected ChannelFuture doWriteData(
            ChannelHandlerContext ctx, int id, int streamId, HttpData data, boolean endStream) {

        final ChannelFuture future = validateStream(ctx, streamId);
        if (future != null) {
            ReferenceCountUtil.safeRelease(data);
            return future;
        }

        return encoder.writeData(ctx, streamId, toByteBuf(ctx, data), 0, endStream, ctx.newPromise());
    }

    @Override
    protected ChannelFuture doWriteReset(ChannelHandlerContext ctx, int id, int streamId, Http2Error error) {
        final ChannelFuture future = validateStream(ctx, streamId);
        if (future != null) {
            return future;
        }

        return encoder.writeRstStream(ctx, streamId, error.code(), ctx.newPromise());
    }

    @Nullable
    private ChannelFuture validateStream(ChannelHandlerContext ctx, int streamId) {
        final Http2Stream stream = encoder.connection().stream(streamId);
        if (stream != null) {
            switch (stream.state()) {
                case RESERVED_LOCAL:
                case OPEN:
                case HALF_CLOSED_REMOTE:
                    break;
                default:
                    // The response has been sent already.
                    return ctx.newFailedFuture(ClosedPublisherException.get());
            }
        } else if (encoder.connection().streamMayHaveExisted(streamId)) {
            // Stream has been removed because it has been closed completely.
            return ctx.newFailedFuture(ClosedPublisherException.get());
        }

        return null;
    }

    @Override
    protected void doClose() {}
}