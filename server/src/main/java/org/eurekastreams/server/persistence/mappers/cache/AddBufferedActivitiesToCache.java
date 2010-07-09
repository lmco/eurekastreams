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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Add buffered activities from the refresh feed actions to cache. This is useful so that we don't overload cache with
 * requests, we can buffer, unique, and bulk them here.
 *
 */
public class AddBufferedActivitiesToCache extends CachedDomainMapper
{
    /**
     * Instance of the logger.
     */
    private final Log perfLog = LogFactory.getLog("activityPerformance");

    /**
     * The bulk activities mapper.
     */
    private BulkActivitiesMapper bulkActivitiesMapper;
    /**
     * Cache.
     */
    private MemcachedCache cache;
    /**
     * Gets the associated stream views given an activity.
     */
    private GetCompositeStreamIdsByAssociatedActivity getCompositeStreamsByActivity;

    /**
     * Local instance of the {@link GetCoreStreamViewIdCacheMapper} to retrieve the Everyone StreamView Id.
     */
    private final GetCoreStreamViewIdCacheMapper getCoreStreamViewIdCacheMapper;

    /**
     * Default constructor.
     *
     * @param inBulkActivitiesMapper
     *            The bulk activities mapper.
     * @param inCache
     *            Cache.
     * @param inGetCompositeStreamsByActivity
     *            Gets the associated stream views given an activity.
     * @param inGetCoreStreamViewIdCacheMapper - mapper to retrieve the Everyone Stream View Id from cache.
     */
    public AddBufferedActivitiesToCache(final BulkActivitiesMapper inBulkActivitiesMapper,
            final MemcachedCache inCache,
            final GetCompositeStreamIdsByAssociatedActivity inGetCompositeStreamsByActivity,
            final GetCoreStreamViewIdCacheMapper inGetCoreStreamViewIdCacheMapper)
    {
        bulkActivitiesMapper = inBulkActivitiesMapper;
        cache = inCache;
        getCompositeStreamsByActivity = inGetCompositeStreamsByActivity;
        getCoreStreamViewIdCacheMapper = inGetCoreStreamViewIdCacheMapper;
    }

    /**
     * Execute.
     * @return true.
     */
    public Boolean execute()
    {
        StopWatch swEntireOp = new StopWatch();
        swEntireOp.start();

        StopWatch sw = new StopWatch();

        sw.start();
        List<Long> activityIds = cache.setListCAS(CacheKeys.BUFFERED_ACTIVITIES, null);
        long msToGetList = sw.getTime();

        sw.reset();
        List<ActivityDTO> activites = bulkActivitiesMapper.execute(activityIds, null);
        long msToGetActivityDTOs = sw.getTime();

        HashMap<Long, ArrayList<Long>> activitesByCompositeStream = new HashMap<Long, ArrayList<Long>>();
        HashMap<Long, ArrayList<Long>> activitesByFollower = new HashMap<Long, ArrayList<Long>>();

        //Get the StreamView id for the Everyone list to add each activity to it.
        Long everyoneListId = getCoreStreamViewIdCacheMapper.execute(Type.EVERYONE);

        for (ActivityDTO activity : activites)
        {
            //Add the everyone list to the list of composite streams and then queue up
            //every activity to be added to the everyone list.
            if (!activitesByCompositeStream.containsKey(everyoneListId))
            {
                activitesByCompositeStream.put(everyoneListId, new ArrayList<Long>());
            }
            activitesByCompositeStream.get(everyoneListId).add(activity.getId());

            List<StreamView> streamViews = getCompositeStreamsByActivity.getCompositeStreams(activity);
            List<Long> followers = getCompositeStreamsByActivity.getFollowers(activity);

            for (StreamView streamView : streamViews)
            {
                if (!activitesByCompositeStream.containsKey(streamView.getId()))
                {
                    activitesByCompositeStream.put(streamView.getId(), new ArrayList<Long>());
                }
                activitesByCompositeStream.get(streamView.getId()).add(activity.getId());
            }

            for (Long followerId : followers)
            {
                if (!activitesByFollower.containsKey(followerId))
                {
                    activitesByFollower.put(followerId, new ArrayList<Long>());
                }
                activitesByFollower.get(followerId).add(activity.getId());
            }
        }

        long msToSetCompositeActivities = 0;
        sw.reset();
        for (Long compositeId : activitesByCompositeStream.keySet())
        {
            ArrayList<Long> ids = activitesByCompositeStream.get(compositeId);
            cache.addToTopOfList(
             CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeId,
             ids);
        }
        msToSetCompositeActivities = sw.getTime();

        long msToSetFollowerActivities = 0;
        sw.reset();
        for (Long compositeId : activitesByFollower.keySet())
        {
            ArrayList<Long> ids = activitesByFollower.get(compositeId);
             cache.addToTopOfList(
             CacheKeys.ACTIVITIES_BY_FOLLOWING + compositeId,
             ids);
        }
        msToSetFollowerActivities = sw.getTime();

        if (perfLog.isInfoEnabled())
        {
            List<Long> toLogActivityIds = new ArrayList<Long>();
            if (activityIds != null)
            {
                toLogActivityIds = activityIds;
            }

            perfLog.info("AddBufferedActivitiesToCache action - Added feed items to denormalized activity streams\n"
                    + "[" + swEntireOp.getTime() + "] milliseconds to complete entire operation.\n"
                    + "[" + toLogActivityIds.size() + "] activity Ids.\n"
                    + "[" + msToGetList + "] milliseconds to pull from cache.\n"
                    + "[" + msToGetActivityDTOs + "] milliseconds to get from database.\n"
                    + "[" + msToSetCompositeActivities + "] milliseconds to set ["
                        + activitesByCompositeStream.size() + "] composite streams to add activities to.\n"
                    + "[" + msToSetFollowerActivities + "] milliseconds to set ["
                        + activitesByFollower.size() + "] follower streams to add activities to.\n"
                    + "Ids are [" + StringUtils.join(toLogActivityIds.toArray(), ',') + "].");

            StringBuilder compositeStreamIds = new StringBuilder();
            for (Long streamViewId : activitesByCompositeStream.keySet())
            {
                compositeStreamIds.append(streamViewId + ", ");
            }
            perfLog.info("Added buffered activities to the following composite streams: "
                    + compositeStreamIds.toString());
        }



        return true;
    }
}
