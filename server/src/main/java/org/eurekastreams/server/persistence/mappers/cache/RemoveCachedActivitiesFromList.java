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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetStreamByOwnerId;

/**
 * This class is responsible for removing cached activities from a list.
 *
 */
public class RemoveCachedActivitiesFromList extends CachedDomainMapper
{
    /**
     * Local instance of the CompositeStreamActivityIdsMapper.
     */
    private final CompositeStreamActivityIdsMapper activitiesMapper;

    /**
     * Local instance of the mapper used to retrieve the stream scope id based on the
     * owner passed in.
     */
    private final GetStreamByOwnerId streamByOwnerIdMapper;

    /**
     * CacheKey to use to access the list being updated.
     */
    private final String listKey;

    /**
     * Constructor.
     * @param inActivityIdsMapper - instance of the CompositeStreamActivityIdsMapper for retrieving
     * the ids of the activities to remove.
     * @param inStreamByOwnerIdMapper - instance of the GetStreamByOwnerId mapper to retrieve the
     * StreamFilter that represents the users's person list (wall).
     * @param inListKey - cachekey for updating the list in cache.
     */
    public RemoveCachedActivitiesFromList(final CompositeStreamActivityIdsMapper inActivityIdsMapper,
            final GetStreamByOwnerId inStreamByOwnerIdMapper,
            final String inListKey)
    {
        activitiesMapper = inActivityIdsMapper;
        streamByOwnerIdMapper = inStreamByOwnerIdMapper;
        listKey = inListKey;
    }

    /**
     * Execute method, removes the activities from the list and updates the cache.
     * @param inRequest - RemoveCachedActivitiesFromListRequest object to identify the context
     * for removing activities.
     * @return List of activity ids for the updated list after removal.
     */
    public List<Long> execute(final RemoveCachedActivitiesFromListRequest inRequest)
    {
        //Retrieve the activity ids in that compositestream.
        List<Long> targetActivityIds = activitiesMapper.execute(
                streamByOwnerIdMapper.execute(inRequest.getActivitiesOwnerId()).getId(),
                inRequest.getActivitiesOwnerId());

        List<String> keys = new ArrayList<String>();

        if (listKey.equals(CacheKeys.ACTIVITIES_BY_FOLLOWING))
        {
            keys.add(listKey + inRequest.getListOwnerId());
        }
        else
        {
            keys.add(listKey + inRequest.getListId());
        }

        getCache().removeFromLists(keys, targetActivityIds);

        return null;
    }
}
