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
package org.eurekastreams.server.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.persistence.strategies.DescendantOrganizationStrategy;

/**
 * This class provides the mapper functionality for Organization entities.
 */
@Deprecated
public class OrganizationMapper extends DomainEntityMapper<Organization> implements CompositeEntityMapper
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(OrganizationMapper.class);

    /**
     * The descendant organization strategy.
     */
    private DescendantOrganizationStrategy descendantOrgStrategy;

    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public OrganizationMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Set the DescendantOrgStrategy.
     *
     * @param inDescendantOrgStrategy
     *            the descendantOrgStrategy to set
     */
    public void setDescendantOrgStrategy(final DescendantOrganizationStrategy inDescendantOrgStrategy)
    {
        descendantOrgStrategy = inDescendantOrgStrategy;
    }

    /**
     * Get the descendant organization strategy.
     *
     * @return the descendantOrgStrategy the descendant org strategy
     */
    protected DescendantOrganizationStrategy getDescendantOrgStrategy()
    {
        if (descendantOrgStrategy == null)
        {
            throw new NullPointerException(
                    "descendantOrgStrategy is null - make sure to set it on JpaOrganizationMapper.");
        }
        return descendantOrgStrategy;
    }

    /**
     * Retrieve the name of the DomainEntity. This is to allow for the super class to identify the table within
     * hibernate.
     *
     * @return The name of the domain entity.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Organization";
    }

    /**
     * Find by name.
     *
     * @param inName
     *            the name of the user to search for
     * @return the Org with the account name.
     */
    @SuppressWarnings("unchecked")
    public Organization findByShortName(final String inName)
    {
        Query q =
                getEntityManager().createQuery("from Organization where shortname = :inName").setParameter("inName",
                        inName.toLowerCase());
        List results = q.getResultList();

        return (results.size() == 0) ? null : (Organization) results.get(0);
    }

    /**
     * Gets list of child organizations for a given organization.
     *
     * @param parentOrgId
     *            Id of parent org.
     * @return List of child organizations for a given organization.
     */
    public List<Organization> getDescendantOrganizations(final long parentOrgId)
    {
        return descendantOrgStrategy.getDescendantOrganizations(parentOrgId);
    }

    /**
     * Return the root of the organization tree, this is indicated by an org that has itself as it's parent.
     *
     * @return The root of the organization tree.
     */
    @SuppressWarnings("unchecked")
    public Organization getRootOrganization()
    {
        Query q = getEntityManager().createQuery("FROM Organization o where o.parentOrganization.id=o.id");
        List results = q.getResultList();

        return (results.size() == 0) ? null : (Organization) results.get(0);
    }

    /**
     * Delete the organization with the input id.
     *
     * @param orgId
     *            the organization id
     * @throws Exception
     *             on error
     */
    public void delete(final long orgId) throws Exception
    {
        // TODO: implement this - or remove it from the interface if this is not
        // a supported feature.

        // NOTE: to preserve the descendantOrganizationCount, this should call:
        // updateChildOrgCounts([parentId]);
    }

    /**
     * Update all of the denormalized statistics for a set of organizations.
     *
     * @param orgTraverser
     *            the {@link OrganizationHierarchyTraverser} to use to get the organizations to traverse. This ensures
     *            that all parent organizations are handled appropriately and efficiently.
     */
    public void updateOrganizationStatistics(final OrganizationHierarchyTraverser orgTraverser)
    {
        long start = 0;
        if (log.isInfoEnabled())
        {
            start = System.currentTimeMillis();
            log.info("Updating the organization's statistics for a total of "
                    + orgTraverser.getOrganizations().size() + " organizations: "
                    + Arrays.toString(orgTraverser.getOrganizations().toArray()));
        }

        // the descendant org strategy uses a cached list to prevent unnecessary
        // queries
        HashMap<String, String> descOrgStringCache = new HashMap<String, String>();

        for (Organization org : orgTraverser.getOrganizations())
        {
            log.info("Inspecting org: " + org.toString());

            // update recursive counts
            String descendantOrgIdString =
                    descendantOrgStrategy.getDescendantOrganizationIdsForJpql(org.getId(), descOrgStringCache);
            updateDescendantEmployeeCount(org, descendantOrgIdString);
            updateDescendantGroupCount(org, descendantOrgIdString);

            // update non-recursive counts
            updateChildOrganizationCount(org);
        }

        getEntityManager().flush();

        if (log.isInfoEnabled())
        {
            log.info("Completed updating the organization's statistics for orgs in "
                    + (System.currentTimeMillis() - start) + " milliseconds");
        }
    }

    /**
     * Update an organization's child (non-recursive) organization count.
     *
     * @param inOrg
     *            The org to update.
     */
    public void updateChildOrganizationCount(final Organization inOrg)
    {
        String queryString =
                "SELECT count(*) FROM Organization WHERE "
                        + "parentOrganization.id = :orgId AND id != parentOrganization.id";
        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("orgId", inOrg.getId());
        inOrg.setChildOrganizationCount(((Long) query.getSingleResult()).intValue());
    }

    /**
     * Update an organization's descendant employee count.
     *
     * @param organization
     *            the organization to update
     * @param descendantOrgIdString
     *            the "1,2,3,4" string of organization ids including the input org and its descendants.
     */
    private void updateDescendantEmployeeCount(final Organization organization, final String descendantOrgIdString)
    {
        Long descendantEmpCount =
                (Long) getEntityManager().createQuery(
                        "select count(distinct id) from Person where parentOrganization.id IN ("
                                + descendantOrgIdString + ")").getSingleResult();
        int descendantEmployeeCount = descendantEmpCount.intValue();

        if (log.isInfoEnabled())
        {
            log.info("Org id #" + organization.getId() + " has " + descendantEmployeeCount + " descendant employees");
        }

        organization.setDescendantEmployeeCount(descendantEmployeeCount);
    }

    /**
     * Update an organization's descendant domain group count.
     *
     * @param organization
     *            the organization to update
     * @param descendantOrgIdString
     *            the "1,2,3,4" string of organization ids including the input org and its descendants.
     */
    private void updateDescendantGroupCount(final Organization organization, final String descendantOrgIdString)
    {
        Long descendantGroupCount =
                (Long) getEntityManager().createQuery(
                        "select count(distinct id) from DomainGroup where parentOrganization.id IN ("
                                + descendantOrgIdString + ") and isPending='false'").getSingleResult();
        int descendantDomainGroupCount = descendantGroupCount.intValue();

        if (log.isInfoEnabled())
        {
            log.info("Org id #" + organization.getId() + " has " + descendantDomainGroupCount
                    + " descendant domain groups");
        }

        organization.setDescendantGroupCount(descendantDomainGroupCount);
    }
}
