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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to return person ids of users that have a given organization as a related org.
 * 
 */
public class GetPeopleIdsWithRelatedOrg extends BaseArgDomainMapper<Long, Set<Long>>
{

    /**
     * Return person ids of users that have a given organization as a related org.
     * 
     * @param inRequest
     *            Org id.
     * @return person ids of users that have a given organization as a related org.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> execute(final Long inRequest)
    {
        String q = "SELECT p.id FROM Person p WHERE "
                + "(FROM Organization WHERE id = :id) MEMBER OF p.relatedOrganizations";
        List<Long> personIdsAffected = getEntityManager().createQuery(q).setParameter("id", inRequest).getResultList();
        return new HashSet<Long>(personIdsAffected);
    }

}
