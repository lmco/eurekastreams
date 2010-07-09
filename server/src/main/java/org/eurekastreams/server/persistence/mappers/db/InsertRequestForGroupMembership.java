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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.RequestedGroupMembership;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Inserts the request for group membership into the database (given it is not already present).
 */
public class InsertRequestForGroupMembership extends
        BaseArgDomainMapper<RequestForGroupMembershipRequest, Boolean>
{
    /**
     * Adds the membership request to the database if it isn't already there.
     *
     * @param inRequest
     *            The request.
     * @return True if added, False if not (already there).
     */
    @Override
    public Boolean execute(final RequestForGroupMembershipRequest inRequest)
    {
        long groupId = inRequest.getGroupId();
        long personId = inRequest.getPersonId();

        final String queryText =
                "SELECT COUNT(*) FROM GroupMembershipRequests WHERE groupId=:groupId AND personId = :personId";
        Boolean needToAdd =
               ((Long) getEntityManager().createQuery(queryText).setParameter("groupId", groupId).setParameter(
                        "personId", personId).getSingleResult()).equals(0L);
        if (needToAdd)
        {
            getEntityManager().persist(new RequestedGroupMembership(groupId, personId));
            getEntityManager().flush();
        }

        return needToAdd;
    }
}
