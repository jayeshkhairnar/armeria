package com.ctrlshift.client.pool;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

interface KeyedPool<K, V> extends AutoCloseable {
    Future<V> acquire(K key);

    Future<V> acquire(K key, Promise<V> promise);

    Future<Void> release(K key, V value);

    Future<Void> release(K key, V value, Promise<Void> promise);

    @Override
    void close();
}