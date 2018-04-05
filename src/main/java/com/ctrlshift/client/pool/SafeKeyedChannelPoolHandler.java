package com.ctrlshift.client.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

/**
 * Helper Handler delegate event to decorated  {@link KeyedChannelPoolHandler}.
 * Ignore Exception when created {@link KeyedChannelPoolHandler} throw Exception.s
 */
class SafeKeyedChannelPoolHandler<K> implements KeyedChannelPoolHandler<K> {
    private static final Logger logger = LoggerFactory.getLogger(SafeKeyedChannelPoolHandler.class);

    private final KeyedChannelPoolHandler<K> handler;

    SafeKeyedChannelPoolHandler(KeyedChannelPoolHandler<K> handler) {
        this.handler = handler;
    }

    private static void logFailure(String handlerName, Throwable cause) {
        logger.warn("Exception handling {}()", handlerName, cause);
    }

    @Override
    public void channelReleased(K key, Channel ch) {
        try {
            handler.channelReleased(key, ch);
        } catch (Exception e) {
            logFailure("channelReleased", e);
        }
    }

    @Override
    public void channelAcquired(K key, Channel ch) {
        try {
            handler.channelAcquired(key, ch);
        } catch (Exception e) {
            logFailure("channelAcquired", e);
        }
    }

    @Override
    public void channelCreated(K key, Channel ch) {
        try {
            handler.channelCreated(key, ch);
        } catch (Exception e) {
            logFailure("channelCreated", e);
        }
    }

    @Override
    public void channelClosed(K key, Channel ch) {
        try {
            handler.channelClosed(key, ch);
        } catch (Exception e) {
            logFailure("channelClosed", e);
        }
    }
}
