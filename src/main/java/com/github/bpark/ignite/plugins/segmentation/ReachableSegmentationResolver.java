/*
 * Copyright 2018 Kurt Sparber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bpark.ignite.plugins.segmentation;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.plugin.segmentation.SegmentationResolver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link SegmentationResolver} implementation that uses {@link InetAddress#isReachable(NetworkInterface, int, int)}
 * to test if a node is segmented. The only required parameter is a list of nodes to test for connections, it must
 * be specified by configuration.
 *
 * NOTE: Please consider, that {@link InetAddress#isReachable(NetworkInterface, int, int)} requires sudo permissions
 * to be executed correctly. Please test if the call is working in your environment before you use this resolver,
 * otherwise the test will always fail!
 */
@SuppressWarnings("unused")
public class ReachableSegmentationResolver implements SegmentationResolver {

    /**
     * Consistency modes. Defines how many servers must respond to pass the segmentation detection.
     */
    public enum Consistency {
        /**
         * At least one server must respond.
         */
        ONE,
        /**
         * Majority of the servers must respond.
         */
        QUORUM,
        /**
         * All servers must respond.
         */
        ALL
    }

    /**
     * Default timeout in milliseconds after the reachable test fails, see
     * {@link InetAddress#isReachable(NetworkInterface, int, int)}.
     */
    private static final int DEFAULT_TIMEOUT = 200;
    /**
     * The maximum numbers of hops to try, see {@link InetAddress#isReachable(NetworkInterface, int, int)}.
     */
    private static final int DEFAULT_TTL = 0;
    /**
     * Default network interface used to test the connection.
     */
    private static final String DEFAULT_NETWORK_INTERFACE = "eth0";

    private String networkInterface = DEFAULT_NETWORK_INTERFACE;
    private int timeout = DEFAULT_TIMEOUT;
    private int ttl = DEFAULT_TTL;
    private List<String> topologyHosts;
    private Consistency consistency = Consistency.QUORUM;

    private IgniteLogger logger;

    private Map<String, Boolean> reachableMap = new LinkedHashMap<>();

    @Override
    public boolean isValidSegment() throws IgniteCheckedException {

        for (String topologyHost : topologyHosts) {

            boolean reachable = checkReachability(topologyHost);

            reachableMap.put(topologyHost, reachable);
        }

        warnAllUnreachable();

        return evaluate();

    }

    public void setTopologyHosts(List<String> topologyHosts) {
        this.topologyHosts = topologyHosts;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public void setConsistency(Consistency consistency) {
        this.consistency = consistency;
    }

    void init(GridKernalContext ctx) {
        this.logger = ctx.log(this.getClass());
    }

    private boolean checkReachability(String host) {
        boolean reachable = false;

        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(this.networkInterface);
            if (networkInterface.isUp()) {
                InetAddress inetAddress = InetAddress.getByName(host);
                reachable = inetAddress.isReachable(networkInterface, ttl, timeout);
            }

        } catch (IOException e) {
            reachable = false;
        }

        return reachable;
    }

    private boolean evaluate() {
        switch (consistency) {
            case ONE: return reachableMap.values().stream().anyMatch(i -> i);
            case ALL: return reachableMap.values().stream().allMatch(i -> i);
            default: return reachableMap.values().stream().filter(i -> i).count() > topologyHosts.size() / 2;
        }
    }

    private void warnAllUnreachable() {
        reachableMap.entrySet().stream()
                .filter((e -> !e.getValue()))
                .forEach(e -> logger.error("Segmentation Alert: detected unreachable host: " + e.getKey()));
    }

}
