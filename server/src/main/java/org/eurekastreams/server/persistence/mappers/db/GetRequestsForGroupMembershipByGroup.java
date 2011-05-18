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

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.GetRequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper that gets the list of people requesting membership to a group.
 */
public class GetRequestsForGroupMembershipByGroup extends
        BaseArgDomainMapper<GetRequestForGroupMembershipRequest, PagedSet<Long>>
{
    /** Log. */
    private Log log = LogFactory.make();

    /**
     * Get all flagged activities.
     * 
     * @param inRequest
     *            the request containing the start and end index
     * @return a paged set of ActivityDTOs
     */
    @Override
    @SuppressWarnings("unchecked")
    public PagedSet<Long> execute(final GetRequestForGroupMembershipRequest inRequest)
    {
        final long groupId = inRequest.getGroupId();
        final int startIndex = inRequest.getStartIndex();
        final int numDesired = inRequest.getEndIndex() - startIndex + 1;

        Query q = buildQuery("pk.personId", groupId);
        q.setFirstResult(startIndex);
        q.setMaxResults(numDesired);
        List<Long> list = q.getResultList();

        // Get/compute the total count: If we didn't get all we asked for, then we know where the end of the list is,
        // and thus can calculate the total count without doing a query. Otherwise we'll have to ask.
        int totalCount;
        if (list.size() < numDesired)
        {
            totalCount = startIndex + list.size();
        }
        else
        {
            totalCount = ((Long) buildQuery("count(*)", groupId).getSingleResult()).intValue();
        }

        int endIndex = startIndex + list.size() - 1;

        if (log.isInfoEnabled())
        {
            log.info("Request for membership requests for group " + groupId + " from " + startIndex + "-"
                    + inRequest.getEndIndex() + " yielded " + list.size() + " results and a total count of "
                    + totalCount + ".");
        }

        return new PagedSet<Long>(startIndex, endIndex, totalCount, list);
    }

    /**
     * Build a query for selecting field(s) from group membership requests.
     * 
     * @param fieldsString
     *            a comma-separated string listing the fields to select
     * @param groupId
     *            the group id for which to get requests.
     * @return a Query object
     */
    private Query buildQuery(final String fieldsString, final Long groupId)
    {
        return getEntityManager().createQuery(
                "SELECT " + fieldsString + " FROM GroupMembershipRequests WHERE groupId = :groupId").setParameter(
                "groupId", groupId);
    }
}
