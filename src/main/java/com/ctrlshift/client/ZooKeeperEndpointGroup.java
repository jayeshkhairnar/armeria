/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ctrlshift.client;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import com.google.common.annotations.VisibleForTesting;

import com.ctrlshift.client.Endpoint;
import com.ctrlshift.client.endpoint.DynamicEndpointGroup;
import com.ctrlshift.client.endpoint.EndpointGroup;
import com.ctrlshift.commons.NodeValueCodec;
import com.ctrlshift.commons.ZooKeeperConnector;
import com.ctrlshift.commons.ZooKeeperListener;

/**
 * A ZooKeeper-based {@link EndpointGroup} implementation. This {@link EndpointGroup} retrieves the list of
 * {@link Endpoint}s from a ZooKeeper using {@link NodeValueCodec} and updates it when the children of the
 * zNode changes.
 */
public class ZooKeeperEndpointGroup extends DynamicEndpointGroup {
    private final NodeValueCodec nodeValueCodec;
    private final ZooKeeperConnector zooKeeperConnector;

    /**
     * Create a ZooKeeper-based {@link EndpointGroup}, endpoints will be retrieved from a node's all children's
     * node value using {@link NodeValueCodec}.
     * @param zkConnectionStr a connection string containing a comma separated list of {@code host:port} pairs,
     *                        each corresponding to a ZooKeeper server
     * @param zNodePath       a zNode path e.g. {@code "/groups/productionGroups"}
     * @param sessionTimeout  Zookeeper session timeout in milliseconds
     */
    public ZooKeeperEndpointGroup(String zkConnectionStr, String zNodePath, int sessionTimeout) {
        this(zkConnectionStr, zNodePath, sessionTimeout, NodeValueCodec.DEFAULT);
    }

    /**
     * Create a ZooKeeper-based {@link EndpointGroup}, endpoints will be retrieved from a node's all children's
     * node value using {@link NodeValueCodec}.
     * @param zkConnectionStr a connection string containing a comma separated list of {@code host:port} pairs,
     *                        each corresponding to a ZooKeeper server
     * @param zNodePath       a zNode path e.g. {@code "/groups/productionGroups"}
     * @param sessionTimeout  Zookeeper session timeout in milliseconds
     * @param nodeValueCodec  the nodeValueCodec
     */
    public ZooKeeperEndpointGroup(String zkConnectionStr, String zNodePath, int sessionTimeout,
                                  NodeValueCodec nodeValueCodec) {
        zooKeeperConnector = new ZooKeeperConnector(zkConnectionStr, zNodePath, sessionTimeout,
                                                    createListener());
        this.nodeValueCodec = requireNonNull(nodeValueCodec, "nodeValueCodec");
        zooKeeperConnector.connect();
    }

    @Override
    public void close() {
        zooKeeperConnector.close(true);
    }

    /**
     * Create a {@link ZooKeeperListener} listens specific ZooKeeper events.
     * @return  {@link ZooKeeperListener}
     */
    private ZooKeeperListener createListener() {
        return new ZooKeeperListener() {
            @Override
            public void nodeChildChange(Map<String, String> newChildrenValue) {
                final List<Endpoint> newData = newChildrenValue.values().stream()
                                                               .map(nodeValueCodec::decode)
                                                               .filter(Objects::nonNull)
                                                               .collect(toImmutableList());
                final List<Endpoint> prevData = endpoints();
                if (!prevData.equals(newData)) {
                    setEndpoints(newData);
                }
            }

            @Override
            public void nodeValueChange(String newValue) {
                //ignore value change event
            }

            @Override
            public void connected() {
            }
        };
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