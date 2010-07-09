/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.stream.RefreshCachedCompositeStreamRequest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;

/**
 * This class is responsible for updating an existing list with all of the activities (within max bounds) from a new
 * user.
 */
public class RefreshCachedCompositeStreamExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper instance for refreshing a cached list.
     */
    private final CompositeStreamActivityIdsMapper idsMapper;

    /**
     * Cache key to use for refreshing.
     */
    private final String listKey;

    /**
     * Instance of cache.
     */
    private final Cache cache;

    /**
     * Constructor for the RefreshCachedCompositeStreamAction class.
     *
     * @param inIdsMapper
     *            - mapper for refreshing the cached list.
     * @param inListKey
     *            - key to access the cache.
     * @param inCache
     *            - instance of the cache.
     */
    public RefreshCachedCompositeStreamExecution(final CompositeStreamActivityIdsMapper inIdsMapper,
            final String inListKey, final Cache inCache)
    {
        idsMapper = inIdsMapper;
        listKey = inListKey;
        cache = inCache;
    }

    /**
     * Refresh the cache for a composite stream.
     *
     * @param inActionContext
     *            the action context
     * @return null
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        RefreshCachedCompositeStreamRequest request = (RefreshCachedCompositeStreamRequest) inActionContext.getParams();

        // Delete the cached list and then repopulate it.
        // Need this logic because Following activities list doesn't use the list
        // id as the key, instead, the owner id is used.
        if (listKey.equals(CacheKeys.ACTIVITIES_BY_FOLLOWING))
        {
            cache.delete(listKey + request.getListOwnerId());
        }
        else
        {
            cache.delete(listKey + request.getListToUpdate());
        }
        idsMapper.execute(request.getListToUpdate(), request.getListOwnerId());
        return null;
    }

}
