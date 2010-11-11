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
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabGroupMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.exceptions.TabDeletionException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link DeleteTabExecution} class.
 * 
 */
public class DeleteTabExecutionTest
{
    /**
     * System under test.
     */
    private DeleteTabExecution sut;

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
     * The mock TabMapper to be used by the action.
     */
    private final TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * The mock TabGroupMapper to be used by the action.
     */
    private final TabGroupMapper tabGroupMapper = context.mock(TabGroupMapper.class);

    /**
     * The Tab that will get deleted during the test.
     */
    private Tab delTab = null;

    /**
     * Principal mock object.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * {@link DomainMapper}.
     */
    private DomainMapper<Set<String>, Boolean> deleteCacheKeysMapper = context.mock(DomainMapper.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        delTab = new Tab("delete Tab", Layout.THREECOLUMN);
        sut = new DeleteTabExecution(tabGroupMapper, tabMapper, deleteCacheKeysMapper);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     * 
     * @throws Exception
     *             should not occur
     */
    @Test
    public final void testPerformAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findById(with(any(Long.class)));
                will(returnValue(delTab));

                oneOf(tabGroupMapper).deleteTab(delTab);

                allowing(deleteCacheKeysMapper).execute(with(any(Set.class)));

                allowing(principalMock).getId();
                will(returnValue(1L));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(new Long(1L), principalMock);

        Boolean actual = sut.execute(currentContext);

        assertEquals(Boolean.TRUE, actual);
        context.assertIsSatisfied();
    }

    /**
     * Call the execute method and make sure it produces what it should under failure circumstances.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test(expected = ExecutionException.class)
    public final void testPerformActionWithException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findById(with(any(Long.class)));
                will(returnValue(delTab));

                oneOf(tabGroupMapper).deleteTab(delTab);
                will(throwException(new TabDeletionException("Failed to delete tab", delTab)));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(new Long(1L), principalMock);

        Boolean actual = sut.execute(currentContext);

        assertEquals(Boolean.TRUE, actual);
        context.assertIsSatisfied();
    }
}
