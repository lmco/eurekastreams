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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.FindSystemSettings;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;

/**
 * Create the root organization.
 */
public class CreateRootOrganizationExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{

    /**
     * Mapper to get the root organization id.
     */
    private final GetRootOrganizationIdAndShortName getRootOrganizationIdMapper;

    /**
     * Organization persister execution strategy.
     */
    private final TaskHandlerExecutionStrategy<PrincipalActionContext> orgPersisterExecutionStrategy;

    /**
     * Organization mapper.
     */
    private final OrganizationMapper orgMapper;

    /**
     * Person mapper.
     */
    private final PersonMapper personMapper;

    /**
     * System settings mapper.
     */
    private final FindSystemSettings settingMapper;

    /**
     * Constructor.
     *
     * @param inGetRootOrganizationidMapper
     *            - instance of {@link GetRootOrganizationIdAndShortName} mapper.
     * @param inOrgPersisterExecutionStrategy
     *            - instance of {@link TaskHandlerExecutionStrategy} configured for the Org Persister.
     * @param inOrgMapper
     *            - instance of {@link OrganizationMapper}.
     * @param inPersonMapper
     *            - instance of {@link PersonMapper}.
     * @param inSettingsMapper - instance of {@link FindSystemSettings} mapper.
     */
    public CreateRootOrganizationExecution(final GetRootOrganizationIdAndShortName inGetRootOrganizationidMapper,
            final TaskHandlerExecutionStrategy<PrincipalActionContext> inOrgPersisterExecutionStrategy,
            final OrganizationMapper inOrgMapper, final PersonMapper inPersonMapper,
            final FindSystemSettings inSettingsMapper)
    {
        getRootOrganizationIdMapper = inGetRootOrganizationidMapper;
        orgPersisterExecutionStrategy = inOrgPersisterExecutionStrategy;
        orgMapper = inOrgMapper;
        personMapper = inPersonMapper;
        settingMapper = inSettingsMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * This method creates the root organization.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        Organization root = null;

        if (getRootOrganizationIdMapper.getRootOrganizationId() == null)
        {
            root = (Organization) orgPersisterExecutionStrategy.execute(inActionContext);

            root.setParentOrganization(root);

            for (Person coordinator : root.getCoordinators())
            {
                coordinator.setParentOrganization(root);
            }

            orgMapper.flush();
            personMapper.flush();

            // convert the params to a map
            Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getActionContext()
                    .getParams();

            List<MembershipCriteria> criteria = (List<MembershipCriteria>) fields.get("ldapGroups");

            SystemSettings systemSettings = settingMapper.execute(null);

            systemSettings.setMembershipCriteria(criteria);

            settingMapper.flush();
        }
        else
        {
            throw new ExecutionException("System already setup");
        }

        return root;
    }

}
