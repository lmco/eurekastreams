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
package org.eurekastreams.server.persistence.mappers;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.action.authorization.CoordinatorAccessAuthorizer;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Mapper to get a Set of Coordinator IDs of all of the organization coordinators recursively up to root.
 */
public class GetRecursiveOrgCoordinators extends CachedDomainMapper implements CoordinatorAccessAuthorizer<Long, Long>
{

    /**
     * Mapper to get list of all recursive orgs up to the Root.
     */
    private OrganizationHierarchyCache organizationHierarchyCache;

    /**
     * Mapper to get Coordinators for a list of Coordinators.
     */
    private GetOrgCoordinators getOrgCoordinators;

    /**
     * Mapper to get a person's id by their account id.
     */
    private DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * Whether or not to go up the org tree or down. Default to go up.
     */
    private Boolean goUpTree = true;

    /**
     * Default constructor.
     * 
     * @param inOrganizationHierarchyCache
     *            org cache.
     * @param inGetOrgCoordinators
     *            get org coordinators.
     * @param inGoUpTree
     *            go up tree or down tree.
     * @param inGetPersonIdByAccountIdMapper
     *            Mapper to get a person's id by their account id
     * @param inCache
     *            the cache
     */
    public GetRecursiveOrgCoordinators(final OrganizationHierarchyCache inOrganizationHierarchyCache,
            final GetOrgCoordinators inGetOrgCoordinators, final Boolean inGoUpTree,
            final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper, final Cache inCache)
    {
        organizationHierarchyCache = inOrganizationHierarchyCache;
        getOrgCoordinators = inGetOrgCoordinators;
        goUpTree = inGoUpTree;
        getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
        setCache(inCache);
    }

    /**
     * Returns a list of all the Coordinators for a Org up to the parent Org.
     * 
     * @param inOrgId
     *            the ID of the organization to fetch coordinators for
     * @return list of Coordinator IDs of all recursive parent Organizations of the input one.
     */
    public Set<Long> execute(final Long inOrgId)
    {
        Set<Long> pOrgs;
        // Get List of parent orgs for the OrgId.
        if (goUpTree)
        {
            pOrgs = new HashSet<Long>(organizationHierarchyCache.getSelfAndParentOrganizations(inOrgId));
        }
        else
        {
            pOrgs = new HashSet<Long>(organizationHierarchyCache.getSelfAndRecursiveChildOrganizations(inOrgId));
        }

        // send list to get Coordinators mapper.
        Set<Long> recursiveOrgCoordIds = getOrgCoordinators.execute(pOrgs);

        // return list
        return recursiveOrgCoordIds;
    }

    /**
     * Returns a list of all the Coordinators for a Org up to the parent Org.
     * 
     * @param inOrgShortName
     *            org short name
     * @return list of Coordinator IDs of all recursive parent Organizations of the input one.
     */
    public Set<Long> execute(final String inOrgShortName)
    {
        return this.execute(organizationHierarchyCache.getOrganizationIdFromShortName(inOrgShortName));
    }

    /**
     * Determine if a person with the input id is an org coordinator of the org with the input id, or any of its
     * parents.
     * 
     * @param inOrgId
     *            the id of the organization to check access to
     * @param inUserPersonId
     *            the id of the person to check access for
     * @return whether the person is an org coordinator for the org
     */
    public boolean isOrgCoordinatorRecursively(final Long inUserPersonId, final Long inOrgId)
    {
        return execute(inOrgId).contains(inUserPersonId);
    }

    /**
     * Determine if a person with the input id is an org coordinator of the org with the input short Name, or any of its
     * parents.
     * 
     * @param inOrgShortName
     *            the shortName of the organization to check access to.
     * @param inUserPersonId
     *            the id of the person to check access for.
     * @return whether the person is an org coordinator for the org.
     */
    public boolean isOrgCoordinatorRecursively(final Long inUserPersonId, final String inOrgShortName)
    {
        return execute(inOrgShortName).contains(inUserPersonId);
    }

    /**
     * Determine if a person with the input account id is an org coordinator of the org with the input org id, or any of
     * its parents.
     * 
     * @param inUserAccountId
     *            the account name of the person to check access to.
     * @param inOrgId
     *            the id of the org to check against.
     * @return whether the person is an org coordinator for the org.
     */
    public boolean isOrgCoordinatorRecursively(final String inUserAccountId, final Long inOrgId)
    {
        Long personId = getPersonIdByAccountIdMapper.execute(inUserAccountId);
        return isOrgCoordinatorRecursively(personId, inOrgId);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Boolean hasCoordinatorAccessRecursively(final Long inPersonId, final Long inEntityId)
    {
        return isOrgCoordinatorRecursively(inPersonId, inEntityId);
    }
}
