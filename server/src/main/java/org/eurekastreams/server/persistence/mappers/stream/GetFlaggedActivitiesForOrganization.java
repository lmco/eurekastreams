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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.GetFlaggedActivitiesByOrgRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper that gets the flagged activities pertaining to an org.
 */
public class GetFlaggedActivitiesForOrganization extends
        BaseArgDomainMapper<GetFlaggedActivitiesByOrgRequest, PagedSet<ActivityDTO>>
{
    /**
     * Log.
     */
    private Log log = LogFactory.make();

    /**
     * Bulk activities mapper.
     */
    private BulkActivitiesMapper activitiesMapper;

    /**
     * Constructor.
     *
     * @param inActivitiesMapper
     *            the activities mapper
     */
    public GetFlaggedActivitiesForOrganization(final BulkActivitiesMapper inActivitiesMapper)
    {
        activitiesMapper = inActivitiesMapper;
    }

    /**
     * Get all flagged activities posted directly under an organization.
     *
     * @param inRequest
     *            the request containing the organization and requesting user
     * @return a paged set of ActivityDTOs
     */
    @Override
    @SuppressWarnings("unchecked")
    public PagedSet<ActivityDTO> execute(final GetFlaggedActivitiesByOrgRequest inRequest)
    {
        if (inRequest.getRequestingUserAccountId() == null)
        {
            log.error("Missing requesting user account.");
            throw new RuntimeException("Missing requesting user account.");
        }

        // get the total number of flagged activities
        Long flaggedActivityCount = (Long) buildQuery("count(*)", inRequest.getOrganizationId()).getSingleResult();

        List<ActivityDTO> activities;
        if (flaggedActivityCount > 0)
        {
            log.info("Found " + flaggedActivityCount
                    + " flagged activity ids - passing them to the bulk activity mapper for ActivityDTOs.");

            Query q = buildQuery("id", inRequest.getOrganizationId());
            q.setFirstResult(inRequest.getStartIndex());
            q.setMaxResults(inRequest.getEndIndex() - inRequest.getStartIndex() + 1);
            List<Long> activityIds = q.getResultList();

            activities = activitiesMapper.execute(activityIds, inRequest.getRequestingUserAccountId());
        }
        else
        {
            log.info("Found no flagged activity ids.");
            activities = new ArrayList<ActivityDTO>();
        }
        return new PagedSet<ActivityDTO>(inRequest.getStartIndex(), inRequest.getEndIndex(), flaggedActivityCount
                .intValue(), activities);
    }

    /**
     * Build a query for selecting field(s) from flagged activities.
     *
     * @param fieldsString
     *            a comma-separated string listing the fields to select
     * @param organizationId
     *            the organization id to get activities for
     * @return a Query object
     */
    private Query buildQuery(final String fieldsString, final Long organizationId)
    {
        return getEntityManager().createQuery(
                "SELECT " + fieldsString
                        + " FROM Activity WHERE recipientParentOrgId = :recipientParentOrgId AND flagged = :isFlagged")
                .setParameter("recipientParentOrgId", organizationId).setParameter("isFlagged", true);
    }
}
