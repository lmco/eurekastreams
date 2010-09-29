/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.persistence.mappers.cache.CacheWarmer;

/**
 * TaskHandlerExecutionStrategy that executes a {@link CacheWarmer} and queues up any UserActionRequests the
 * {@link CacheWarmer} has created.
 */
public class CacheWarmerExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * {@link CacheWarmer}.
     */
    private CacheWarmer cacheWarmer;

    /**
     * Constructor.
     * 
     * @param inCacheWarmer
     *            {@link CacheWarmer}.
     */
    public CacheWarmerExecution(final CacheWarmer inCacheWarmer)
    {
        cacheWarmer = inCacheWarmer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        cacheWarmer.execute(inActionContext.getUserActionRequests());
        return null;
    }
}
