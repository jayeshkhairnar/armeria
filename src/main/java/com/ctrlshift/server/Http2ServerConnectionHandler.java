package com.ctrlshift.server;

import com.ctrlshift.internal.AbstractHttp2ConnectionHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Settings;

final class Http2ServerConnectionHandler extends AbstractHttp2ConnectionHandler {

    Http2ServerConnectionHandler(
            Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
            Http2Settings initialSettings) {

        super(decoder, encoder, initialSettings);
    }

    @Override
    protected void onCloseRequest(ChannelHandlerContext ctx) throws Exception {}
}