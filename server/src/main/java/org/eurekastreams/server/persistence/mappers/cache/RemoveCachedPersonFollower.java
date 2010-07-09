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
 * This class handles updating the cache when a user stops following another user.
 *
 */
public class RemoveCachedPersonFollower extends CachedDomainMapper
{
    /**
     * Local instance of the logger.
     */
    private final Log logger = LogFactory.getLog(RemoveCachedPersonFollower.class);

    /**
     * This method performs the appropriate cache updates.
     * @param followerId - id of the user that is severing the relationship.
     * @param followingId - target user of the relationship, the user no longer being followed.
     * @return true on success.
     */
    public Boolean execute(final Long followerId, final Long followingId)
    {
        try
        {
            //Remove the current user that is severing a relationship with the target 
            // from the list of followers for that target user.
            getCache().removeFromList(CacheKeys.FOLLOWERS_BY_PERSON + followingId, followerId);
            //Remove the target user the current user is no longer following from the list of
            //users that the current is already following.
            getCache().removeFromList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + followerId, followingId);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred updating the cached for ending a following relationship.", ex);
            return false;
        }
        return true;
    }
}
