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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Add buffered activities from the refresh feed actions to cache. This is useful so that we don't overload cache with
 * requests, we can buffer, unique, and bulk them here.
 */
public class AddBufferedActivitiesToCache extends CachedDomainMapper
{
    /**
     * The bulk activities mapper.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper;
    /**
     * Cache.
     */
    private Cache cache;
    /**
     * Gets the associated stream views given an activity.
     */
    private GetCompositeStreamIdsByAssociatedActivity getCompositeStreamsByActivity;

    /**
     * Default constructor.
     * 
     * @param inBulkActivitiesMapper
     *            The bulk activities mapper.
     * @param inCache
     *            Cache.
     * @param inGetCompositeStreamsByActivity
     *            Gets the associated stream views given an activity.
     */
    public AddBufferedActivitiesToCache(final DomainMapper<List<Long>, List<ActivityDTO>> inBulkActivitiesMapper,
            final Cache inCache, final GetCompositeStreamIdsByAssociatedActivity inGetCompositeStreamsByActivity)
    {
        bulkActivitiesMapper = inBulkActivitiesMapper;
        cache = inCache;
        getCompositeStreamsByActivity = inGetCompositeStreamsByActivity;
    }

    /**
     * Execute.
     * 
     * @return true.
     */
    public Boolean execute()
    {
        List<Long> activityIds = cache.setListCAS(CacheKeys.BUFFERED_ACTIVITIES, null);
        List<ActivityDTO> activites = bulkActivitiesMapper.execute(activityIds);

        HashMap<Long, ArrayList<Long>> activitesByFollower = new HashMap<Long, ArrayList<Long>>();
        List<Long> allActivityIds = new ArrayList<Long>();

        for (ActivityDTO activity : activites)
        {
            // Add the everyone list to the list of composite streams and then queue up
            // every activity to be added to the everyone list.
            allActivityIds.add(activity.getId());

            List<Long> followers = getCompositeStreamsByActivity.getFollowers(activity);

            for (Long followerId : followers)
            {
                if (!activitesByFollower.containsKey(followerId))
                {
                    activitesByFollower.put(followerId, new ArrayList<Long>());
                }
                activitesByFollower.get(followerId).add(activity.getId());
            }
        }

        if (!allActivityIds.isEmpty())
        {
            cache.addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, allActivityIds);
        }

        for (Long compositeId : activitesByFollower.keySet())
        {
            ArrayList<Long> ids = activitesByFollower.get(compositeId);
            cache.addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + compositeId, ids);
        }

        return true;
    }
}
