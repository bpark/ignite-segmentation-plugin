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

import org.apache.ignite.IgniteLogger;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.processors.GridProcessorAdapter;
import org.apache.ignite.internal.processors.segmentation.GridSegmentationProcessor;
import org.apache.ignite.plugin.segmentation.SegmentationResolver;

/**
 * Segmentation processor.
 *
 * TODO: evaluate all segmentation configuration parameters.
 */
public class SegmentationProcessor extends GridProcessorAdapter implements GridSegmentationProcessor {

    private SegmentationResolver[] segmentationResolvers;

    private IgniteLogger igniteLogger;

    /**
     * @param ctx Kernal context.
     */
    public SegmentationProcessor(GridKernalContext ctx) {
        super(ctx);
        segmentationResolvers = ctx.config().getSegmentationResolvers();

        for (SegmentationResolver segmentationResolver : segmentationResolvers) {
            if (segmentationResolver.getClass().isAssignableFrom(ReachableSegmentationResolver.class)) {
                ((ReachableSegmentationResolver)segmentationResolver).init(ctx);
            }
        }

        igniteLogger = ctx.log(this.getClass());
    }

    @Override
    public boolean isValidSegment() {

        boolean valid = true;

        for (SegmentationResolver segmentationResolver : segmentationResolvers) {
            try {
                valid = valid && segmentationResolver.isValidSegment();
            } catch (Exception e) {
                valid = false;
            }
        }

        if (!valid) {
            igniteLogger.info("Network segmentation detected!");
        }

        return valid;
    }
}
