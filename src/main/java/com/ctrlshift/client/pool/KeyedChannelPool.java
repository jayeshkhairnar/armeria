package com.ctrlshift.client.pool;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

/**
 * An asynchronous {@link Channel} pool that provides key-based acquisition and release.
 *
 * @param <K> the type of the key, usually {@link PoolKey}
 */
public interface KeyedChannelPool<K> extends KeyedPool<K, Channel> {

    /**
     * Finds the {@link KeyedChannelPool} that the specified {@link Channel} belongs to.
     *
     * @return {@code null} if the specified {@link Channel} does not belong to any {@link KeyedChannelPool}
     */
    static <K> KeyedChannelPool<K> findPool(Channel channel) {
        return KeyedChannelPoolUtil.findPool(channel);
    }

    /**
     * Acquires a {@link Channel} with the specified {@code key}.
     */
    @Override
    Future<Channel> acquire(K key);

    /**
     * Acquires a {@link Channel} with the specified {@code key} and get notified with the specified
     * {@link Promise}.
     */
    @Override
    Future<Channel> acquire(K key, Promise<Channel> promise);

    /**
     * Releases the acquired {@link Channel} to the pool.
     */
    @Override
    Future<Void> release(K key, Channel value);

    /**
     * Releases the acquired {@link Channel} to the pool and get notified with the specified {@link Promise}.
     */
    @Override
    Future<Void> release(K key, Channel value, Promise<Void> promise);

    /**
     * Closes all {@link Channel}s managed by this pool.
     */
    @Override
    void close();
}
