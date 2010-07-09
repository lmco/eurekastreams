/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * Get a list of coordinator person ids for an Organization, straight from the
 * database.
 */
public class GetOrgCoordinatorIds extends ReadMapper<Long, Set<Long>>
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.getLog(GetOrgCoordinatorIds.class);

    /**
     * Get the Set of organization coordinators from the database.
     * 
     * @param inOrganizationId
     *            the id of the organization to query for coordinators
     * @return a Set of person ids that are coordinators for the organization
     *         with the input id
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<Long> execute(final Long inOrganizationId)
    {
        log.trace("Looking in the database for coordinators for organization #"
                + inOrganizationId);

        String queryString = "SELECT p.id FROM Person p, Organization o "
                + "WHERE p MEMBER OF o.coordinators AND o.id=:orgId";
        Query coordinatorsQuery = getEntityManager().createQuery(queryString);
        coordinatorsQuery.setParameter("orgId", inOrganizationId);

        List<Long> coordinatorIds = coordinatorsQuery.getResultList();

        if (log.isTraceEnabled())
        {
            log.trace("Coordinators for org # " + inOrganizationId + ": "
                    + coordinatorIds.toString());
        }

        return new HashSet<Long>(coordinatorIds);
    }
}
