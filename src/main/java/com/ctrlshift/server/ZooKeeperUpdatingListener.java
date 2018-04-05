package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import com.ctrlshift.client.Endpoint;
import com.ctrlshift.server.Server;
import com.ctrlshift.server.ServerListenerAdapter;

/**
 * A ZooKeeper Server Listener. When you add this listener, server will be automatically registered
 * into the ZooKeeper.
 */
public class ZooKeeperUpdatingListener extends ServerListenerAdapter {
    private final String zkConnectionStr;
    private final String zNodePath;
    private final int sessionTimeout;
    @Nullable
    private Endpoint endpoint;
    @Nullable
    private ZooKeeperRegistration connector;

    /**
     * A ZooKeeper server listener, used for register server into ZooKeeper.
     * @param zkConnectionStr ZooKeeper connection string
     * @param zNodePath       ZooKeeper node path(under which this server will registered)
     * @param sessionTimeout  session timeout
     * @param endpoint        the endpoint of the server being registered
     */
    public ZooKeeperUpdatingListener(String zkConnectionStr, String zNodePath, int sessionTimeout,
                                     Endpoint endpoint) {
        this.zkConnectionStr = requireNonNull(zkConnectionStr, "zkConnectionStr");
        this.zNodePath = requireNonNull(zNodePath, "zNodePath");
        this.endpoint = requireNonNull(endpoint, "endpoint");
        this.sessionTimeout = sessionTimeout;
    }

    /**
     * A ZooKeeper server listener, used for register server into ZooKeeper.
     * @param zkConnectionStr ZooKeeper connection string
     * @param zNodePath       ZooKeeper node path(under which this server will registered)
     * @param sessionTimeout  session timeout
     */
    public ZooKeeperUpdatingListener(String zkConnectionStr, String zNodePath, int sessionTimeout) {
        this.zkConnectionStr = requireNonNull(zkConnectionStr, "zkConnectionStr");
        this.zNodePath = requireNonNull(zNodePath, "zNodePath");
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    public void serverStarted(Server server) {
        if (endpoint == null) {
            assert server.activePort().isPresent();
            endpoint = Endpoint.of(server.defaultHostname(),
                                   server.activePort().get()
                                         .localAddress().getPort());
        }
        connector = new ZooKeeperRegistration(zkConnectionStr, zNodePath, sessionTimeout, endpoint);
    }

    @Override
    public void serverStopping(Server server) {
        if (connector != null) {
            connector.close(true);
        }
    }

    @Nullable
    @VisibleForTesting
    ZooKeeperRegistration getConnector() {
        return connector;
    }

    @Nullable
    @VisibleForTesting
    Endpoint getEndpoint() {
        return endpoint;
    }
}
