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
package org.eurekastreams.server.action.execution.settings;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.action.request.CreatePersonRequest;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for CreatePersonExecution.
 * 
 */
public class CreatePersonExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Persist resource action.
     */
    private PersistResourceExecution<Person> persistResourceExecution = context.mock(PersistResourceExecution.class);

    /**
     * Factory to create person.
     */
    private CreatePersonActionFactory createPersonActionFactory = context.mock(CreatePersonActionFactory.class);

    /**
     * Person mapper.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Mapper to find org by id.
     */
    private FindByIdMapper<Organization> findByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link CreatePersonRequest}.
     */
    private CreatePersonRequest createRequest = context.mock(CreatePersonRequest.class);

    /**
     * {@link Person}.
     */
    private Person person = context.mock(Person.class);

    /**
     * {@link Organization}.
     */
    private Organization organization = context.mock(Organization.class);

    /**
     * Org id used in test.
     */
    private Long orgId = 1L;

    /**
     * System under test.
     */
    private CreatePersonExecution sut = new CreatePersonExecution(createPersonActionFactory, personMapper, "emailKey",
            findByIdMapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(createRequest));

                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(organization));

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(true));

                allowing(person).getEmail();
                will(returnValue("eamil@email.com"));

                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        sut.execute(taskHandlerActionContext);

        assertEquals(1, list.size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNoEmail()
    {
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(createRequest));

                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(organization));

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(false));
            }
        });

        sut.execute(taskHandlerActionContext);

        assertEquals(0, list.size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNullEmailActionKey()
    {
        CreatePersonExecution tempSut = new CreatePersonExecution(createPersonActionFactory, personMapper, null,
                findByIdMapper);

        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(createRequest));

                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(organization));

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(true));
            }
        });

        tempSut.execute(taskHandlerActionContext);

        assertEquals(0, list.size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testEmptyEmailActionKey()
    {
        CreatePersonExecution tempSut = new CreatePersonExecution(createPersonActionFactory, personMapper, "",
                findByIdMapper);

        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(createRequest));

                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(organization));

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(true));
            }
        });

        tempSut.execute(taskHandlerActionContext);

        assertEquals(0, list.size());

        context.assertIsSatisfied();
    }
}
