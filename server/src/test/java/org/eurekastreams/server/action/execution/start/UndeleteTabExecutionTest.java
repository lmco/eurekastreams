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
package org.eurekastreams.server.action.execution.start;

import static junit.framework.Assert.assertEquals;

import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabGroupMapper;
import org.eurekastreams.server.persistence.exceptions.TabUndeletionException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for UndeleteTabExecution class.
 * 
 */
public class UndeleteTabExecutionTest
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
     * The mock mapper to be used by the action.
     */
    private TabGroupMapper tabGroupMapper = context.mock(TabGroupMapper.class);

    /**
     * The tab the tabGroupMapper.undeleteTab will return.
     */
    private Tab undeletedTab = context.mock(Tab.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock object.
     */
    private final Principal principal = context.mock(Principal.class);

    /**
     * Tab id.
     */
    private Long tabId = 5L;

    /**
     * Subject under test.
     */
    private UndeleteTabExecution sut = null;

    /**
     * {@link DomainMapper}.
     */
    private DomainMapper<Set<String>, Boolean> deleteCacheKeysMapper = context.mock(DomainMapper.class);

    /**
     * Pre-test setup.
     * 
     */
    @Before
    public final void setup()
    {
        sut = new UndeleteTabExecution(tabGroupMapper, deleteCacheKeysMapper);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testExecute() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(tabId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(1L));

                allowing(deleteCacheKeysMapper).execute(with(any(Set.class)));

                allowing(tabGroupMapper).undeleteTab(tabId.longValue());
                will(returnValue(undeletedTab));
            }
        });

        Tab actual = sut.execute(actionContext);

        assertEquals("The undeleteTab action didn't return the expected Tab", undeletedTab, actual);

        context.assertIsSatisfied();
    }

    /**
     * Test exception.
     * 
     * @throws Exception
     *             expected
     */
    @Test(expected = ExecutionException.class)
    public final void testExecuteFail() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(tabId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(1L));

                allowing(deleteCacheKeysMapper).execute(with(any(Set.class)));

                oneOf(tabGroupMapper).undeleteTab(with(tabId.longValue()));
                will(throwException(new TabUndeletionException("string", tabId)));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

}
