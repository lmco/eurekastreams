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

import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.cache.DeleteCacheKeys;

/**
 * Execution for deleting cache keys.
 * 
 */
public class DeleteCacheKeysExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper for deleting cache keys.
     */
    private DeleteCacheKeys cacheKeyDAO;

    /**
     * Constructor.
     * 
     * @param inCacheKeyDAO
     *            {@link DeleteCacheKeys}.
     */
    public DeleteCacheKeysExecution(final DeleteCacheKeys inCacheKeyDAO)
    {
        cacheKeyDAO = inCacheKeyDAO;
    }

    /**
     * Delete cache keys.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return True if successful.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        cacheKeyDAO.execute((Set<String>) inActionContext.getParams());
        return Boolean.TRUE;
    }
}
