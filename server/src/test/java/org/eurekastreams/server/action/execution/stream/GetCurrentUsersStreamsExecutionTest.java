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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the action to get the current user's streams.
 */
public class GetCurrentUsersStreamsExecutionTest
{

    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Streams mapper.
     */
    private static DomainMapper<Long, List<StreamFilter>> getUserStreamsMapper = CONTEXT.mock(DomainMapper.class);

    /**
     * Person mapper.
     */
    private static FindByIdMapper<Person> personMapper = CONTEXT.mock(FindByIdMapper.class);

    /**
     * Action context.
     */
    private static PrincipalActionContext actionContext = CONTEXT.mock(PrincipalActionContext.class);

    /**
     * Principle.
     */
    private static Principal principal = CONTEXT.mock(Principal.class);

    /**
     * User ID.
     */
    private static final Long USER_ID = 5L;

    /**
     * System under test.
     */
    private static GetCurrentUsersStreamsExecution sut = null;

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new GetCurrentUsersStreamsExecution(getUserStreamsMapper, personMapper);
    }

    /**
     * Test execution.
     */
    @Test
    public void testExecute()
    {
        final List<StreamFilter> filters = new ArrayList<StreamFilter>();

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(USER_ID));

                oneOf(getUserStreamsMapper).execute(USER_ID);
                will(returnValue(filters));
            }
        });

        Assert.assertEquals(filters, sut.execute(actionContext));

        CONTEXT.assertIsSatisfied();
    }
}
