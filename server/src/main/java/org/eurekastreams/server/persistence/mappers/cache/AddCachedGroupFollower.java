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
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedGroupIds;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;

/**
 * This class is responsible for updating the cache of the list of followers and following
 * when a follow relationship is added.
 *
 */
public class AddCachedGroupFollower extends CachedDomainMapper
{
    /**
     * Local instance of the logger.
     */
    private final Log logger = LogFactory.getLog(AddCachedGroupFollower.class);
    
    /**
     * Local instance of the GetFollowedPersonIds mapper.  This is the mapper that retrieves
     * the users that are following the the target user.
     */
    private final GetFollowedGroupIds followedMapper;
    
    /**
     * Local instance of the GetFollowerIds mapper.  This is the mapper that retrieves
     * the users that the current user is a follower of.
     */
    private final GetGroupFollowerIds followerMapper;
    
    /**
     * Constructor for the AddCachedGroupFollower mapper.
     * @param inFollowedMapper - instance of the GetFollowerGroupIds mapper.
     * @param inFollowerMapper - instance of the GetGroupFollowerIds mapper.
     */
    public AddCachedGroupFollower(final GetFollowedGroupIds inFollowedMapper,
            final GetGroupFollowerIds inFollowerMapper)
    {
        followedMapper = inFollowedMapper;
        followerMapper = inFollowerMapper;
    }
    
    /**
     * This method performs the appropriate cache updates.
     * @param followerId - id of the user that is initiating a new relationship.
     * @param followingId - target group of the relationship, the new group being followed.
     * @return true on success.
     */
    public Boolean execute(final Long followerId, final Long followingId)
    {
        try
        {
            //Get the list of users that are following the new target group.
            List<Long> followers = followerMapper.execute(followingId);
            if (!followers.contains(followerId))
            {
                //Add the current user that is initiating a relationship with the target 
                // to the list of followers for that target group.
                getCache().addToTopOfList(CacheKeys.FOLLOWERS_BY_GROUP + followingId, followerId);
            }
            
            //Get the list of users that current user is following.
            List<Long> following = followedMapper.execute(followerId);
            if (!following.contains(followingId))
            {
                //Add the target group the current user is now following to the list of
                //users that the current is already following.
                getCache().addToTopOfList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + followerId, followingId);
            }
        }
        catch (Exception ex)
        {
            logger.error("Error occurred updating the cached for a new following relationship.", ex);
            return false;
        }
        return true;
    }
}
