package com.ctrlshift.internal;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;

public final class InboundTrafficController extends AtomicInteger {

    private static final long serialVersionUID = 420503276551000218L;

    private static int numDeferredReads;

    public static int numDeferredReads() {
        return numDeferredReads;
    }

    @Nullable
    private final ChannelConfig cfg;
    private final int highWatermark;
    private final int lowWatermark;
    private volatile boolean suspended;

    public InboundTrafficController(@Nullable Channel channel) {
        this(channel, 128 * 1024, 64 * 1024);
    }

    public InboundTrafficController(@Nullable Channel channel, int highWatermark, int lowWatermark) {
        cfg = channel != null ? channel.config() : null;
        this.highWatermark = highWatermark;
        this.lowWatermark = lowWatermark;
    }

    public void inc(int numProducedBytes) {
        final int oldValue = getAndAdd(numProducedBytes);
        if (oldValue <= highWatermark && oldValue + numProducedBytes > highWatermark) {
            // Just went above high watermark
            if (cfg != null) {
                cfg.setAutoRead(false);
                numDeferredReads++;
                suspended = true;
            }
        }
    }

    public void dec(int numConsumedBytes) {
        final int oldValue = getAndAdd(-numConsumedBytes);
        if (oldValue > lowWatermark && oldValue - numConsumedBytes <= lowWatermark) {
            // Just went below high watermark
            if (cfg != null) {
                cfg.setAutoRead(true);
                suspended = false;
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("suspended", suspended)
                          .add("unconsumed", get())
                          .add("watermarks", highWatermark + "/" + lowWatermark)
                          .toString();
    }
}