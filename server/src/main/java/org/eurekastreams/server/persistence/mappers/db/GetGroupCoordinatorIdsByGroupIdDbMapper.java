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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Return group coordinator ids for for given group id.
 * 
 */
public class GetGroupCoordinatorIdsByGroupIdDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{

    /**
     * Return group coordinator ids for for given group id.
     * 
     * @param inRequest
     *            Group id to find coordinator ids for.
     * @return group coordinator ids for for given group id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inRequest)
    {
        String queryString = "SELECT p.id FROM Person p, DomainGroup g WHERE p member of g.coordinators"
                + " AND g.id = :groupId";
        return getEntityManager().createQuery(queryString).setParameter("groupId", inRequest).getResultList();
    }
}
