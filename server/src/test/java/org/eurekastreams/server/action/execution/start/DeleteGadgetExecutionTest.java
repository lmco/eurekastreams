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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link DeleteGadgetExecution} class.
 *
 */
public class DeleteGadgetExecutionTest
{
    /**
     * System under test.
     */
    private DeleteGadgetExecution sut;

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
     * The mock user information from the session.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * An id for a gadget to be deleted. Arbitrary.
     */
    private static final Long NO_SUCH_GADGET_ID = 999L;

    /**
     * ID of a gadget in the set mocked up for testing.
     */
    private static final long GADGET1_ID = 10;

    /**
     * ID of a gadget in the set mocked up for testing.
     */
    private static final long GADGET2_ID = 20;

    /**
     * ID of a gadget in the set mocked up for testing.
     */
    private static final long GADGET3_ID = 30;

    /**
     * ID of a gadget in the set mocked up for testing.
     */
    private static final long GADGET4_ID = 40;

    /**
     * ID of a gadget in the set mocked up for testing.
     */
    private static final long GADGET5_ID = 50;

    /**
     * ID of a gadget in the set mocked up for testing.
     */
    private static final long GADGET6_ID = 60;

    /**
     * The mock mapper to be used by the action.
     */
    private TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * Having a state lets me make the gadgets return different values at
     * different times.
     */
    private final States index = context.states("index");

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new DeleteGadgetExecution(tabMapper);
    }

    /**
     * Test the normal case.
     *
     * @throws Exception
     *             not expected to occur
     */
    @Test
    public void performActionsWithGoodParams() throws Exception
    {
        final long targetGadgetId = GADGET3_ID;
        final List<Gadget> gadgets = buildGadgets(targetGadgetId);
        final Tab tab = context.mock(Tab.class);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findTabByGadgetId(GADGET3_ID);
                will(returnValue(tab));

                oneOf(tab).getGadgets();
                will(returnValue(gadgets));

                // delete the target
                oneOf(tabMapper).deleteGadget(gadgets.get(2));

                // write back to the database
                oneOf(tabMapper).flush();
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(targetGadgetId, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Delete the only gadget in a zone to make sure it's handled right.
     *
     * @throws Exception
     *             not expected to occur
     */
    @Test
    public void performActionsDeleteOnlyGadgetInZone() throws Exception
    {
        final long targetGadgetId = GADGET1_ID;
        final List<Gadget> gadgets = buildGadgets(targetGadgetId);
        final Tab tab = context.mock(Tab.class);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findTabByGadgetId(GADGET1_ID);
                will(returnValue(tab));

                oneOf(tab).getGadgets();
                will(returnValue(gadgets));

                // delete the target
                oneOf(tabMapper).deleteGadget(gadgets.get(0));

                // there are no other gadgets in the zone, so no other ones to
                // rearrange

                // write back to the database
                oneOf(tabMapper).flush();
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(targetGadgetId, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Delete the last gadget in the collection to make sure we don't overrun.
     *
     * @throws Exception
     *             not expected to occur
     */
    @Test
    public void performActionsDeleteGadget() throws Exception
    {
        final long targetGadgetId = GADGET6_ID;
        final List<Gadget> gadgets = buildGadgets(targetGadgetId);
        final Tab tab = context.mock(Tab.class);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findTabByGadgetId(GADGET6_ID);
                will(returnValue(tab));

                oneOf(tab).getGadgets();
                will(returnValue(gadgets));

                // delete the target
                oneOf(tabMapper).deleteGadget(gadgets.get(5));

                // there are no other gadgets in the zone, so no other ones to
                // rearrange

                // write back to the database
                oneOf(tabMapper).flush();
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(targetGadgetId, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test that an invalid gadget id is handled.
     * @throws Exception should be thrown
     */
    @Test(expected = ExecutionException.class)
    public void performActionWithInvalidGadgetId() throws Exception
    {
        final long targetGadgetId = NO_SUCH_GADGET_ID;
        final List<Gadget> gadgets = buildGadgets(targetGadgetId);
        final Tab tab = context.mock(Tab.class);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findTabByGadgetId(NO_SUCH_GADGET_ID);
                will(returnValue(tab));

                oneOf(tab).getGadgets();
                will(returnValue(gadgets));

                // an exception will be thrown before doing anything else
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(targetGadgetId, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Builds a collection of mocked Gadgets. Each gadget knows its ID, zone
     * number, and zone index.
     *
     * @param targetGadgetId
     *            the gadget that is being targeted in the current test.
     * @return List of mocked gadgets.
     */
    private List<Gadget> buildGadgets(final long targetGadgetId)
    {
        final ArrayList<Gadget> gadgets = new ArrayList<Gadget>();

        context.checking(new Expectations()
        {
            {
                gadgets.add(setupGadget(GADGET1_ID, targetGadgetId, 0, 0));
                gadgets.add(setupGadget(GADGET2_ID, targetGadgetId, 1, 0));
                gadgets.add(setupGadget(GADGET3_ID, targetGadgetId, 1, 1));
                gadgets.add(setupGadget(GADGET4_ID, targetGadgetId, 1, 2));
                gadgets.add(setupGadget(GADGET5_ID, targetGadgetId, 1, 3));
                gadgets.add(setupGadget(GADGET6_ID, targetGadgetId, 2, 0));
            }

            private Gadget setupGadget(final long gadgetId,
                    final long targetGadgetId, final int zoneNumber,
                    final int zoneIndex)
            {
                Gadget gadget = context.mock(Gadget.class, "gadget"
                        + Long.toString(gadgetId));
                allowing(gadget).getId();
                will(returnValue(gadgetId));

                allowing(gadget).getZoneNumber();
                will(returnValue(zoneNumber));

                allowing(gadget).getZoneIndex();
                if (gadgetId == targetGadgetId)
                {
                    when(index.is("initial"));
                }
                will(returnValue(zoneIndex));

                return gadget;
            }
        });

        return gadgets;
    }
}
