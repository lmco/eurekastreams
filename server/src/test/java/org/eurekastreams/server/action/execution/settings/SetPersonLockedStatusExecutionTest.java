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

import static junit.framework.Assert.assertEquals;

import java.util.Collection;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.SetPersonLockedStatus;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for SetPersonLockedStatusExecution.
 *
 */
public class SetPersonLockedStatusExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final String ACCOUNT_ID = "jdoe";

    /** Test data. */
    private static final Long PERSON_ID = 7L;

    /** {@link SetPersonLockedStatus}. */
    private final SetPersonLockedStatus setLockedStatusDAO = context.mock(SetPersonLockedStatus.class);;

    /** For mapping accountid to id. */
    private final DomainMapper<String, Long> personIdMapper = context.mock(DomainMapper.class, "personIdMapper");

    /** System under test. */
    private SetPersonLockedStatusExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SetPersonLockedStatusExecution(setLockedStatusDAO, personIdMapper);
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteWhenLocking()
    {
        final SetPersonLockedStatusRequest request = new SetPersonLockedStatusRequest(ACCOUNT_ID, true);
        context.checking(new Expectations()
        {
            {
                oneOf(setLockedStatusDAO).execute(request);

                allowing(personIdMapper).execute(ACCOUNT_ID);
                will(returnValue(PERSON_ID));
            }
        });

        TaskHandlerActionContext<ActionContext> outerActionContext = TestContextCreator
                .createTaskHandlerAsyncContext(request);
        sut.execute(outerActionContext);

        context.assertIsSatisfied();
        assertEquals(1, outerActionContext.getUserActionRequests().size());
        UserActionRequest outRequest = outerActionContext.getUserActionRequests().get(0);
        assertEquals("deleteCacheKeysAction", outRequest.getActionKey());
        Collection<String> keys = (Collection<String>) outRequest.getParams();
        assertEquals(1, keys.size());
        assertEquals(CacheKeys.PERSON_BY_ID + PERSON_ID, keys.iterator().next());
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteWhenUnlocking()
    {
        final SetPersonLockedStatusRequest request = new SetPersonLockedStatusRequest(ACCOUNT_ID, false);
        context.checking(new Expectations()
        {
            {
                oneOf(setLockedStatusDAO).execute(request);

                allowing(personIdMapper).execute(ACCOUNT_ID);
                will(returnValue(PERSON_ID));
            }
        });

        TaskHandlerActionContext<ActionContext> outerActionContext = TestContextCreator
                .createTaskHandlerAsyncContext(request);
        sut.execute(outerActionContext);

        context.assertIsSatisfied();
        assertEquals(1, outerActionContext.getUserActionRequests().size());
        UserActionRequest outRequest = outerActionContext.getUserActionRequests().get(0);
        assertEquals("cachePerson", outRequest.getActionKey());
        assertEquals(PERSON_ID, outRequest.getParams());
    }

}
