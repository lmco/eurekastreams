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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetStreamByOwnerId;

/**
 * This class is responsible for adding the activities of the target user to the target list.
 * These activities are pulled from the list of activities that comprise the target user's
 * composite stream.
 *
 */
public class AddCachedActivityToList extends CachedDomainMapper
{
    /**
     * Instance of logger.
     */
    private final Log logger = LogFactory.getLog(AddCachedActivityToList.class);
    
    /**
     * Local instance of the Mapper responsible for loading the activity ids
     * of the composite stream.
     */
    private final CompositeStreamActivityIdsMapper activitiesMapper;
    
    /**
     * Local instance of the Mapper responsible for loading activities by id.
     */
    private final DomainMapper<List<Long>, List<ActivityDTO>>  activitiesByIdMapper;
    
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
     * Constructor for the AddCachedActivityToList Mapper class.
     * @param inActivityIdsMapper - mapper used to retrieve the activity ids for the 
     * supplied list.
     * @param inActivitiesByIdMapper - mapper for retrieving activity ids.
     * @param inStreamByOwnerIdMapper - mapper for retrieving the personal stream (wall) of the owner id.
     * @param inListKey - string cache key for the cached list that needs to be updated.
     */
    public AddCachedActivityToList(final CompositeStreamActivityIdsMapper inActivityIdsMapper, 
            final DomainMapper<List<Long>, List<ActivityDTO>>  inActivitiesByIdMapper,
            final GetStreamByOwnerId inStreamByOwnerIdMapper,
            final String inListKey)
    {
        activitiesMapper = inActivityIdsMapper;
        activitiesByIdMapper = inActivitiesByIdMapper;
        streamByOwnerIdMapper = inStreamByOwnerIdMapper;
        listKey = inListKey;
    }
    
    /**
     * This method retrieves the newest activity of the person passed in
     * and merges it with the existing list.
     * {@inheritDoc}
     */
    public List<Long> execute(final AddCachedActivityToListRequest inRequest)
    {   
        logger.trace("Entering the execute method for adding cached activity to list with listid: " 
                + inRequest.getListId() + " listownerid: " + inRequest.getListOwnerId() 
                + " and activity owner id: " + inRequest.getActivityOwnerId());
        //Retrieve the owner's list of activities.
        List<Long> ownerActivityIds = 
            activitiesMapper.execute(inRequest.getListId(), inRequest.getListOwnerId());
        List<ActivityDTO> ownerActivities = activitiesByIdMapper.execute(ownerActivityIds);
        logger.debug("Retrieved " + ownerActivities.size() + " activities for the list.");
        
        //Retrieve the activity ids in that compositestream.
        List<Long> targetActivityIds = activitiesMapper.execute(
                streamByOwnerIdMapper.execute(inRequest.getActivityOwnerId()).getId(), 
                inRequest.getActivityOwnerId());        
        logger.debug("Retrieved " + targetActivityIds.size() + " activities for the target activity owner.");
        
        //if targetActivityIds list is empty, short circuit here.
        if (targetActivityIds.size() == 0)
        {
            return ownerActivityIds;
        }
        
        //Get the ActivityDTO of the newest item in that List, this will be the one that is added into the ownerlist.
        List<ActivityDTO> targetActivity = activitiesByIdMapper.execute(targetActivityIds.subList(0, 1));
        
        //Find the index to insert the new item at.
        int targetIndex = findIndexToInsertActivity(ownerActivities, targetActivity.get(0));
        
        logger.debug("Found index: " + targetIndex + " for activity id: " + targetActivityIds.get(0));

        //Insert the new item's id at that index.
        ownerActivityIds.add(targetIndex, targetActivity.get(0).getEntityId());
        //Add the list back into cache.  For Following activities list, we need special logic
        //because the list id is not the key to the cache, it is the owner's id.
        if (listKey.equals(CacheKeys.ACTIVITIES_BY_FOLLOWING))
        {
            getCache().setList(listKey + inRequest.getListOwnerId(), ownerActivityIds);
        }
        else
        {
            getCache().setList(listKey + inRequest.getListId(), ownerActivityIds);
        }
        logger.debug("Setting the list back into the cache with the new activity with size: " 
                + ownerActivityIds.size());
        
        return ownerActivityIds;
    }
    
    /**
     * This method performs a binary search to find the right index to insert the new activity
     * based on the date posted.  Assumes that the list is sorted by date descending.
     * @param list - list of ActivityDTO's to search for the best place based on date posted.
     * @param target - ActivityDTO to be compared within the list.
     * @return - index of where to insert the activity.
     */
    private int findIndexToInsertActivity(final List<ActivityDTO> list, final ActivityDTO target)
    {
        if (list.size() == 0)
        {
            return 0;
        }
            
        int lowIndex = 0;
        int highIndex = list.size() - 1;
        int midIndex;
        
        while (lowIndex <= highIndex)
        {
            midIndex = (lowIndex + highIndex) / 2;
            
            if (list.get(midIndex).getPostedTime().compareTo(target.getPostedTime()) < 0)
            {
                highIndex = midIndex - 1;
            }
            else if (list.get(midIndex).getPostedTime().compareTo(target.getPostedTime()) > 0)
            {
                lowIndex = midIndex + 1;
            }
            else
            {
                return midIndex;
            }
        }
        
        //highindex needs to be incremented because the add logic for a list adds before the index
        //supplied.
        return (highIndex >= 0) ? highIndex + 1 : 0;
    }

}
