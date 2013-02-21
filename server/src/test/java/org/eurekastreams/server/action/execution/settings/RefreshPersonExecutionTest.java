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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.db.UpdatePersonMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdatePersonResponse;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RefreshPersonExecution.
 * 
 */
public class RefreshPersonExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link UpdatePersonMapper}.
     */
    private UpdatePersonMapper updatePersonMapper = context.mock(UpdatePersonMapper.class);

    /**
     * Async updater for person activity caches.
     */
    private CacheUpdater personActivityCacheUpdater = context.mock(CacheUpdater.class);

    /**
     * Person.
     */
    private Person ldapPerson = context.mock(Person.class, "person");

    /**
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * System under test.
     */
    private RefreshPersonExecution sut = new RefreshPersonExecution(updatePersonMapper, personActivityCacheUpdater);

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(ldapPerson));

                oneOf(updatePersonMapper).execute(with(ldapPerson));
                will(returnValue(new UpdatePersonResponse(1L, true, false)));

                allowing(ldapPerson).getAccountId();
                will(returnValue("foo"));

                oneOf(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(requests));
            }
        });

        sut.execute(taskHandlerActionContext);

        assertEquals(1, requests.size());
        context.assertIsSatisfied();
    }

    /**
     * Test with update and display name update.
     */
    @Test
    public void testExecuteWithDisplayNameUpdate()
    {
        final ArrayList<UserActionRequest> requests = new ArrayList<UserActionRequest>();

        final List<UserActionRequest> displayNameRequests = new ArrayList<UserActionRequest>();
        UserActionRequest updateRequest = new UserActionRequest("foo", null, null);
        displayNameRequests.add(updateRequest);

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(ldapPerson));

                oneOf(updatePersonMapper).execute(with(ldapPerson));
                will(returnValue(new UpdatePersonResponse(1L, true, true)));

                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(requests));

                allowing(ldapPerson).getAccountId();
                will(returnValue("foo"));

                oneOf(personActivityCacheUpdater).getUpdateCacheRequests(null, new Long(1L));
                will(returnValue(displayNameRequests));
            }
        });

        sut.execute(taskHandlerActionContext);

        assertEquals(2, requests.size());
        assertTrue(requests.contains(updateRequest));
        context.assertIsSatisfied();
    }

    /**
     * Test where no cache update is needed.
     */
    @Test
    public void testExecuteNoUpdate()
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(ldapPerson));

                allowing(ldapPerson).getAccountId();
                will(returnValue("foo"));

                oneOf(updatePersonMapper).execute(with(ldapPerson));
                will(returnValue(new UpdatePersonResponse(1L, false, false)));
            }
        });

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }
}
