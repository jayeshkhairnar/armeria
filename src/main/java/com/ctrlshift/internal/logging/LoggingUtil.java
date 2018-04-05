package com.ctrlshift.internal.logging;

import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;

import com.ctrlshift.commons.HttpHeaders;

import io.netty.channel.Channel;

/**
 * A utility class for logging.
 */
public final class LoggingUtil {

    /**
     * Returns a remote host from the specified {@link HttpHeaders} and {@link Channel}.
     */
    public static String remoteHost(HttpHeaders headers, Channel channel) {
        requireNonNull(headers, "headers");
        requireNonNull(channel, "channel");
        String host = headers.authority();
        if (host == null) {
            host = ((InetSocketAddress) channel.remoteAddress()).getHostString();
        } else {
            final int colonIdx = host.lastIndexOf(':');
            if (colonIdx > 0) {
                host = host.substring(0, colonIdx);
            }
        }
        return host;
    }

    private LoggingUtil() {}
}