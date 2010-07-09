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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.PersonRelatedOrganization;

/**
 * Mapper to get RelatedOrganizations for people/person ids.
 */
public class GetRelatedOrganizationIdsByPersonId extends BaseDomainMapper
{
    /**
     * Get PersonRelatedOrganizations for the input person id.
     * 
     * @param inPersonId
     *            collections of ids of people to fetch PersonRelatedOrganizationsf or
     * @return a List of organization ids that the input person has marked as related
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final Long inPersonId)
    {
        return (List<Long>) getEntityManager().createQuery(
                "SELECT pk.organizationId FROM PersonRelatedOrganization WHERE pk.personId = :personId").setParameter(
                "personId", inPersonId).getResultList();
    }

    /**
     * Get PersonRelatedOrganizations for the input list of person ids. The returned map has the person id as key, and
     * each input person id will be represented in the return map with a non-null List of Longs, even if empty.
     * 
     * @param inPersonIds
     *            collections of ids of people to fetch org ids or
     * @return a Map of [person id], [List of org ids]
     */
    @SuppressWarnings("unchecked")
    public Map<Long, List<Long>> execute(final Collection<Long> inPersonIds)
    {
        List<PersonRelatedOrganization> relatedOrgs = (List<PersonRelatedOrganization>) getEntityManager().createQuery(
                "FROM PersonRelatedOrganization WHERE pk.personId IN (:peopleIds)").setParameter("peopleIds",
                inPersonIds).getResultList();

        Map<Long, List<Long>> results = new HashMap<Long, List<Long>>();

        // create a mapped list for each person.
        for (Long personId : inPersonIds)
        {
            results.put(personId, new ArrayList<Long>());
        }

        // loop across the results
        for (PersonRelatedOrganization ro : relatedOrgs)
        {
            results.get(ro.getPersonId()).add(ro.getOrganizationId());
        }

        return results;
    }
}
