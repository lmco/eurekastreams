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
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;

/**
 * This action initialized/warms the cache by running a series of cache loaders. This action is meant to be run
 * asynchronously at application startup.
 */
public class InitializeCacheExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * The logger.
     */
    private Log log = LogFactory.make();

    /**
     * The cache.
     */
    private Cache cache;

    /**
     * Action key.
     */
    private List<String> actionKeys = null;

    /**
     * Constructor. Set cache to null to skip clearing cache before warming.
     * 
     * @param inCache
     *            Cache client, leave null to skip clearing cache before warming.
     * @param inActionKeys
     *            Action keys.
     */
    public InitializeCacheExecution(final Cache inCache, final List<String> inActionKeys)
    {
        cache = inCache;
        actionKeys = inActionKeys;
    }

    /**
     * Clear cache (if not null) and queue list of cache warming actions.
     * 
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     * @return null.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        if (cache != null)
        {
            log.info("Clearing Cache");
            cache.clear();
        }
        else
        {
            log.info("Skipping Cache clearing");
        }

        for (String key : actionKeys)
        {
            if (key != null && !key.isEmpty())
            {
                log.info("Queueing up action: " + key);
                inActionContext.getUserActionRequests().add(new UserActionRequest(key, null, null));
            }
        }
        return null;
    }
}
