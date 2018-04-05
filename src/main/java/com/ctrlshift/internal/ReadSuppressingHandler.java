package com.ctrlshift.internal;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;

/**
 * A {@link ChannelOutboundHandler} that suppresses unnecessary {@link ChannelHandlerContext#read()} calls
 * when auto-read is disabled.
 */
@Sharable
public final class ReadSuppressingHandler extends ChannelOutboundHandlerAdapter {

    /**
     * The singleton {@link ReadSuppressingHandler} instance.
     */
    public static final ReadSuppressingHandler INSTANCE = new ReadSuppressingHandler();

    private ReadSuppressingHandler() {}

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().config().isAutoRead()) {
            super.read(ctx);
        }
    }
}
