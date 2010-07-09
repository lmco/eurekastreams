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
package org.eurekastreams.server.persistence;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Mapper to handle cache updates when an organization is renamed.
 */
public class UpdateCachedOrganizationName extends CachedDomainMapper
{
    /**
     * Performs the database lookup of parent orgs that need to be renamed and updates the cached items as necessary.
     * 
     * @param organizationId
     *            The organization id that was renamed.
     * @param newOrganizationName
     *            The new organization name.
     */
    public void execute(final long organizationId, final String newOrganizationName)
    {
        // updates for person
        List<Long> personIds = queryForIds("Person", organizationId);
        for (Long id : personIds)
        {
            PersonModelView person = (PersonModelView) getCache().get(CacheKeys.PERSON_BY_ID + id);
            if (person != null)
            {
                person.setParentOrganizationName(newOrganizationName);
                getCache().set(CacheKeys.PERSON_BY_ID + id, person);
            }
        }

        // updates for domainGroup
        List<Long> groupIds = queryForIds("DomainGroup", organizationId);
        for (Long id : groupIds)
        {
            DomainGroupModelView group = (DomainGroupModelView) getCache().get(CacheKeys.GROUP_BY_ID + id);
            if (group != null)
            {
                group.setParentOrganizationName(newOrganizationName);
                getCache().set(CacheKeys.GROUP_BY_ID + id, group);
            }
        }
    }

    /**
     * Performs the database lookup for items that have this org as a parent.
     * 
     * @param entityName
     *            the name of the entity to be queried.
     * @param id
     *            the id to be included in the where clause.
     * @return the list of Long ids found by the query.
     */
    @SuppressWarnings("unchecked")
    private List<Long> queryForIds(final String entityName, final long id)
    {
        // Runs database query for list of ids
        Query q = getEntityManager().createQuery(
                "select x.id from " + entityName + " x where x.parentOrganization.id = :id").setParameter("id", id);

        List<Long> results = q.getResultList();
        return results;
    }
}
