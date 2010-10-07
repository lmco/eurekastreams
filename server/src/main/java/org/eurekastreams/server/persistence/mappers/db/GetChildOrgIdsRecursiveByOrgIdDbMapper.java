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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.hibernate.Query;

/**
 * Return a list of all children org ids recursively.
 * 
 */
public class GetChildOrgIdsRecursiveByOrgIdDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{

    /**
     * Return a list of all children org ids recursively.
     * 
     * @param inOrgId
     *            the ID of the organization to fetch child organizations for
     * @return list of all children org ids recursively.
     */
    public List<Long> execute(final Long inOrgId)
    {
        List<Long> recursiveOrgIds = new ArrayList<Long>();
        recurse(inOrgId, recursiveOrgIds);
        return recursiveOrgIds;
    }

    /**
     * Recursively load inOrgChildIds with the IDs of the child organizations of the org with id inOrgId.
     * 
     * @param inOrgId
     *            the ID of the org to load children for
     * @param inOrgChildIds
     *            the Set to store the IDs of the children organizations in
     */
    @SuppressWarnings("unchecked")
    private void recurse(final Long inOrgId, final List<Long> inOrgChildIds)
    {
        String queryString = "SELECT id FROM Organization WHERE "
                + "parentOrganization.id = :parentOrgId AND id != parentOrganization.id";
        Query query = getHibernateSession().createQuery(queryString);
        query.setParameter("parentOrgId", inOrgId);
        List<Long> results = query.list();

        // loop across the results, storing children in collections of their direct parents
        for (Long childOrgId : results)
        {
            inOrgChildIds.add(childOrgId);
            recurse(childOrgId, inOrgChildIds);
        }
    }

}
