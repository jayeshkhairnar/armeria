package com.ctrlshift.client.pool;

import io.netty.channel.Channel;

/**
 * Handles the events produced by {@link KeyedChannelPool}.
 *
 * @param <K> the key type
 */
public interface KeyedChannelPoolHandler<K> {

    /**
     * Returns an instance that does nothing.
     */
    @SuppressWarnings("unchecked")
    static <K> KeyedChannelPoolHandler<K> noop() {
        return (KeyedChannelPoolHandler<K>) KeyedChannelPoolHandlerAdapter.NOOP;
    }

    /**
     * Invoked when the specified {@code channel} has been created for the specified {@code key}.
     */
    void channelCreated(K key, Channel ch) throws Exception;

    /**
     * Invoked when the specified {@code channel} has been acquired from the pool.
     */
    void channelAcquired(K key, Channel ch) throws Exception;

    /**
     * Invoked when the specified {@code channel} has been released to the pool.
     */
    void channelReleased(K key, Channel ch) throws Exception;

    /**
     * Invoked when the specified {@code channel} has been closed and removed from the pool.
     */
    void channelClosed(K key, Channel ch) throws Exception;
}