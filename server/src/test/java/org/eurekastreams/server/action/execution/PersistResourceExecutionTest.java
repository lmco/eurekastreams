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

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.ResourcePersistenceStrategy;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for PersistResourceExecution.
 */
public class PersistResourceExecutionTest
{
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
     * The mock person mapper to be used by the action.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * The mock updater to be used by the action.
     */
    private UpdaterStrategy updaterMock = context.mock(UpdaterStrategy.class);

    /**
     * The subject under test.
     */
    private PersistResourceExecution<Organization> sut;

    /**
     * Mocked user information in the session.
     */
    private CreatePersonActionFactory factoryMock = context.mock(CreatePersonActionFactory.class);

    /**
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * principal action context.
     */
    private PrincipalActionContext pActionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked user information in the session.
     */
    private ResourcePersistenceStrategy<Organization> persistStrategyMock = context
            .mock(ResourcePersistenceStrategy.class);

    /**
     * Mocked person lookup by group.
     */
    private PersistResourceExecution<Person> personCreatorMock = context.mock(PersistResourceExecution.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new PersistResourceExecution<Organization>(personMapperMock, factoryMock, updaterMock,
                persistStrategyMock);

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(pActionContext));
            }
        });
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("unchecked")
    @Test
    public void performActionWithFullFormWithExistingCoordinators() throws Exception
    {
        final String name = "org name here";
        final long id = 1L;
        String newName = "NEW org name here";
        final Organization newOrg = new Organization(name, name);
        final Person coordinatorMock = context.mock(Person.class);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("membershipcriteria", "somegroup");
        Set<Person> requestedCoordinators = new HashSet<Person>();
        final Person requestedCoordinator = new Person("aperson", "A", "Simple", "Person", "A");
        requestedCoordinators.add(requestedCoordinator);
        formData.put("coordinators", (Serializable) requestedCoordinators);

        context.checking(new Expectations()
        {
            {
                allowing(pActionContext).getParams();
                will(returnValue(formData));

                oneOf(personMapperMock).findByAccountId(with(requestedCoordinator.getAccountId()));
                will(returnValue(coordinatorMock));

                oneOf(persistStrategyMock).get(with(taskHandlerActionContext), with(any(HashMap.class)));
                will(returnValue(newOrg));

                oneOf(persistStrategyMock).persist(with(taskHandlerActionContext), with(any(HashMap.class)),
                        with(any(Organization.class)));
                oneOf(updaterMock).setProperties(with(newOrg), with(any(HashMap.class)));

            }

        });

        try
        {
            sut.execute(taskHandlerActionContext);
            context.assertIsSatisfied();
        }
        catch (Exception e)
        {
            fail(e + ": something bad happened while setting properties");
        }

    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithFullFormWithNonExistingCoordinators() throws Exception
    {
        final String name = "org name here";
        final long id = 1L;
        String newName = "NEW org name here";
        final Organization newOrg = new Organization(name, name);
        final Person coordinatorMock = context.mock(Person.class);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("memberbershipcriteria", "someattrib=something");
        Set<Person> requestedCoordinators = new HashSet<Person>();
        final Person requestedCoordinator = new Person("aperson", "A", "Simple", "Person", "A");
        requestedCoordinators.add(requestedCoordinator);
        formData.put("coordinators", (Serializable) requestedCoordinators);

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(new ArrayList<UserActionRequest>()));

                allowing(pActionContext).getParams();
                will(returnValue(formData));

                oneOf(personMapperMock).findByAccountId(with(requestedCoordinator.getAccountId()));
                will(returnValue(null));

                oneOf(factoryMock).getCreatePersonAction(personMapperMock, updaterMock);
                will(returnValue(personCreatorMock));

                oneOf(persistStrategyMock).get(with(taskHandlerActionContext), with(any(HashMap.class)));
                will(returnValue(newOrg));

                oneOf(personCreatorMock).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(coordinatorMock));

                oneOf(persistStrategyMock).persist(with(taskHandlerActionContext), with(any(HashMap.class)),
                        with(any(Organization.class)));
                oneOf(updaterMock).setProperties(with(any(Object.class)), with(any(HashMap.class)));
            }
        });

        try
        {
            sut.execute(taskHandlerActionContext);
            context.assertIsSatisfied();
        }
        catch (Exception e)
        {
            fail(e + ": something bad happened while setting properties");
        }

    }
}
