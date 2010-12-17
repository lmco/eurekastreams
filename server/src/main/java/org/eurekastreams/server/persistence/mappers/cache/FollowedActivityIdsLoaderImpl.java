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
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * Implementation of {@link FollowedActivityIdsLoader}.
 *
 */
public class FollowedActivityIdsLoaderImpl extends BaseDomainMapper implements FollowedActivityIdsLoader
{
    
    /**
     * {@inheritDoc}
     */
    public List<Long> getFollowedActivityIds(final long inPersonId, final int inMaxResults)
    {
        List<Long> results = new ArrayList<Long>();
        
        results.addAll(getPersonActivityIdsFollowed(inPersonId, inMaxResults));
        
        //trim list if needed.
        if (results.size() > inMaxResults)
        {
            results = results.subList(0, inMaxResults);
        }
        
        return results;        
    }

    /**
     * Returns list of activity ids for people the user is following.
     * @param inPersonId The user's id.
     * @param inMaxResults Max. number of ids to return
     * @return List of activity ids for people the user is following.
     */
    @SuppressWarnings("unchecked")
    private List< ? extends Long> getPersonActivityIdsFollowed(final long inPersonId, final int inMaxResults)
    {
        String queryString = "SELECT a.id FROM "
            + "Activity a, "
            + "Follower f, "
            + "Person followedPerson "
            + "WHERE f.pk.followerId = :userId " 
                        + "AND followedPerson.id = f.pk.followingId " 
                        + "AND a.recipientStreamScope = followedPerson.streamScope ORDER BY a.id DESC";
        Query query = getEntityManager().createQuery(queryString).setParameter("userId", inPersonId);
        query.setMaxResults(inMaxResults);
        
        return query.getResultList();
    }
}
