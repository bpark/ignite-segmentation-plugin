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
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.processors.segmentation.GridSegmentationProcessor;
import org.apache.ignite.plugin.*;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings({"unused", "unchecked"})
public class SegmentationPluginProvider implements PluginProvider<SegmentationPluginConfiguration> {

    /**
     * The Context.
     */
    private GridKernalContext kCtx;

    @Override
    public String name() {
        return "Segmentation Resolver Plugin";
    }

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public String copyright() {
        return "bpark 2018";
    }

    @Override
    public <T extends IgnitePlugin> T plugin() {
        return (T)new IgnitePlugin() {};
    }

    @Override
    public void initExtensions(PluginContext ctx, ExtensionRegistry registry) throws IgniteCheckedException {
        kCtx = ((IgniteKernal)ctx.grid()).context();
    }

    @Override
    public <T> T createComponent(PluginContext pluginContext, Class<T> aClass) {
        if (aClass.isAssignableFrom(GridSegmentationProcessor.class)) {
            return (T)new SegmentationProcessor(kCtx);
        } else {
            return null;
        }
    }

    @Override
    public CachePluginProvider createCacheProvider(CachePluginContext ctx) {
        return null;
    }

    @Override
    public void start(PluginContext ctx) throws IgniteCheckedException {

    }

    @Override
    public void stop(boolean cancel) throws IgniteCheckedException {

    }

    @Override
    public void onIgniteStart() throws IgniteCheckedException {

    }

    @Override
    public void onIgniteStop(boolean cancel) {

    }

    @Nullable
    @Override
    public Serializable provideDiscoveryData(UUID nodeId) {
        return null;
    }

    @Override
    public void receiveDiscoveryData(UUID nodeId, Serializable data) {

    }

    @Override
    public void validateNewNode(ClusterNode node) throws PluginValidationException {

    }

    // Other methods can be no-op
}
