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
package org.eurekastreams.server.persistence.mappers;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StarredActivity;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetStarredActivityIds;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Insert entry into StarredActivity table.
 * 
 */
public class InsertStarredActivity extends CachedDomainMapper
{
    /**
     * Mapper to read starred activities.
     */
    private GetStarredActivityIds starredActivitiesMapper;

    /**
     * Constructor.
     * 
     * @param inStarredActivitiesMapper
     *            the starred activity read mapper.
     */
    public InsertStarredActivity(final GetStarredActivityIds inStarredActivitiesMapper)
    {
        starredActivitiesMapper = inStarredActivitiesMapper;
    }

    /**
     * Inserts entry into StarredActivity table.
     * 
     * @param inStarredActivity
     *            the {@link StarredActivity} to insert.
     * @return True if successful.
     */
    public Boolean execute(final StarredActivity inStarredActivity)
    {
        Query q = getEntityManager().createQuery(
                "FROM StarredActivity where personId=:personId and activityId=:activityId").setParameter("personId",
                inStarredActivity.getPersonId()).setParameter("activityId", inStarredActivity.getActivityId());

        if (q.getResultList().size() > 0)
        {
            // already starred
            return Boolean.TRUE;
        }

        this.getEntityManager().persist(inStarredActivity);

        // sets in cache.
        String key = CacheKeys.STARRED_BY_PERSON_ID + inStarredActivity.getPersonId();
        List<Long> starredIds = starredActivitiesMapper.execute(inStarredActivity.getPersonId());
        
        if (starredIds == null)
        {
            starredIds = new LinkedList<Long>();
        }
        starredIds.add(inStarredActivity.getActivityId());
        Collections.sort(starredIds);
        Collections.reverse(starredIds);
        getCache().setList(key, starredIds);

        return Boolean.TRUE;
    }

}
