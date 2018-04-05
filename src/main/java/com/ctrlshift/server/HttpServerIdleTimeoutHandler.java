package com.ctrlshift.server;

import com.ctrlshift.internal.IdleTimeoutHandler;

import io.netty.channel.ChannelHandlerContext;

final class HttpServerIdleTimeoutHandler extends IdleTimeoutHandler {

    HttpServerIdleTimeoutHandler(long idleTimeoutMillis) {
        super("server", idleTimeoutMillis);
    }

    @Override
    protected boolean hasRequestsInProgress(ChannelHandlerContext ctx) {
        final HttpServer server = HttpServer.get(ctx);
        return server != null && server.unfinishedRequests() != 0;
    }
}