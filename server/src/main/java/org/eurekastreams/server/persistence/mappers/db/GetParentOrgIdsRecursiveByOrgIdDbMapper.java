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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Build recursive parent org id list from Db.
 * 
 */
public class GetParentOrgIdsRecursiveByOrgIdDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{
    /**
     * Local instance of logger.
     */
    private final Log logger = LogFactory.make();

    /**
     * Build recursive parent org id list from Db.
     * 
     * @param inOrgId
     *            the ID of the organization to fetch parent organizations for.
     * @return recursive parent org id list from Db.
     * 
     *         Note: If the Org id passed in is the root org, an empty list of organizations will be passed back.
     */
    public List<Long> execute(final Long inOrgId)
    {

        if (logger.isTraceEnabled())
        {
            logger.trace("Retrieve hierarchy for org id: " + inOrgId);
        }

        List<Long> parentOrgIds = new ArrayList<Long>();
        // If the orgId passed in is the same as its parent org id, we have
        // the root org and there is no reason to recurse through the tree.
        Long parentOrgId = getParentOrgId(inOrgId);
        if (!parentOrgId.equals(inOrgId))
        {
            recurse(inOrgId, parentOrgIds);
        }
        // add the list to cache
        return parentOrgIds;
    }

    /**
     * Recursively load inParentOrgIds with the IDs of the parent organizations of the org with id inOrgId.
     * 
     * @param inOrgId
     *            the ID of the org to load parent for
     * @param inParentOrgIds
     *            the Set to store the IDs of the parent organizations in
     */
    private void recurse(final Long inOrgId, final List<Long> inParentOrgIds)
    {
        Long parentOrgId = getParentOrgId(inOrgId);

        if (!parentOrgId.equals(inOrgId))
        {
            recurse(parentOrgId, inParentOrgIds);
        }

        if (!parentOrgId.equals(inOrgId))
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Adding " + parentOrgId + " to the list");
            }
            inParentOrgIds.add(parentOrgId);
        }
    }

    /**
     * Helper method to retrieve the parent org id.
     * 
     * @param inOrgId
     *            - id of the org to find the parent org id for.
     * @return parent org id of the passed in org id.
     */
    private Long getParentOrgId(final Long inOrgId)
    {
        String queryString = "SELECT parentOrganization.id FROM Organization WHERE id = :orgId";
        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("orgId", inOrgId);
        return (Long) query.getSingleResult();
    }

}
