package com.ctrlshift.server;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import com.google.common.annotations.VisibleForTesting;

import com.ctrlshift.client.Endpoint;
import com.ctrlshift.commons.NodeValueCodec;
import com.ctrlshift.commons.ZooKeeperConnector;
import com.ctrlshift.commons.ZooKeeperListener;

/**
 * A Server connection maintains the underlying connection and hearing notice from a ZooKeeper cluster.
 */
class ZooKeeperRegistration {
    private final ZooKeeperConnector zooKeeperConnector;

    /**
     * Create a server register.
     * @param zkConnectionStr ZooKeeper connection string
     * @param zNodePath       ZooKeeper node path(under which this server will registered)
     * @param sessionTimeout  session timeout
     * @param endpoint        register information
     * @param nodeValueCodec  nodeValueCodec used
     */
    ZooKeeperRegistration(String zkConnectionStr, String zNodePath, int sessionTimeout, Endpoint endpoint,
                          NodeValueCodec nodeValueCodec) {
        requireNonNull(nodeValueCodec);
        requireNonNull(endpoint);
        zooKeeperConnector = new ZooKeeperConnector(zkConnectionStr, zNodePath, sessionTimeout,
                                                    new ZooKeeperListener() {
                                                        @Override
                                                        public void nodeChildChange(
                                                                Map<String, String> newChildrenValue) {
                                                        }

                                                        @Override
                                                        public void nodeValueChange(String newValue) {
                                                        }

                                                        @Override
                                                        public void connected() {
                                                            zooKeeperConnector.createChild(
                                                                    endpoint.host() + '_' + endpoint.port(),
                                                                    nodeValueCodec.encode(endpoint));
                                                        }
                                                    });
        zooKeeperConnector.connect();
    }

    /**
     * Create a server register.
     * @param zkConnectionStr ZooKeeper connection string
     * @param zNodePath       ZooKeeper node path(under which this server will registered)
     * @param sessionTimeout  session timeout
     * @param endpoint        register information
     */
    ZooKeeperRegistration(String zkConnectionStr, String zNodePath, int sessionTimeout, Endpoint endpoint) {
        this(zkConnectionStr, zNodePath, sessionTimeout, endpoint, NodeValueCodec.DEFAULT);
    }

    public void close(boolean active) {
        zooKeeperConnector.close(active);
    }

    @VisibleForTesting
    void enableStateRecording() {
        zooKeeperConnector.enableStateRecording();
    }

    @VisibleForTesting
    ZooKeeper underlyingClient() {
        return zooKeeperConnector.underlyingClient();
    }

    @VisibleForTesting
    BlockingQueue<KeeperState> stateQueue() {
        return zooKeeperConnector.stateQueue();
    }
}