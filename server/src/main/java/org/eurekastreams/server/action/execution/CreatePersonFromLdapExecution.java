/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.CreatePersonRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;
import org.springframework.util.Assert;

/**
 * Creates person in DB from LDAP lookup info.
 * 
 */
public class CreatePersonFromLdapExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * {@link PersonLookupStrategy}.
     */
    private PersonLookupStrategy ldapPersonMapper;

    /**
     * {@link GetRootOrganizationIdAndShortName}.
     */
    private GetRootOrganizationIdAndShortName rootOrgIdDAO;

    /**
     * Create person strategy.
     */
    private TaskHandlerExecutionStrategy<ActionContext> createPersonStrategy;

    /**
     * Constructor.
     * 
     * @param inLdapPersonMapper
     *            {@link PersonLookupStrategy}.
     * @param inRootOrgIdDAO
     *            {@link GetRootOrganizationIdAndShortName}.
     * @param inCreatePersonStrategy
     *            Create person strategy.
     */
    public CreatePersonFromLdapExecution(final PersonLookupStrategy inLdapPersonMapper,
            final GetRootOrganizationIdAndShortName inRootOrgIdDAO,
            final TaskHandlerExecutionStrategy<ActionContext> inCreatePersonStrategy)
    {
        ldapPersonMapper = inLdapPersonMapper;
        rootOrgIdDAO = inRootOrgIdDAO;
        createPersonStrategy = inCreatePersonStrategy;
        Assert.notNull(ldapPersonMapper);
        Assert.notNull(rootOrgIdDAO);
        Assert.notNull(createPersonStrategy);
    }

    /**
     * Creates person in DB from LDAP lookup info.
     * 
     * @param inActionContext
     *            context.
     * @return Person created.
     */
    @Override
    public Person execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        Person person = null;
        String userId = (String) inActionContext.getActionContext().getParams();
        Assert.notNull(userId);

        List<Person> results = ldapPersonMapper.findPeople(userId, 1);

        // short circut if no results from ldap.
        if (results == null || results.size() == 0)
        {
            return person;
        }

        // get Person and set locked before creation.
        person = results.get(0);
        person.setAccountLocked(true);

        return (Person) createPersonStrategy.execute(new TaskHandlerActionContext<ActionContext>(
                new ServiceActionContext(new CreatePersonRequest(person, rootOrgIdDAO.getRootOrganizationId(), false),
                        null), inActionContext.getUserActionRequests()));
    }

}
