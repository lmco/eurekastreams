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

import org.eurekastreams.server.domain.stream.StarredActivity;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Removes entry from {@link StarredActivity} table.
 * 
 */
public class DeleteStarredActivity extends CachedDomainMapper
{
    /**
     * Removes entry from StarredActivity table.
     * 
     * @param inStarredActivity
     *            the {@link StarredActivity} to remove.
     * @return True if successful.
     */
    @SuppressWarnings("unchecked")
    public Boolean execute(final StarredActivity inStarredActivity)
    {
        // update database
        Query q = getEntityManager().createQuery(
                "DELETE FROM StarredActivity where personId=:personId and activityId=:activityId");
        q.setParameter("personId", inStarredActivity.getPersonId());
        q.setParameter("activityId", inStarredActivity.getActivityId());
        q.executeUpdate();

        // remove from cache.
        getCache().removeFromList(CacheKeys.STARRED_BY_PERSON_ID + inStarredActivity.getPersonId(),
                inStarredActivity.getActivityId());        

        return Boolean.TRUE;
    }
}
