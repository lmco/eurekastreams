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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.authorization.IsRootOrganizationCoordinator;
import org.eurekastreams.server.domain.Person;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for IsRootOrgCoordinatorExecution class.
 * 
 */
public class IsRootOrgCoordinatorExecutionTest
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
     * The subject under test.
     */
    private IsRootOrgCoordinatorExecution sut;

    /**
     * The IsRootOrganizationCoordinatorStrategy.
     */
    private IsRootOrganizationCoordinator strategy = context.mock(IsRootOrganizationCoordinator.class);

    /**
     * Mocked user information in the session.
     */
    private PrincipalActionContext pac = context.mock(PrincipalActionContext.class);

    /**
     * Mock principal.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Mock person.
     */
    private Person user = context.mock(Person.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new IsRootOrgCoordinatorExecution(strategy);
    }

    /**
     * Verify that the security check calls all expected interfaces.
     */
    @Test
    public void testExecuteTrue()
    {
        final Long id = 1L;

        context.checking(new Expectations()
        {
            {
                allowing(pac).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(id));

                allowing(strategy).isRootOrganizationCoordinator(id);
                will(returnValue(true));
            }
        });

        assertTrue((Boolean) sut.execute(pac));
        context.assertIsSatisfied();
    }

    /**
     * Verify that the security check calls all expected interfaces.
     */
    @Test
    public void testExecuteFail()
    {
        final Long id = 1L;

        context.checking(new Expectations()
        {
            {
                allowing(pac).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(id));

                allowing(strategy).isRootOrganizationCoordinator(id);
                will(returnValue(false));
            }
        });

        assertFalse((Boolean) sut.execute(pac));
        context.assertIsSatisfied();
    }

}
