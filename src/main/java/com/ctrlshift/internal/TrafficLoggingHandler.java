package com.ctrlshift.internal;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * A Netty handler that logs the I/O traffic of a connection.
 */
public final class TrafficLoggingHandler extends LoggingHandler {

    public static final TrafficLoggingHandler SERVER = new TrafficLoggingHandler(true);
    public static final TrafficLoggingHandler CLIENT = new TrafficLoggingHandler(false);

    private TrafficLoggingHandler(boolean server) {
        super("com.linecorp.armeria.logging.traffic." + (server ? "server" : "client"), LogLevel.TRACE);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf && !((ByteBuf) msg).isReadable()) {
            ctx.write(msg, promise);
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
