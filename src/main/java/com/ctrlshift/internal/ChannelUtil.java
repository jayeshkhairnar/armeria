package com.ctrlshift.internal;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableList;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public final class ChannelUtil {

    public static CompletableFuture<Void> close(Iterable<? extends Channel> channels) {
        final List<Channel> channelsCopy = ImmutableList.copyOf(channels);
        if (channelsCopy.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        final AtomicInteger numChannelsToClose = new AtomicInteger(channelsCopy.size());
        final CompletableFuture<Void> future = new CompletableFuture<>();
        final ChannelFutureListener listener = unused -> {
            if (numChannelsToClose.decrementAndGet() == 0) {
                future.complete(null);
            }
        };

        for (Channel ch : channelsCopy) {
            ch.close().addListener(listener);
        }

        return future;
    }

    private ChannelUtil() {}
}