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

import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;

/**
 * Get a set of organization ids that a person is directly coordinating.
 */
public class GetOrgIdsDirectlyCoordinatedByPerson extends BaseArgCachedDomainMapper<Long, Set<Long>>
{
    /**
     * Get a set of organization ids that a person is directly coordinating.
     * 
     * @param inUserId
     *            the user id to check org coordinator status of
     * @return a set of ids of orgs that a user is explicitly the coordinator of
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> execute(final Long inUserId)
    {
        String cacheKey = CacheKeys.ORG_IDS_DIRECTLY_COORD_BY_PERSON + inUserId;

        Set<Long> results = (Set<Long>) getCache().get(cacheKey);

        if (results == null)
        {

            Query q = getEntityManager().createQuery(
                    "SELECT o.id FROM Person p, Organization o "
                            + "WHERE p MEMBER OF o.coordinators AND p.id = :personId");
            q.setParameter("personId", inUserId);
            results = new HashSet<Long>(q.getResultList());

            getCache().set(cacheKey, results);
        }

        return results;
    }
}
