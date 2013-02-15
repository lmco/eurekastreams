/*
 * Copyright (c) 2011-2013 Lockheed Martin Corporation
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
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Database mapper to get a list of suggested people streams for a person by getting all groups that their followers are
 * members of, sorted by follow count within that group, and ignoring the input user's followers as suggestions.
 */
public class GetSuggestedPeopleForPersonDbMapper extends
        BaseArgDomainMapper<SuggestedStreamsRequest, List<PersonModelView>>
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
    public List<PersonModelView> execute(final SuggestedStreamsRequest inRequest)
    {
        Query query = getEntityManager()
                .createQuery(
                        "SELECT new org.eurekastreams.server.search.modelview.PersonModelView("
                                + "peopleTheyFollow.pk.followingId, "
                                + "person.accountId, person.preferredName, person.lastName, person.displayName, "
                                + "person.displayNameSuffix, "
                                + "COUNT(peopleTheyFollow.pk.followingId), person.dateAdded, person.streamScope.id) "
                                + "FROM Follower peopleIFollow, Follower peopleTheyFollow, Person person "
                                + "WHERE peopleIFollow.pk.followingId = peopleTheyFollow.pk.followerId "
                                + "AND peopleIFollow.pk.followerId = :personId"
                                + " AND peopleTheyFollow.pk.followingId NOT IN "
                                + "(SELECT pk.followingId FROM Follower WHERE followerId = :personId) "
                                + " AND person.streamScope.id NOT IN "
                                + "(SELECT pk.scopeId FROM PersonBlockedSuggestion "
                                + "WHERE personid = :personBlockedId) "
                                + "AND person.id = peopleTheyFollow.pk.followingId "
                                + "AND person.accountLocked = false AND person.accountDeactivated = false "
                                + "GROUP BY peopleTheyFollow.pk.followingId, person.accountId, person.preferredName, "
                                + "person.lastName, person.displayNameSuffix, person.displayName, person.dateAdded, "
                                + "person.streamScope.id " + "ORDER BY COUNT(peopleTheyFollow.pk.followingId) DESC")
                .setParameter("personBlockedId", inRequest.getPersonId())
                .setParameter("personId", inRequest.getPersonId());
        query.setMaxResults(inRequest.getStreamCount());
        return query.getResultList();
    }
}
