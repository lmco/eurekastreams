/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.DeleteCacheKeys;

/**
 * Action to delete a featured stream.
 */
public class DeleteFeaturedStreamExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Delete mapper.
     */
    DomainMapper<Long, Void> deleteMapper;

    /**
     * Mapper for deleting cache keys.
     */
    private DeleteCacheKeys deleteCacheKeyDAO;

    /**
     * Constructor.
     * 
     * @param inDeleteMapper
     *            Delete mapper.
     * @param inDeleteCacheKeyDAO
     *            Mapper for deleting cache keys.
     */
    public DeleteFeaturedStreamExecution(final DomainMapper<Long, Void> inDeleteMapper,
            final DeleteCacheKeys inDeleteCacheKeyDAO)
    {
        deleteMapper = inDeleteMapper;
        deleteCacheKeyDAO = inDeleteCacheKeyDAO;
    }

    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        // delete featured stream.
        deleteMapper.execute((Long) inActionContext.getActionContext().getParams());

        // Delete FeaturedStreams from cache both now and async to prevent unlikely, but possible, race condition.
        Set<String> keysToDelete = Collections.singleton(CacheKeys.FEATURED_STREAMS);
        deleteCacheKeyDAO.execute(keysToDelete);
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteCacheKeysAction", null, (Serializable) keysToDelete));

        return true;
    }
}
