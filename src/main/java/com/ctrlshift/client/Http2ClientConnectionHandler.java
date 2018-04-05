package com.ctrlshift.client;

import com.ctrlshift.internal.AbstractHttp2ConnectionHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Settings;

final class Http2ClientConnectionHandler extends AbstractHttp2ConnectionHandler {

    private final Http2ResponseDecoder responseDecoder;

    Http2ClientConnectionHandler(
            Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
            Http2Settings initialSettings, Http2ResponseDecoder responseDecoder) {

        super(decoder, encoder, initialSettings);
        this.responseDecoder = responseDecoder;
        connection().addListener(responseDecoder);
        decoder().frameListener(responseDecoder);
    }

    Http2ResponseDecoder responseDecoder() {
        return responseDecoder;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        // NB: Http2ConnectionHandler does not flush the preface string automatically.
        ctx.flush();
    }

    @Override
    protected void onCloseRequest(ChannelHandlerContext ctx) throws Exception {
        HttpSession.get(ctx.channel()).deactivate();
    }
}
