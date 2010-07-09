/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Organization;

/**
 * Strategy to recursively determine child organizations.
 */
public class DescendantOrganizationStrategy
{
    /**
     * The QueryOptimizer to use for specialized functions.
     */
    private QueryOptimizer queryOptimizer;

    /**
     * EntityManager to use for all ORM operations.
     */
    private EntityManager entityManager;

    /**
     * Set the entity manager to use for all ORM operations.
     *
     * @param inEntityManager
     *            the EntityManager to use for all ORM operations.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        this.entityManager = inEntityManager;
    }

    /**
     * Set the QueryOptimizer.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer
     */
    public void setQueryOptimizer(final QueryOptimizer inQueryOptimizer)
    {
        queryOptimizer = inQueryOptimizer;
    }

    /**
     * Return a comma-separated list of IDs of all child organizations of the input parent organization id, including
     * the id of the passed-in org.
     *
     * @param parentOrgId
     *            the parent organization id
     * @param descendantOrgIdMap
     *            hashmap to use for caching query results - this method will most likely be called in a loop, so
     *            prevent senseless queries for the same descendant counts.
     * @return a comma-separated list of IDs of all child organizations of the input parent organization id
     */
    public String getDescendantOrganizationIdsForJpql(final long parentOrgId,
            final HashMap<String, String> descendantOrgIdMap)
    {
        return getDescendantOrganizationIdsForJpql(Long.toString(parentOrgId), descendantOrgIdMap);
    }

    /**
     * Return a comma-separated list of IDs of the input and all child organizations of the input parent organization
     * ids. Flush is called at the top to make any local changes available to the JPQL.
     *
     * @param orgIds
     *            comma-separated list of the IDs to get children for
     * @param descendantOrgIdMap
     *            hashmap to use for caching query results - this method will most likely be called in a loop, so
     *            prevent senseless queries for the same descendant counts.
     * @return a comma-separated list of IDs of all child organizations of the input parent organization id
     */
    @SuppressWarnings("unchecked")
    public String getDescendantOrganizationIdsForJpql(final String orgIds,
            final HashMap<String, String> descendantOrgIdMap)
    {
        // use the cached hierarchy if we have it
        if (descendantOrgIdMap.containsKey(orgIds))
        {
            return descendantOrgIdMap.get(orgIds);
        }

        entityManager.flush();

        // note: the ordering is for unit testability
        Query q = entityManager.createQuery("SELECT id FROM Organization o where o.parentOrganization.id IN ( "
                + orgIds + ") and o.id<>o.parentOrganization.id order by id");

        // flatten the list of longs into a comma-separated list of child org
        // ids
        String childOrgIds = "";
        for (Long childOrgId : (List<Long>) q.getResultList())
        {
            if (childOrgIds.length() > 0)
            {
                childOrgIds += ",";
            }
            childOrgIds += childOrgId;
        }

        // recurse
        String allOrgIds = orgIds;
        if (childOrgIds.length() > 0)
        {
            String result = getDescendantOrganizationIdsForJpql(childOrgIds, descendantOrgIdMap);
            if (result.length() > 0)
            {
                if (allOrgIds.length() > 0)
                {
                    allOrgIds += ",";
                }
                allOrgIds += result;
            }
        }

        // cache this hierarchy
        descendantOrgIdMap.put(orgIds, allOrgIds);

        return allOrgIds;
    }

    /**
     * Gets list of child organizations for a given organization.
     *
     * @param parentOrgId
     *            Id of parent org.
     * @return List of child organizations for a given organization.
     */
    @SuppressWarnings("unchecked")
    public List<Organization> getDescendantOrganizations(final long parentOrgId)
    {
        Query q = entityManager.createQuery(
                "FROM Organization o where o.parentOrganization.id=:parentOrgId and o.id<>:parentOrgId").setParameter(
                "parentOrgId", parentOrgId);

        List<Organization> childOrgs = q.getResultList();

        List<Organization> allOrgs = new ArrayList<Organization>();

        // recursively add child organizations
        for (Organization org : childOrgs)
        {
            allOrgs.add(org);
            allOrgs.addAll(getDescendantOrganizations(org.getId()));
        }

        return allOrgs;
    }

}
