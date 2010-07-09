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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.start.ReorderGadgetRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for ReorderGadgetExecution class.
 *
 */
public class ReorderGadgetExecutionTest
{
    /**
     * Collection of jmock states, one for each gadget's zone number and one for each gadget's zone index. The naming is
     * the gadgetId plus '.zoneIndex'.
     */
    private HashMap<String, States> gadgetStates;

    /**
     * Context for building mock objects.
     */
    private Mockery context;

    /**
     * Subject under test.
     */
    private ReorderGadgetExecution sut;

    /**
     * The SUT will use this to retrieve and save the Tab it's going to change.
     */
    private TabMapper tabMapper;

    /**
     * Mock tab1.
     */
    private Tab tab1;

    /**
     * Mock tab1 template.
     */
    private TabTemplate tab1Template;

    /**
     * Mock tab2.
     */
    private Tab tab2;

    /**
     * Mock tab2 template.
     */
    private TabTemplate tab2Template;

    /**
     * Gagdet collection for tab 1.
     */
    List<Gadget> tab1Gadgets;

    /**
     * Gadget collection for tab 2.
     */
    List<Gadget> tab2Gadgets;

    /**
     * Gadget IDs to use for tab 1.
     */
    private static ArrayList<Long> tab1GadgetIds = new ArrayList<Long>();

    /**
     * Gadget IDs to use for tab 2.
     */
    private static ArrayList<Long> tab2GadgetIds = new ArrayList<Long>();

    /**
     * Setup the array of GadgetIds.
     */
    @BeforeClass
    public static void setupClass()
    {
        // tab 1 gadget ids - start with 1031, then keep adding add 8
        final long tab1StartingIndex = 1031;
        tab1GadgetIds.add(tab1StartingIndex);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);
        tab1GadgetIds.add(tab1GadgetIds.get(tab1GadgetIds.size() - 1) + 8);

        // tab 2 gadget ids - start with 8483, then keep adding add 4
        final long tab2StartingIndex = 8483;
        tab2GadgetIds.add(tab2StartingIndex);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
        tab2GadgetIds.add(tab2GadgetIds.get(tab2GadgetIds.size() - 1) + 4);
    }

    /**
     * Set up the SUT, two tabs and their gadgets collections.
     */
    @Before
    public void setup()
    {
        context = new JUnit4Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        gadgetStates = new HashMap<String, States>();

        tabMapper = context.mock(TabMapper.class);
        tab1 = context.mock(Tab.class, "tab1");
        tab2 = context.mock(Tab.class, "tab2");
        tab1Template = context.mock(TabTemplate.class, "tab1Template");
        tab2Template = context.mock(TabTemplate.class, "tab2Template");

        sut = new ReorderGadgetExecution(tabMapper);

        tab1Gadgets = new ArrayList<Gadget>();
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(0), 0, 0)); // [0]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(1), 0, 1)); // [1]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(2), 0, 2)); // [2]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(3), 1, 0)); // [3]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(4), 1, 1)); // [4]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(5), 1, 2)); // [5]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(6), 3, 0)); // [6]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(7), 3, 1)); // [7]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(8), 3, 2)); // [8]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(9), 3, 3)); // [9]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(9 + 1), 3, 4)); // [10]
        tab1Gadgets.add(getGadget(tab1GadgetIds.get(9 + 2), 3, 5)); // [11]

        tab2Gadgets = new ArrayList<Gadget>();
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(0), 0, 0)); // [0]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(1), 0, 1)); // [1]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(2), 0, 2)); // [2]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(3), 1, 0)); // [3]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(4), 1, 1)); // [4]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(5), 1, 2)); // [5]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(6), 3, 0)); // [6]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(7), 3, 1)); // [7]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(8), 3, 2)); // [8]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(9), 3, 3)); // [9]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(9 + 1), 3, 4)); // [10]
        tab2Gadgets.add(getGadget(tab2GadgetIds.get(9 + 2), 3, 5)); // [11]

        context.checking(new Expectations()
        {
            {
                final long tab1TemplateId = 8147L;
                final long tab2TemplateId = 8274L;

                // TabTemplate 1
                allowing(tab1Template).getId();
                will(returnValue(tab1TemplateId));

                allowing(tab1Template).getGadgets();
                will(returnValue(tab1Gadgets));

                allowing(tab1Template).getTabLayout();
                will(returnValue(Layout.THREECOLUMNRIGHTWIDEHEADER));

                // TabTemplate 2
                allowing(tab2Template).getId();
                will(returnValue(tab2TemplateId));

                allowing(tab2Template).getGadgets();
                will(returnValue(tab2Gadgets));

                allowing(tab2Template).getTabLayout();
                will(returnValue(Layout.THREECOLUMNLEFTWIDEHEADER));
            }
        });

        context.checking(new Expectations()
        {
            {
                final long tab1Id = 4781L;
                final long tab2Id = 7482L;

                // tab 1:
                allowing(tab1).getId();
                will(returnValue(tab1Id));

                allowing(tab1).getGadgets();
                will(returnValue(tab1Template.getGadgets()));

                allowing(tab1).getTabLayout();
                will(returnValue(tab1Template.getTabLayout()));

                allowing(tab1).getTemplate();
                will(returnValue(tab1Template));

                // tab 2:
                allowing(tab2).getId();
                will(returnValue(tab2Id));

                allowing(tab2).getGadgets();
                will(returnValue(tab2Template.getGadgets()));

                allowing(tab2).getTabLayout();
                will(returnValue(tab2Template.getTabLayout()));

                allowing(tab2).getTemplate();
                will(returnValue(tab2Template));
            }
        });
    }

    /**
     * Move a gadget within a tab to an earlier index.
     *
     * @throws Exception
     *             don't expect this to be thrown.
     */
    @Test
    public void performActionMoveToEarlierZoneWithinSameTab() throws Exception
    {
        final long gadgetId = tab1GadgetIds.get(4);

        setupSourceAndDestinationTabs(tab1, tab1, gadgetId);

        assertMapperMoveCall(gadgetId, tab1.getTemplate().getId(), 1, 1, tab1.getTemplate().getId(), 1, 0);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ReorderGadgetRequest request = new ReorderGadgetRequest(tab1.getId(), gadgetId, 0, 1);
        final Map<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(tabMapper).findById(tab1.getId());
                will(returnValue(tab1));
            }
        });

        Tab resultTab = sut.execute(actionContext);

        // make sure the right tab was returned
        assertSame(tab1, resultTab);

        // verify mock assertions
        context.assertIsSatisfied();
    }

    /**
     * Move a gadget within a tab to a later index.
     *
     * Move from [1,1] to [4,0]
     *
     * @throws Exception
     *             don't expect this to be thrown.
     */
    @Test
    public void performActionMoveToLaterZoneWithinSameTab() throws Exception
    {
        final long gadgetId = tab1GadgetIds.get(4);
        setupSourceAndDestinationTabs(tab1, tab1, gadgetId);

        assertMapperMoveCall(gadgetId, tab1.getTemplate().getId(), 1, 1, tab1.getTemplate().getId(), 2, 3);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ReorderGadgetRequest request = new ReorderGadgetRequest(tab1.getId(), gadgetId, 3, 2);
        final Map<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(tabMapper).findById(tab1.getId());
                will(returnValue(tab1));
            }
        });

        // Perform the action
        Tab resultTab = sut.execute(actionContext);

        // make sure the right tab was returned
        assertSame(tab1, resultTab);

        // verify mock assertions
        context.assertIsSatisfied();
    }

    /**
     * Move from tab 1 to tab2.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public void performActionMoveToEmptyZoneAcrossTabs() throws Exception
    {
        final long gadgetId = tab1GadgetIds.get(4);
        final int destinationZoneNumber = 2;
        final int destinationZoneIndex = 0;

        setupSourceAndDestinationTabs(tab1, tab2, gadgetId);

        assertMapperMoveCall(gadgetId, tab1.getTemplate().getId(), 1, 1, tab2.getTemplate().getId(), 0, 2);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ReorderGadgetRequest request = new ReorderGadgetRequest(tab2.getId(), gadgetId, destinationZoneNumber,
                destinationZoneIndex);
        final Map<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(tabMapper).findById(tab2.getId());
                will(returnValue(tab2));
            }
        });

        // Perform the action
        Tab resultTab = sut.execute(actionContext);

        // make sure the right tab was returned
        assertSame(tab2, resultTab);

        // verify mock assertions
        context.assertIsSatisfied();
    }

    /**
     * Move out of a zone, leaving it empty.
     *
     * Move from tab 2's (1,0) to tab 1's (0,0).
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public void performActionLeaveZoneEmpty() throws Exception
    {
        final long gadgetId = tab2GadgetIds.get(3);
        setupSourceAndDestinationTabs(tab2, tab1, gadgetId);

        assertMapperMoveCall(gadgetId, tab2.getTemplate().getId(), 0, 1, tab1.getTemplate().getId(), 0, 0);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ReorderGadgetRequest request = new ReorderGadgetRequest(tab1.getId(), gadgetId, 0, 0);
        final Map<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(tabMapper).findById(tab1.getId());
                will(returnValue(tab1));
            }
        });

        // Perform the action
        Tab resultTab = sut.execute(actionContext);

        // make sure the right tab was returned
        assertSame(tab1, resultTab);

        // verify mock assertions
        context.assertIsSatisfied();

        // loop through tab2 to make sure there's no zone4 in it
        for (Gadget g : tab2.getGadgets())
        {
            assertFalse(g.getZoneNumber() == 2);
        }
    }

    /**
     * Swap two consecutive gadgets in the same zone.
     *
     * Note that I deliberately picked two gadgets at the end of a zone. If we test one before the end, we'll have to
     * setup assertions/allowings for temporary shifts upward due to the two-step nature of the SUT. To avoid this
     * headache, I'm swapping two gadgets at the end of a zone.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public void performActionSwapConsecutiveGadgets() throws Exception
    {
        final long gadgetId = tab1GadgetIds.get(5);
        setupSourceAndDestinationTabs(tab1, tab1, gadgetId);

        assertMapperMoveCall(gadgetId, tab1.getTemplate().getId(), 2, 1, tab1.getTemplate().getId(), 1, 1);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ReorderGadgetRequest request = new ReorderGadgetRequest(tab1.getId(), gadgetId, 1, 1);
        final Map<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(tabMapper).findById(tab1.getId());
                will(returnValue(tab1));
            }
        });

        // Perform the action
        Tab resultTab = sut.execute(actionContext);

        // make sure the right tab was returned
        assertSame(tab1, resultTab);

        // verify mock assertions
        context.assertIsSatisfied();
    }

    /**
     * Swap two consecutive gadgets in the same zone.
     *
     * Note that I deliberately picked two gadgets at the end of a zone. If we test one before the end, we'll have to
     * setup assertions/allowings for temporary shifts upward due to the two-step nature of the SUT. To avoid this
     * headache, I'm swapping two gadgets at the end of a zone.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public void performActionSwapConsecutiveGadgetsWithStateSavedTabs() throws Exception
    {
        final long gadgetId = tab1GadgetIds.get(5);

        assertMapperMoveCall(gadgetId, tab1.getTemplate().getId(), 2, 1, tab1.getTemplate().getId(), 1, 1);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ReorderGadgetRequest request = new ReorderGadgetRequest(tab1.getId(), gadgetId, 1, 1);
        final Map<String, Object> state = new HashMap<String, Object>();
        state.put("sourceTemplate", tab1.getTemplate());
        state.put("destinationTemplate", tab1.getTemplate());

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getState();
                will(returnValue(state));

                oneOf(tabMapper).findById(tab1.getId());
                will(returnValue(tab1));
            }
        });

        // Perform the action
        Tab resultTab = sut.execute(actionContext);

        // make sure the right tab was returned
        assertSame(tab1, resultTab);

        // verify mock assertions
        context.assertIsSatisfied();
    }

    /**
     * Test the failure case where the source template is not found and the mapper throws an exception.
     * @throws Exception on error.
     */
    @Test(expected = ExecutionException.class)
    public void testExecutionFailure() throws Exception
    {
        final long gadgetId = tab1GadgetIds.get(5);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ReorderGadgetRequest request = new ReorderGadgetRequest(tab1.getId(), gadgetId, 1, 1);
        final Map<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getState();
                will(returnValue(state));

                one(tabMapper).findByGadgetId(gadgetId);
                will(throwException(new NoResultException()));
            }
        });

        // Perform the action
        sut.execute(actionContext);

        // verify mock assertions
        context.assertIsSatisfied();
    }

    // --------------------------------------------------------------------------
    // Unit Test Helper Methods
    // --------------------------------------------------------------------------
    /**
     * Test Helper Method: Setup the expectations of the mapper and have it return the expected mock Tabs for source and
     * destination tabs.
     *
     * @param sourceTab
     *            the tab that the gadget is moving from.
     * @param destinationTab
     *            the tab that the gadget is moving to.
     * @param gadgetId
     *            the id of the gadget we're moving.
     */
    private void setupSourceAndDestinationTabs(final Tab sourceTab, final Tab destinationTab, final long gadgetId)
    {
        context.checking(new Expectations()
        {
            {
                one(tabMapper).findByGadgetId(gadgetId);
                will(returnValue(sourceTab.getTemplate()));

                one(tabMapper).findById(destinationTab.getId());
                will(returnValue(destinationTab));
            }
        });
    }

    /**
     * Test Helper Method: Build a mocked Gadget.
     *
     * @param id
     *            the ID of the Gadget.
     * @param zoneNumber
     *            the zoneNumber of the gadget.
     * @param zoneIndex
     *            the zoneIndex of the gadget.
     *
     * @return the mocked Gadget.
     */
    private Gadget getGadget(final long id, final int zoneNumber, final int zoneIndex)
    {
        final Gadget g = context.mock(Gadget.class, "gadget#" + id);
        context.checking(new Expectations()
        {
            {
                States znState = context.states(id + ".zoneNumber").startsAs("initial");
                gadgetStates.put(id + ".zoneNumber", znState);

                States ziState = context.states(id + ".zoneNumber").startsAs("initial");
                gadgetStates.put(id + ".zoneIndex", ziState);

                allowing(g).getId();
                will(returnValue(id));

                allowing(g).getZoneNumber();
                will(returnValue(zoneNumber));
                when(znState.isNot("changed"));

                allowing(g).getZoneIndex();
                will(returnValue(zoneIndex));
                when(ziState.isNot("changed"));

                allowing(g).getGadgetDefinition();
                will(returnValue(new GadgetDefinition()));

                allowing(g).getOwner();
                will(returnValue(new Person()));
            }
        });
        return g;
    }

    /**
     * This method sets up the expectations for the call to the Tab mapper to move the gadget.
     *
     * @param gadgetId
     *            - id of the gadget to move.
     * @param sourceTabTemplateId
     *            - source tab template id.
     * @param sourceZoneIndex
     *            - source zone index.
     * @param sourceZoneNumber
     *            - source zone number.
     * @param targetTabTemplateId
     *            - target tab template id.
     * @param targetZoneIndex
     *            - target zone index.
     * @param targetZoneNumber
     *            - target zone number.
     */
    private void assertMapperMoveCall(final Long gadgetId, final Long sourceTabTemplateId,
            final Integer sourceZoneIndex, final Integer sourceZoneNumber, final Long targetTabTemplateId,
            final Integer targetZoneIndex, final Integer targetZoneNumber)
    {
        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).moveGadget(gadgetId, sourceTabTemplateId, sourceZoneIndex, sourceZoneNumber,
                        targetTabTemplateId, targetZoneIndex, targetZoneNumber);
            }
        });
    }

    // --------------------------------------------------------------------------
    // Unit Tests for Helper Methods
    // --------------------------------------------------------------------------

    /**
     * Unit Test Test: Test that states work like I think.
     */
    @Test
    public void testAssertStates()
    {
        final Gadget g = context.mock(Gadget.class, "foo");

        final States znState = context.states("500.zoneNumber").startsAs("initial");

        context.checking(new Expectations()
        {
            {
                allowing(g).getZoneIndex();
                will(returnValue(1));
                when(znState.isNot("changed"));

                one(g).setZoneIndex(2);
                then(znState.is("changed"));

                allowing(g).getZoneIndex();
                will(returnValue(2));
                when(znState.is("changed"));
            }
        });

        assertEquals(1, g.getZoneIndex());
        assertEquals(1, g.getZoneIndex());
        g.setZoneIndex(2);
        assertEquals(2, g.getZoneIndex());
        assertEquals(2, g.getZoneIndex());

        context.assertIsSatisfied();
    }

    /**
     * Unit Test Test: Test the helper method getGagdet works the way we expect.
     */
    @Test
    public void testGetGadget()
    {
        final long gadgetId = 12345L;
        final int gadgetZone = 123;
        final int gadgetIndex = 321;

        final int newGadgetIndex = 555;

        final Gadget g = getGadget(gadgetId, gadgetZone, gadgetIndex);
        context.checking(new Expectations()
        {
            {
                one(g).setZoneIndex(newGadgetIndex);
                then(gadgetStates.get(gadgetId + ".zoneIndex").is("changed"));

                allowing(g).getZoneIndex();
                will(returnValue(newGadgetIndex));
                when(gadgetStates.get(gadgetId + ".zoneIndex").is("changed"));
            }
        });

        assertEquals(gadgetId, g.getId());
        assertEquals(gadgetIndex, g.getZoneIndex());

        g.setZoneIndex(newGadgetIndex);

        assertEquals(newGadgetIndex, g.getZoneIndex());

        context.assertIsSatisfied();
    }

}
