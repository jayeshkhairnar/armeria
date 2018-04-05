package com.ctrlshift.client.pool;

import io.netty.channel.Channel;

/**
 * A skeletal {@link KeyedChannelPoolHandler} implementation to minimize the effort to implement this interface.
 * Extend this class to implement only few of the provided handler methods.
 *
 * @param <K> the key type
 */
public class KeyedChannelPoolHandlerAdapter<K> implements KeyedChannelPoolHandler<K> {

    static final KeyedChannelPoolHandlerAdapter<Object> NOOP = new KeyedChannelPoolHandlerAdapter<Object>() {};

    /**
     * Creates a new instance.
     */
    protected KeyedChannelPoolHandlerAdapter() {}

    @Override
    public void channelReleased(K key, Channel ch) throws Exception {}

    @Override
    public void channelAcquired(K key, Channel ch) throws Exception {}

    @Override
    public void channelCreated(K key, Channel ch) throws Exception {}

    @Override
    public void channelClosed(K key, Channel ch) throws Exception {}
}