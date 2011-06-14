/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SuggestedStreamsRequest;

/**
 * Database mapper to get a list of suggested people streams for a person by getting all groups that their followers are
 * members of, sorted by follow count within that group, and ignoring the input user's followers as suggestions.
 */
public class GetSuggestedPeopleForPersonDbMapper extends BaseArgDomainMapper<SuggestedStreamsRequest, List<Long>>
{
    /**
     * Get a list of suggested group streams for a person by getting all groups that their followers are members of,
     * sorted by follow count within that group.
     * 
     * @param inRequest
     *            the request containing the person and stream count
     * @return the top NN suggested groups by their followers
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> execute(final SuggestedStreamsRequest inRequest)
    {
        Query query = getEntityManager().createQuery(
                "SELECT peopleTheyFollow.pk.followingId FROM Follower peopleIFollow, Follower peopleTheyFollow "
                        + "WHERE peopleIFollow.pk.followingId = peopleTheyFollow.pk.followerId "
                        + "AND peopleIFollow.pk.followerId = :personId AND peopleTheyFollow.pk.followingId NOT IN "
                        + "(SELECT pk.followingId FROM Follower WHERE followerId = :personId) "
                        + "GROUP BY peopleTheyFollow.pk.followingId "
                        + "ORDER BY COUNT(peopleTheyFollow.pk.followingId) DESC").setParameter("personId",
                inRequest.getPersonId());
        query.setMaxResults(inRequest.getStreamCount());
        return query.getResultList();
    }
}
