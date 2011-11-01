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
package org.eurekastreams.server.action.execution.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.action.request.CreatePersonRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.eurekastreams.server.testing.TestContextCreator;
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
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Persist resource action.
     */
    private final PersistResourceExecution<Person> persistResourceExecution = context
            .mock(PersistResourceExecution.class);

    /**
     * Factory to create person.
     */
    private final CreatePersonActionFactory createPersonActionFactory = context.mock(CreatePersonActionFactory.class);

    /**
     * Person mapper.
     */
    private final PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * {@link CreatePersonRequest}.
     */
    private final CreatePersonRequest createRequest = context.mock(CreatePersonRequest.class);

    /**
     * {@link Person}.
     */
    private final Person person = context.mock(Person.class);

    /**
     * Org id used in test.
     */
    private final Long orgId = 1L;

    /**
     * System under test.
     */
    private final CreatePersonExecution sut = new CreatePersonExecution(createPersonActionFactory, personMapper,
            "emailKey");

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(true));

                allowing(person).getEmail();
                will(returnValue("eamil@email.com"));
            }
        });

        TaskHandlerActionContext<ActionContext> taskHandlerActionContext = TestContextCreator
                .createTaskHandlerAsyncContext(createRequest);
        List<UserActionRequest> list = taskHandlerActionContext.getUserActionRequests();
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
        context.checking(new Expectations()
        {
            {
                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(false));
            }
        });

        TaskHandlerActionContext<ActionContext> taskHandlerActionContext = TestContextCreator
                .createTaskHandlerAsyncContext(createRequest);
        List<UserActionRequest> list = taskHandlerActionContext.getUserActionRequests();
        sut.execute(taskHandlerActionContext);

        assertTrue(list.isEmpty());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNullEmailActionKey()
    {
        CreatePersonExecution tempSut = new CreatePersonExecution(createPersonActionFactory, personMapper, null);

        context.checking(new Expectations()
        {
            {
                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(true));
            }
        });

        TaskHandlerActionContext<ActionContext> taskHandlerActionContext = TestContextCreator
                .createTaskHandlerAsyncContext(createRequest);
        List<UserActionRequest> list = taskHandlerActionContext.getUserActionRequests();
        tempSut.execute(taskHandlerActionContext);

        assertTrue(list.isEmpty());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testEmptyEmailActionKey()
    {
        CreatePersonExecution tempSut = new CreatePersonExecution(createPersonActionFactory, personMapper, "");

        context.checking(new Expectations()
        {
            {
                allowing(createRequest).getPerson();
                will(returnValue(person));

                allowing(person).getAccountId();

                allowing(person).getProperties();

                allowing(createPersonActionFactory).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceExecution));

                allowing(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(person));

                allowing(createRequest).getSendEmail();
                will(returnValue(true));
            }
        });

        TaskHandlerActionContext<ActionContext> taskHandlerActionContext = TestContextCreator
                .createTaskHandlerAsyncContext(createRequest);
        List<UserActionRequest> list = taskHandlerActionContext.getUserActionRequests();
        tempSut.execute(taskHandlerActionContext);

        assertTrue(list.isEmpty());

        context.assertIsSatisfied();
    }
}
