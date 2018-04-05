package com.ctrlshift.client;

import javax.annotation.Nullable;

import com.ctrlshift.commons.ClosedSessionException;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.SessionProtocol;
import com.ctrlshift.internal.InboundTrafficController;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

interface HttpSession {

    HttpSession INACTIVE = new HttpSession() {

        private final InboundTrafficController inboundTrafficController =
                new InboundTrafficController(null, 0, 0);

        @Nullable
        @Override
        public SessionProtocol protocol() {
            return null;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public InboundTrafficController inboundTrafficController() {
            return inboundTrafficController;
        }

        @Override
        public boolean hasUnfinishedResponses() {
            return false;
        }

        @Override
        public boolean invoke(ClientRequestContext ctx, HttpRequest req, DecodedHttpResponse res) {
            res.close(ClosedSessionException.get());
            return false;
        }

        @Override
        public void retryWithH1C() {
            throw new IllegalStateException();
        }

        @Override
        public void deactivate() {}
    };

    static HttpSession get(Channel ch) {
        final ChannelHandler lastHandler = ch.pipeline().last();
        if (lastHandler instanceof HttpSession) {
            return (HttpSession) lastHandler;
        }

        for (ChannelHandler h : ch.pipeline().toMap().values()) {
            if (h instanceof HttpSession) {
                return (HttpSession) h;
            }
        }

        return INACTIVE;
    }

    @Nullable
    SessionProtocol protocol();

    boolean isActive();

    InboundTrafficController inboundTrafficController();

    boolean hasUnfinishedResponses();

    boolean invoke(ClientRequestContext ctx, HttpRequest req, DecodedHttpResponse res);

    void retryWithH1C();

    void deactivate();
}
