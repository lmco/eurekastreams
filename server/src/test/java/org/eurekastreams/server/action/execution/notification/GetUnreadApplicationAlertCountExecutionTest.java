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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.cache.GetCachedAlertCountByUserId;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests execution strategy for GetUnreadApplicationAlertCount.
 */
public class GetUnreadApplicationAlertCountExecutionTest
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
     * System under test.
     */
    private GetUnreadApplicationAlertCountExecution sut;

    /**
     * PrincipalActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Alert count mapper mock.
     */
    private GetCachedAlertCountByUserId alertCountMapper = context.mock(GetCachedAlertCountByUserId.class);

    /**
     * Setup.
     */
    @Before
    public final void setUp()
    {
        sut = new GetUnreadApplicationAlertCountExecution(alertCountMapper);
    }

    /**
     * Tests the execute method.
     */
    @Test
    public void testExecute()
    {
        final long userId = 123L;
        final int count = 17;

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(userId));

                oneOf(alertCountMapper).execute(userId);
                will(returnValue(count));
            }
        });

        int results = (Integer) sut.execute(actionContext);
        assertEquals(results, count);
        context.assertIsSatisfied();
    }
}
