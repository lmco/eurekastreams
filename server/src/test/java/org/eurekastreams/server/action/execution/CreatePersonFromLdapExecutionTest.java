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

import static junit.framework.Assert.assertNull;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for CreatePersonFromLdapExecution.
 * 
 */
public class CreatePersonFromLdapExecutionTest
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
     * {@link PersonLookupStrategy}.
     */
    private PersonLookupStrategy ldapPersonMapper = context.mock(PersonLookupStrategy.class);

    /**
     * {@link GetRootOrganizationIdAndShortName}.
     */
    private GetRootOrganizationIdAndShortName rootOrgIdDAO = context.mock(GetRootOrganizationIdAndShortName.class);

    /**
     * Create person strategy.
     */
    private TaskHandlerExecutionStrategy<ActionContext> createPersonStrategy = context
            .mock(TaskHandlerExecutionStrategy.class);

    /**
     * Mocked instance of the {@link TaskHandlerActionContext} class.
     */
    private final TaskHandlerActionContext<ActionContext> taskHandlerActionContextMock = //
    context.mock(TaskHandlerActionContext.class);

    /**
     * Mocked instance of the {@link ServiceActionContext} class.
     */
    private final ServiceActionContext serviceActionContextMock = context.mock(ServiceActionContext.class);

    /**
     * User account id for test.
     */
    private final String userAcctId = "userAcctId";

    /**
     * System under test.
     */
    private CreatePersonFromLdapExecution sut;

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new CreatePersonFromLdapExecution(ldapPersonMapper, rootOrgIdDAO, createPersonStrategy);
    }

    /**
     * Test.
     */
    @Test
    public final void testNullResult()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContextMock).getActionContext();
                will(returnValue(serviceActionContextMock));

                oneOf(serviceActionContextMock).getParams();
                will(returnValue(userAcctId));

                // Any value greater than 0
                oneOf(ldapPersonMapper).findPeople(userAcctId, 1);
                will(returnValue(null));
            }
        });

        Person result = sut.execute(taskHandlerActionContextMock);
        assertNull(result);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public final void testEmptyResult()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContextMock).getActionContext();
                will(returnValue(serviceActionContextMock));

                oneOf(serviceActionContextMock).getParams();
                will(returnValue(userAcctId));

                // Any value greater than 0
                oneOf(ldapPersonMapper).findPeople(userAcctId, 1);
                will(returnValue(new ArrayList<Person>()));
            }
        });

        Person result = sut.execute(taskHandlerActionContextMock);
        assertNull(result);
        context.assertIsSatisfied();
    }
}
