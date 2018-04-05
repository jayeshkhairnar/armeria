package com.ctrlshift.server;

import javax.annotation.Nullable;

import com.ctrlshift.commons.SessionProtocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

interface HttpServer {

    @Nullable
    static HttpServer get(Channel channel) {
        final ChannelPipeline p = channel.pipeline();
        final ChannelHandler lastHandler = p.last();
        if (lastHandler instanceof HttpServer) {
            return (HttpServer) lastHandler;
        }

        for (ChannelHandler h : p.toMap().values()) {
            if (h instanceof HttpServer) {
                return (HttpServer) h;
            }
        }

        return null;
    }

    @Nullable
    static HttpServer get(ChannelHandlerContext ctx) {
        return get(ctx.channel());
    }

    SessionProtocol protocol();

    int unfinishedRequests();
}
