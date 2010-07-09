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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.profile.DomainGroupCacheUpdaterRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.OrganizationMapper;

/**
 * Abstract parent class for creating/updating groups.
 */
public abstract class GroupPersister implements ResourcePersistenceStrategy<DomainGroup>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(GroupPersister.class);

    /**
     * the group mapper.
     */
    private final DomainGroupMapper groupMapper;

    /**
     * the org mapper.
     */
    private final OrganizationMapper orgMapper;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            The group mapper.
     * @param inOrganizationMapper
     *            The Org being added too.
     */
    public GroupPersister(final DomainGroupMapper inGroupMapper, 
            final OrganizationMapper inOrganizationMapper)
    {
        groupMapper = inGroupMapper;
        orgMapper = inOrganizationMapper;
    }

    /**
     * Queue an async cache updating for the input DomainGroup.
     *
     * @param inActionContext
     *            the app context
     * @param inDomainGroup
     *            the domain group
     * @param inIsUpdate
     *            flag to indicate if the action is to be performed in an update context.
     * @throws Exception
     *             on error
     */
    protected void queueAsyncAction(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final DomainGroup inDomainGroup, final boolean inIsUpdate) throws Exception
    {
        log.info("Queuing up async action to update the cache for domain group #" + inDomainGroup.getId());
        DomainGroupCacheUpdaterRequest request = new DomainGroupCacheUpdaterRequest(inDomainGroup.getId(), inIsUpdate);

        inActionContext.getUserActionRequests().add(
                new UserActionRequest("domainGroupCacheUpdaterAsyncAction", null, request));
    }

    /**
     * Persists Group.
     *
     * @param inActionContext
     *            the app context
     * @param inFields
     *            The property map.
     * @param inGroup
     *            The group.
     * @throws Exception
     *             If error occurs.
     */
    public abstract void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final DomainGroup inGroup) throws Exception;

    /**
     * Abstract method.
     *
     * @param inActionContext
     *            the action context
     * @param inFields
     *            The property map.
     * @return an DomainGroup.
     */
    public abstract DomainGroup get(TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            Map<String, Serializable> inFields);

    /**
     * @return the groupMapper
     */
    protected DomainGroupMapper getGroupMapper()
    {
        return groupMapper;
    }

    /**
     * @return the orgMapper
     */
    protected OrganizationMapper getOrgMapper()
    {
        return orgMapper;
    }

}
