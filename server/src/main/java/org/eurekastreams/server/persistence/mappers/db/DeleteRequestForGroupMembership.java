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
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Deletes the request for group membership from the database (given it is present).
 */
public class DeleteRequestForGroupMembership extends
        BaseArgDomainMapper<RequestForGroupMembershipRequest, Boolean>
{
    /**
     * Removes the membership request from the database (if it is there).
     *
     * @param inRequest
     *            The request.
     * @return True if removed, False if not (already gone).
     */
    @Override
    public Boolean execute(final RequestForGroupMembershipRequest inRequest)
    {
        long groupId = inRequest.getGroupId();
        long personId = inRequest.getPersonId();

        final String queryText = "DELETE FROM GroupMembershipRequests WHERE groupId=:groupId AND personId = :personId";
        int count =
                getEntityManager().createQuery(queryText).setParameter("groupId", groupId).setParameter("personId",
                        personId).executeUpdate();
        return count > 0;
    }
}
