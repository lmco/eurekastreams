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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link CreateRootOrganizationExecution} class.
 * 
 */
public class CreateRootOrganizationExecutionTest
{
    /**
     * System under test.
     */
    private CreateRootOrganizationExecution sut;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Org mapper.
     */
    private final OrganizationMapper orgMapper = context.mock(OrganizationMapper.class);

    /**
     * Person mapper.
     */
    private final PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Org cache.
     */
    private final GetRootOrganizationIdAndShortName getRootOrgIdMapper = context
            .mock(GetRootOrganizationIdAndShortName.class);

    /**
     * Settings mapper.
     */
    private final FindSystemSettings settingsMapper = context.mock(FindSystemSettings.class);

    /**
     * Mocked isntance of {@link TaskHandlerExecutionStrategy}.
     */
    private final TaskHandlerExecutionStrategy orgPersisterExecutionStratMock = context
            .mock(TaskHandlerExecutionStrategy.class);

    /**
     * Instance of the {@link PrincipalActionContext}.
     */
    private final PrincipalActionContext principalActionContextMock = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of the {@link TaskHandlerActionContext} class.
     */
    private final TaskHandlerActionContext taskHandlerActionContextMock = context.mock(TaskHandlerActionContext.class);

    /**
     * Mocked instance of Organization.
     */
    private final Organization orgMock = context.mock(Organization.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new CreateRootOrganizationExecution(getRootOrgIdMapper, orgPersisterExecutionStratMock, orgMapper,
                personMapper, settingsMapper);
    }

    /**
     * Test performing the action when a root org exists.
     * 
     * @throws Exception
     *             ExecutionException may be thrown.
     */
    @Test(expected = ExecutionException.class)
    public final void executeWithRootOrgTest() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                // Any value greater than 0
                oneOf(getRootOrgIdMapper).getRootOrganizationId();
                will(returnValue(1L));
            }
        });

        sut.execute(taskHandlerActionContextMock);
        context.assertIsSatisfied();
    }

    /**
     * Tests performing the action without a root org in the system.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void executeTest() throws Exception
    {
        final Map<String, Serializable> fields = new HashMap<String, Serializable>();
        fields.put("ldapGroups", new ArrayList<String>());

        final long rootId = 1L;

        final Set<Person> coordinators = context.mock(Set.class);

        final Collection<Person> people = new ArrayList<Person>();

        final Person person1 = context.mock(Person.class, "person1");
        final Person person2 = context.mock(Person.class, "person2");
        final Person person3 = context.mock(Person.class, "person3");

        final SystemSettings settings = context.mock(SystemSettings.class);

        people.add(person1);
        people.add(person2);
        people.add(person3);

        context.checking(new Expectations()
        {
            {
                // No root org exists
                oneOf(getRootOrgIdMapper).getRootOrganizationId();
                will(returnValue(null));

                oneOf(orgPersisterExecutionStratMock).execute(taskHandlerActionContextMock);
                will(returnValue(orgMock));

                oneOf(orgMock).setParentOrganization(orgMock);

                oneOf(orgMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(coordinators).iterator();
                will(returnIterator(people));

                oneOf(person1).setParentOrganization(orgMock);
                oneOf(person2).setParentOrganization(orgMock);
                oneOf(person3).setParentOrganization(orgMock);

                oneOf(orgMapper).flush();
                oneOf(personMapper).flush();

                oneOf(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(fields));

                oneOf(settingsMapper).execute(null);
                will(returnValue(settings));

                oneOf(settings).setMembershipCriteria((List<MembershipCriteria>) fields.get("ldapGroups"));

                oneOf(settingsMapper).flush();
            }
        });

        sut.execute(taskHandlerActionContextMock);
        context.assertIsSatisfied();
    }
}
