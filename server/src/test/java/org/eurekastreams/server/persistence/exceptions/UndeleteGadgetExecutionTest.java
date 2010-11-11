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
package org.eurekastreams.server.persistence.exceptions;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.execution.start.UndeleteGadgetExecution;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link UndeleteGadgetExecution} class.
 * 
 */
public class UndeleteGadgetExecutionTest
{
    /**
     * Subject under test.
     */
    private UndeleteGadgetExecution sut;

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
    private final TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * The tab the Gadget to be undeleted lives on.
     */
    private final Tab tab = context.mock(Tab.class);

    /**
     * Mocked instance of Principal class for this test suite.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * A tab id to use for testing. Everything's mocked, so the number doesn't matter.
     */
    private static final long TAB_ID = 23;

    /**
     * A gadget id to use for testing. Everything's mocked, so the number doesn't matter.
     */
    private static final long GADGET_ID = 74;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new UndeleteGadgetExecution(tabMapper);
    }

    /**
     * Have the Action undelete a gadget with nothing going wrong.
     * 
     * @throws Exception
     *             should not be thrown
     */
    @Test
    public void performAction() throws Exception
    {
        final Gadget undeletedGadget = context.mock(Gadget.class);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).undeleteGadget(GADGET_ID);
                will(returnValue(undeletedGadget));
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(new Long(GADGET_ID), principalMock);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Have the Action undelete a gadget with exception being thrown.
     * 
     * @throws Exception
     *             should not be thrown
     */
    @Test(expected = ExecutionException.class)
    public void performActionFail() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).undeleteGadget(GADGET_ID);
                will(throwException(new GadgetUndeletionException("Error undeleting", GADGET_ID)));
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(new Long(GADGET_ID), principalMock);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }
}
