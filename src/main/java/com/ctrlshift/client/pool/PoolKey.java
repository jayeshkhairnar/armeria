package com.ctrlshift.client.pool;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

import com.ctrlshift.commons.SessionProtocol;

/**
 * The default key of {@link KeyedChannelPool}. It consists of:
 * <ul>
 *   <li>the server's host name</li>
 *   <li>the server's IP address, if resolved</li>
 *   <li>the server's port number</li>
 *   <li>the server's {@link SessionProtocol}</li>
 * </ul>
 */
public final class PoolKey {

    private final String host;
    @Nullable
    private final String ipAddr;
    private final int port;
    private final SessionProtocol sessionProtocol;

    /**
     * Creates a new key with the specified {@code host}, {@code ipAddr}, {@code port} and
     * {@code sessionProtocol}.
     */
    public PoolKey(String host, @Nullable String ipAddr, int port, SessionProtocol sessionProtocol) {
        this.host = requireNonNull(host, "host");
        this.ipAddr = ipAddr;
        this.port = port;
        this.sessionProtocol = requireNonNull(sessionProtocol, "sessionProtocol");
    }

    /**
     * Returns the host name of the server associated with this key.
     */
    public String host() {
        return host;
    }

    /**
     * Returns the IP address of the server associated with this key.
     *
     * @return the IP address, or {@code null} if the host name is not resolved.
     */
    @Nullable
    public String ipAddr() {
        return ipAddr;
    }

    /**
     * Returns the port number of the server associated with this key.
     */
    public int port() {
        return port;
    }

    /**
     * Returns the {@link SessionProtocol} of the server associated with this key.
     */
    public SessionProtocol sessionProtocol() {
        return sessionProtocol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PoolKey)) {
            return false;
        }

        final PoolKey that = (PoolKey) o;
        return host.equals(that.host) && Objects.equals(ipAddr, that.ipAddr) &&
               port == that.port && sessionProtocol == that.sessionProtocol;
    }

    @Override
    public int hashCode() {
        return ((host.hashCode() * 31 + Objects.hashCode(ipAddr)) * 31 + port) * 31 +
               sessionProtocol.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                          .add("sessionProtocol", sessionProtocol.uriText())
                          .add("host", host)
                          .add("ipAddr", ipAddr)
                          .add("port", port)
                          .toString();
    }
}