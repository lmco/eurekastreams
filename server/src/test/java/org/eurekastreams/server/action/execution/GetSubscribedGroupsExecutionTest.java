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

package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for GetSubscribedGroupsExecution.
 */
public class GetSubscribedGroupsExecutionTest
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
     * Action contest passed into execution.
     */
    private final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mapper to get the subscribed groups for a person.
     */
    @SuppressWarnings("unchecked")
    private DomainMapper<Long, ArrayList<String>> getSubscribedGroupsMapper = context.mock(DomainMapper.class);

    /**
     * Principal.
     */
    private final Principal principal = context.mock(Principal.class);

    /**
     * Test with no group found.
     */
    @Test
    public void testWhenNoGroupFound()
    {
        final long personId = 87374L;
        final ArrayList<String> groups = new ArrayList<String>();

        GetSubscribedGroupsExecution sut = new GetSubscribedGroupsExecution(getSubscribedGroupsMapper);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue("snuts"));

                oneOf(principal).getId();
                will(returnValue(personId));

                oneOf(getSubscribedGroupsMapper).execute(personId);
                will(returnValue(groups));
            }
        });

        assertSame(groups, sut.execute(actionContext));
        context.assertIsSatisfied();
    }

}
