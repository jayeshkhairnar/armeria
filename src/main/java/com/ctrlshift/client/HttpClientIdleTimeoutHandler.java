package com.ctrlshift.client;

import com.ctrlshift.internal.IdleTimeoutHandler;

import io.netty.channel.ChannelHandlerContext;

final class HttpClientIdleTimeoutHandler extends IdleTimeoutHandler {

    HttpClientIdleTimeoutHandler(long idleTimeoutMillis) {
        super("client", idleTimeoutMillis);
    }

    @Override
    protected boolean hasRequestsInProgress(ChannelHandlerContext ctx) {
        return HttpSession.get(ctx.channel()).hasUnfinishedResponses();
    }
}