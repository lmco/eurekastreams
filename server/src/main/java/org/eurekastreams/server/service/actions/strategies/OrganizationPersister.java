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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.profile.OrganizationCacheUpdaterRequest;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.OrganizationMapper;

//TODO remove this class it really doesn't do anything.

/**
 * Abstract parent class for creating/updating organizations.
 * 
 */
public abstract class OrganizationPersister implements ResourcePersistenceStrategy<Organization>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(OrganizationPersister.class);

    /**
     * the org mapper.
     */
    private OrganizationMapper orgMapper;

    /**
     * Constructor.
     * 
     * @param inOrganizationMapper
     *            The org mapper.
     */
    public OrganizationPersister(final OrganizationMapper inOrganizationMapper)
    {
        orgMapper = inOrganizationMapper;
    }

    /**
     * Persists Organization.
     * 
     * @param inActionContext
     *            the action context
     * @param inFields
     *            The property map.
     * @param inOrganization
     *            The organization.
     * @throws Exception
     *             If error occurs.
     */
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final Organization inOrganization) throws Exception
    {
        // call concrete class to persist the org for updated stats
        persistOrg(inActionContext, inOrganization);
    }

    /**
     * Abstract method.
     * 
     * @param inActionContext
     *            the action context
     * @param inFields
     *            The property map.
     * @return an Organization.
     */
    public abstract Organization get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            Map<String, Serializable> inFields);

    /**
     * Abstract method.
     * 
     * @param inActionContext
     *            the action context
     * @param inOrganization
     *            The organization.
     * @throws Exception
     *             on error
     */
    protected abstract void persistOrg(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Organization inOrganization) throws Exception;

    /**
     * @return the orgMapper
     */
    protected OrganizationMapper getOrgMapper()
    {
        return orgMapper;
    }

    /**
     * Submit an async cache updating for the org with the input id.
     * 
     * @param inActionContext
     *            the action context
     * @param inOrganization
     *            the organization
     * @param clearRecursiveOrgChildernUpTree
     *            Flag to determine if should clear the recursive org children id cache for every org up the tree from
     *            input org.
     * @param inOrigCoordIds
     *            Original coordinator ids.
     * @throws Exception
     *             on error
     */
    protected void queueAsyncAction(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Organization inOrganization, final boolean clearRecursiveOrgChildernUpTree,
            final Set<Long> inOrigCoordIds) throws Exception
    {
        log.info("Queuing up action to update the cache for org #" + inOrganization.getId());

        Set<Long> coordinatorIds = new HashSet<Long>();
        for (Person p : inOrganization.getCoordinators())
        {
            coordinatorIds.add(p.getId());
        }

        inActionContext.getUserActionRequests().add(
                new UserActionRequest("organizationCacheUpdaterAsyncAction", null, new OrganizationCacheUpdaterRequest(
                        inOrganization.getId(), coordinatorIds, clearRecursiveOrgChildernUpTree, inOrigCoordIds)));
    }

    /**
     * Submit an async cache updating for the org with the input id. This creates request using default value for
     * clearRecursiveOrgChildernUpTree flag (false).
     * 
     * @param inActionContext
     *            the action context
     * @param inOrganization
     *            the organization
     * @param inOrigCoordIds
     *            Original coordinator ids.
     * @throws Exception
     *             on error
     */
    protected void queueAsyncAction(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Organization inOrganization, final Set<Long> inOrigCoordIds) throws Exception
    {
        queueAsyncAction(inActionContext, inOrganization, false, inOrigCoordIds);
    }
}
