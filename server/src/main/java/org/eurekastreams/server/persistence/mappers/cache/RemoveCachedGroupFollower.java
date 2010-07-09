/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * This class is responsible for updating the cache when a group is removed
 * from the list of following for a user. 
 *
 */
public class RemoveCachedGroupFollower extends CachedDomainMapper
{
    /**
     * Local instance of the logger.
     */
    private final Log logger = LogFactory.getLog(RemoveCachedGroupFollower.class);
    
    /**
     * This method performs the appropriate cache updates.
     * @param followerId - id of the user that is severing the relationship.
     * @param followingId - target group of the relationship, the group to sever a relationship with.
     * @return true on success.
     */
    public Boolean execute(final Long followerId, final Long followingId)
    {
        try
        {
            //Remove the current user that is severing a relationship with the target 
            // from the list of followers for that target group.
            getCache().removeFromList(CacheKeys.FOLLOWERS_BY_GROUP + followingId, followerId);
        
            //Remove the target group the current user is now following from the list of
            //entities that the current user is already following.
            getCache().removeFromList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + followerId, followingId);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred updating the cache for ending a relationship.", ex);
            return false;
        }
        return true;
    }
}
