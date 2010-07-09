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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * This class removes the specified activities from the database and cache, including comments. The caller of this
 * method should ensure that the list of ids passed in is of reasonable size, chunking as necessary.
 */
public class RemoveExpiredActivities extends CachedDomainMapper
{
    /**
     * Execute the sql commands to remove the activities and associated comments.
     * 
     * @param expiredActivityIds
     *            the list of ids of the activities to be removed from the database.
     */
    public void execute(final List<Long> expiredActivityIds)
    {
        // deletes comments for activities from the database
        getEntityManager().createQuery("DELETE FROM Comment c WHERE c.target.id in (:expiredActivityIds)")
                .setParameter("expiredActivityIds", expiredActivityIds).executeUpdate();
        
        // delete comments for activities from cache
        for (long activityId : expiredActivityIds)
        {
            // get comment ids
            List<Long> commentIds = getCache().getList(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activityId);
            if (commentIds != null && commentIds.size() > 0)
            {
                for (long commentId : commentIds)
                {
                    getCache().delete(CacheKeys.COMMENT_BY_ID + commentId);
                }
            }
            getCache().delete(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activityId);
            getCache().delete(CacheKeys.ACTIVITY_BY_ID + activityId);
        }

        // deletes activities from the database
        getEntityManager().createQuery("DELETE FROM Activity WHERE id in (:expiredActivityIds)").setParameter(
                "expiredActivityIds", expiredActivityIds).executeUpdate();
    }
}
