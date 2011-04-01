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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.search.modelview.UsageMetricDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RegisterUserMetricExecution.
 * 
 */
public class RegisterUserMetricExecutionTest
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
     * Mocked isntance of the {@link Principal}.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked instance of the {@link PrincipalActionContext}.
     */
    private final PrincipalActionContext principalActionContextMock = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of the {@link TaskHandlerActionContext}.
     */
    private final TaskHandlerActionContext taskHandlerActionContextMock = context.mock(TaskHandlerActionContext.class);

    /**
     * Mocked instance of the UsageMetricDTO.
     */
    private final UsageMetricDTO um = context.mock(UsageMetricDTO.class);

    /**
     * System under test.
     */
    private RegisterUserMetricExecution sut = new RegisterUserMetricExecution();

    /**
     * Test performing the action.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformAction() throws Exception
    {
        final ArrayList<UserActionRequest> uar = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(um));

                allowing(um).isStreamView();
                will(returnValue(true));

                allowing(um).getMetricDetails();
                will(returnValue("metric details"));

                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(principalMock).getAccountId();
                will(returnValue("accountId"));

                allowing(taskHandlerActionContextMock).getUserActionRequests();
                will(returnValue(uar));
            }
        });

        sut.execute(taskHandlerActionContextMock);

        assertEquals(1, uar.size());

        context.assertIsSatisfied();
    }
}
