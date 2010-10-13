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
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowerIds;

/**
 * This mapper updates the cache entries for a new person followed by another person.
 * 
 */
public class AddCachedPersonFollower extends CachedDomainMapper
{
    /**
     * Local instance of the logger.
     */
    private final Log logger = LogFactory.getLog(AddCachedPersonFollower.class);

    /**
     * Local instance of the GetFollowedPersonIds mapper. This is the mapper that retrieves the users that are following
     * the the target user.
     */
    private final DomainMapper<Long, List<Long>> followedMapper;

    /**
     * Local instance of the GetFollowerIds mapper. This is the mapper that retrieves the users that the current user is
     * a follower of.
     */
    private final GetFollowerIds followerMapper;

    /**
     * Constructor for the AddCachedPersonFollower mapper.
     * 
     * @param inFollowedMapper
     *            = mapper to retrieve the list of users that the current user is following.
     * @param inFollowerMapper
     *            - mapper to retrieve the list of users that are following the new target user.
     */
    public AddCachedPersonFollower(final DomainMapper<Long, List<Long>> inFollowedMapper,
            final GetFollowerIds inFollowerMapper)
    {
        followedMapper = inFollowedMapper;
        followerMapper = inFollowerMapper;
    }

    /**
     * This method performs the appropriate cache updates.
     * 
     * @param followerId
     *            - id of the user that is initiating a new relationship.
     * @param followingId
     *            - target user of the relationship, the new user being followed.
     * @return true on success.
     */
    public Boolean execute(final Long followerId, final Long followingId)
    {
        try
        {
            // Get the list of users that are following the new target user.
            List<Long> followers = followerMapper.execute(followingId);
            if (!followers.contains(followerId))
            {
                // Add the current user that is initiating a relationship with the target
                // to the list of followers for that target user.
                getCache().addToTopOfList(CacheKeys.FOLLOWERS_BY_PERSON + followingId, followerId);
            }

            // Get the list of users that current user is following.
            List<Long> following = followedMapper.execute(followerId);
            if (!following.contains(followingId))
            {
                // Add the target user the current user is now following to the list of
                // users that the current is already following.
                getCache().addToTopOfList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + followerId, followingId);
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
