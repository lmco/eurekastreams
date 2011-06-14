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
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Database mapper to get a list of suggested group streams for a person by getting all groups that their followers are
 * members of, sorted by follow count within that group, and ignoring the input user's groups as suggestions.
 */
public class GetSuggestedGroupsForPersonDbMapper extends
        BaseArgDomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>>
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
    public List<DomainGroupModelView> execute(final SuggestedStreamsRequest inRequest)
    {
        Query query = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.DomainGroupModelView(g.id, "
                        + "g.shortName, g.name, COUNT(theirGroups.pk.followingId)) "
                        + "FROM Follower peopleIFollow, GroupFollower theirGroups, DomainGroup g "
                        + "WHERE peopleIFollow.pk.followingId = theirGroups.pk.followerId "
                        + "AND theirGroups.pk.followingId = g.id "
                        + "AND peopleIFollow.pk.followerId = :personId AND theirGroups.pk.followingId NOT IN "
                        + "(SELECT pk.followingId FROM GroupFollower WHERE followerId = :personId) "
                        + "GROUP BY theirGroups.pk.followingId, g.id, g.shortName, g.name "
                        + "ORDER BY COUNT(theirGroups.pk.followingId) DESC").setParameter("personId",
                inRequest.getPersonId());
        query.setMaxResults(inRequest.getStreamCount());
        return query.getResultList();
    }
}
