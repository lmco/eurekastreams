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
package org.eurekastreams.server.persistence.mappers;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.LikedActivity;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Removes entry from {@link LikedActivity} table.
 *
 */
public class DeleteLikedActivity extends CachedDomainMapper
{
    /**
     * Removes entry from LikedActivity table.
     *
     * @param inLikedActivity
     *            the {@link LikedActivity} to remove.
     * @return True if successful.
     */
    @SuppressWarnings("unchecked")
    public Boolean execute(final LikedActivity inLikedActivity)
    {
        // update database
        Query q = getEntityManager().createQuery(
                "DELETE FROM LikedActivity where personId=:personId and activityId=:activityId");
        q.setParameter("personId", inLikedActivity.getPersonId());
        q.setParameter("activityId", inLikedActivity.getActivityId());
        q.executeUpdate();

        // remove from cache.
        getCache().removeFromList(CacheKeys.LIKED_BY_PERSON_ID + inLikedActivity.getPersonId(),
                inLikedActivity.getActivityId());

        getCache().removeFromList(CacheKeys.LIKERS_BY_ACTIVITY_ID + inLikedActivity.getActivityId(),
                inLikedActivity.getPersonId());

        return Boolean.TRUE;
    }
}
