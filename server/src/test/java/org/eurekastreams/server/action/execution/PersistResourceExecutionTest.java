/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.ResourcePersistenceStrategy;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.eurekastreams.server.testing.TestContextCreator;
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
    private final PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * The mock updater to be used by the action.
     */
    private final UpdaterStrategy updaterMock = context.mock(UpdaterStrategy.class);

    /**
     * The subject under test.
     */
    private PersistResourceExecution<DomainGroup> sut;

    /**
     * Mocked user information in the session.
     */
    private final CreatePersonActionFactory factoryMock = context.mock(CreatePersonActionFactory.class);

    /**
     * Mocked user information in the session.
     */
    private final ResourcePersistenceStrategy<DomainGroup> persistStrategyMock = context
            .mock(ResourcePersistenceStrategy.class);

    /**
     * Mocked person lookup by group.
     */
    private final PersistResourceExecution<Person> personCreatorMock = context.mock(PersistResourceExecution.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new PersistResourceExecution<DomainGroup>(personMapperMock, //
                factoryMock, updaterMock, persistStrategyMock);
    }

    /**
     * Build DomainGroup based on the input form being fully filled out with valid data.
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
        final DomainGroup newGroup = new DomainGroup();
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
                oneOf(personMapperMock).findByAccountId(with(requestedCoordinator.getAccountId()));
                will(returnValue(coordinatorMock));

                oneOf(persistStrategyMock).get(with(any(TaskHandlerActionContext.class)), with(any(HashMap.class)));
                will(returnValue(newGroup));

                oneOf(persistStrategyMock).persist(with(any(TaskHandlerActionContext.class)),
                        with(any(HashMap.class)),
                        with(any(DomainGroup.class)));
                oneOf(updaterMock).setProperties(with(newGroup), with(any(HashMap.class)));

            }

        });

        TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(formData, null);
            sut.execute(taskHandlerActionContext);
            context.assertIsSatisfied();
    }

    /**
     * Build a group based on the input form being fully filled out with valid data.
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
        final DomainGroup newGroup = new DomainGroup();
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
                oneOf(personMapperMock).findByAccountId(with(requestedCoordinator.getAccountId()));
                will(returnValue(null));

                oneOf(factoryMock).getCreatePersonAction(personMapperMock, updaterMock);
                will(returnValue(personCreatorMock));

                oneOf(persistStrategyMock).get(with(any(TaskHandlerActionContext.class)), with(any(HashMap.class)));
                will(returnValue(newGroup));

                oneOf(personCreatorMock).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(coordinatorMock));

                oneOf(persistStrategyMock).persist(with(any(TaskHandlerActionContext.class)),
                        with(any(HashMap.class)),
                        with(any(DomainGroup.class)));
                oneOf(updaterMock).setProperties(with(any(Object.class)), with(any(HashMap.class)));
            }
        });

        TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(formData, null);
            sut.execute(taskHandlerActionContext);
            context.assertIsSatisfied();
    }
}
