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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.start.SetGadgetStateRequest;
import org.eurekastreams.server.action.request.start.SetGadgetStateRequest.State;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.persistence.GadgetMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetGadgetStateExecution} class.
 *
 */
public class SetGadgetStateExecutionTest
{
    /**
     * System under test.
     */
    private SetGadgetStateExecution sut;

    /**
     * A gadget id for testing.
     */
    private static final Long GADGET_ID1 = 10L;

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
     * Mocked mapper for retrieving the tab.
     */
    private GadgetMapper gadgetMapper = context.mock(GadgetMapper.class);

    /**
     * Mocked instance of the principal object for this test suite.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetGadgetStateExecution(gadgetMapper);
    }

    /**
     * Call the action and make sure it does the minimize and returns the right tab.
     *
     * @throws Exception
     *             should not be thrown
     */
    @Test
    public void performActionWithGoodParams() throws Exception
    {
        final Gadget expected = context.mock(Gadget.class);

        context.checking(new Expectations()
        {
            {
                oneOf(gadgetMapper).findById(GADGET_ID1);
                will(returnValue(expected));

                oneOf(expected).setMinimized(true);
                oneOf(expected).setMaximized(false);

                oneOf(gadgetMapper).flush();
            }
        });

        SetGadgetStateRequest currentRequest = new SetGadgetStateRequest(GADGET_ID1,
                State.MINIMIZED);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        Gadget actual = sut.execute(currentContext);

        assertEquals("Did not return the right Tab", actual, expected);
    }

    /**
     * If the GadgetId doesn't belong to the tab, we won't minimize it. In that case, throw an exception.
     *
     * @throws Exception
     *             expecting a GadgetMinimizationException
     */
    @Test(expected = ExecutionException.class)
    public void performActionWithBadGadgetId() throws Exception
    {
        final Long noSuchGadgetId = 99L;

        context.checking(new Expectations()
        {
            {
                oneOf(gadgetMapper).findById(noSuchGadgetId);
                will(returnValue(null));

            }
        });

        SetGadgetStateRequest currentRequest = new SetGadgetStateRequest(noSuchGadgetId,
                State.MINIMIZED);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);
    }
}
