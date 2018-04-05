package com.ctrlshift.client.pool;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

final class KeyedChannelPoolUtil {

    @SuppressWarnings("rawtypes")
    static final AttributeKey<KeyedChannelPool> POOL = AttributeKey.valueOf(KeyedChannelPool.class, "POOL");

    @SuppressWarnings("unchecked")
    static <K> KeyedChannelPool<K> findPool(Channel ch) {
        return (KeyedChannelPool<K>) ch.attr(POOL).get();
    }

    private KeyedChannelPoolUtil() {}
}