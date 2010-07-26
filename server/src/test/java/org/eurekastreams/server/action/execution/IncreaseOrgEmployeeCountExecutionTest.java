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

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.IncreaseOrgEmployeeCountRequest;
import org.eurekastreams.server.persistence.mappers.db.IncreaseOrgEmployeeCount;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for IncreaseOrgEmployeeCountExecution class.
 */
public class IncreaseOrgEmployeeCountExecutionTest
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
    private IncreaseOrgEmployeeCountExecution sut;

    /**
     * {@link IncreaseOrgEmployeeCount} mock.
     */
    private IncreaseOrgEmployeeCount mapper = context.mock(IncreaseOrgEmployeeCount.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Setup.
     */
    @Before
    public final void setUp()
    {
        sut = new IncreaseOrgEmployeeCountExecution(mapper);
    }

    /**
     * Test performing the action.
     */
    @Test
    public final void testPerformAction()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new IncreaseOrgEmployeeCountRequest(0, 0)));

                oneOf(mapper).execute(with(any(IncreaseOrgEmployeeCountRequest.class)));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
