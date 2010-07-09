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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Cached mapper for getting organization leader person ids.
 * 
 */
public class GetOrganizationLeaderIdsByOrgId extends CachedDomainMapper
{
    /**
     * Returns list of person ids that represent leaders for given organization.
     * 
     * @param inOrganizationId
     *            Id of organization to get leader ids for.
     * @return list of person ids that represent leaders for given organization.
     */
    @SuppressWarnings("unchecked")
    public Set<Long> execute(final Long inOrganizationId)
    {
        String cacheKey = CacheKeys.ORGANIZATION_LEADERS_BY_ORG_ID + inOrganizationId;
        Set<Long> results = (Set<Long>) getCache().get(cacheKey);

        if (results != null)
        {
            return results;
        }

        String queryString = "SELECT p.id FROM Person p, Organization o "
                + "WHERE p MEMBER OF o.leaders AND o.id=:orgId";
        Query leaderQuery = getEntityManager().createQuery(queryString);
        leaderQuery.setParameter("orgId", inOrganizationId);

        results = new HashSet(leaderQuery.getResultList());

        getCache().set(cacheKey, results);

        return results;
    }
}
