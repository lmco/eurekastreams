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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.start.SetTabOrderRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link SetTabOrderExecution} class.
 *
 */
public class SetTabOrderExecutionTest
{
    /**
     * System under test.
     */
    private SetTabOrderExecution sut;

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
     * Mapper used to retrieve the page whose tabs are getting moved.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Mocked instance of the Principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Collection of mocked tabs that the action will work on.
     */
    private List<Tab> tabs = null;

    /**
     * A tab id to use for testing. Arbitrary.
     */
    private static final Long TAB_ID = 65L;

    /**
     * Id of the first tab in the page.
     */
    private static final Long TAB0_ID = 0L;

    /**
     * Id of the second tab in the page.
     */
    private static final Long TAB1_ID = 10L;

    /**
     * Id of the third tab in the page.
     */
    private static final Long TAB2_ID = 20L;

    /**
     * Id of the fourth tab in the page.
     */
    private static final Long TAB3_ID = 30L;

    /**
     * Id of the fifth tab in the page.
     */
    private static final Long TAB4_ID = 40L;

    /**
     * Id of a tab that does not exist in the db.
     */
    private static final Long NO_SUCH_TAB_ID = 999L;

    /**
     * A new index to use for testing. Arbitrary.
     */
    private static final Integer NEW_INDEX = 5;

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new SetTabOrderExecution(personMapper);
        tabs = buildTabs();
    }

    /**
     * Test case that covers moving the first tab and moving later in the list.
     *
     * @throws Exception
     *             should not be thrown
     */
    @Test
    public void performActionMoveTabFromFirstToLater() throws Exception
    {
        final Long tabId = TAB0_ID;
        final Integer newIndex = 3;

        setupExpectations();

        SetTabOrderRequest currentRequest = new SetTabOrderRequest(TabGroupType.START, tabId, newIndex);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);

        assertPositions(TAB1_ID, TAB2_ID, TAB3_ID, TAB0_ID, TAB4_ID);

        context.assertIsSatisfied();
    }

    /**
     * Test case that covers moving the last tab and moving earlier in the list.
     *
     * @throws Exception
     *             should not be thrown
     */
    @Test
    public void performActionMoveTabFromLastToEarlier() throws Exception
    {
        final Long tabId = TAB4_ID;
        final Integer newIndex = 2;

        setupExpectations();

        SetTabOrderRequest currentRequest = new SetTabOrderRequest(TabGroupType.START, tabId, newIndex);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);

        assertPositions(TAB0_ID, TAB1_ID, TAB4_ID, TAB2_ID, TAB3_ID);

        context.assertIsSatisfied();
    }

    /**
     * Test case that covers moving a tab in the middle and putting a tab at the end.
     *
     * @throws Exception
     *             should not be thrown
     */
    @Test
    public void performActionMoveTabToEnd() throws Exception
    {
        final Long tabId = TAB2_ID;
        final Integer newIndex = 4;

        setupExpectations();

        SetTabOrderRequest currentRequest = new SetTabOrderRequest(TabGroupType.START, tabId, newIndex);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);

        assertPositions(TAB0_ID, TAB1_ID, TAB3_ID, TAB4_ID, TAB2_ID);

        context.assertIsSatisfied();
    }

    /**
     * Test case that covers moving a tab to the first spot and moving earlier from the middle.
     *
     * @throws Exception
     *             should not be thrown
     */
    @Test
    public void performActionMoveTabToFirst() throws Exception
    {
        final Long tabId = TAB2_ID;
        final Integer newIndex = 0;

        setupExpectations();

        SetTabOrderRequest currentRequest = new SetTabOrderRequest(TabGroupType.START, tabId, newIndex);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);

        assertPositions(TAB2_ID, TAB0_ID, TAB1_ID, TAB3_ID, TAB4_ID);

        context.assertIsSatisfied();
    }

    /**
     * Attempting to reorder a non-existent tab should trigger an exception.
     *
     * @throws Exception
     *             expected
     */
    @Test(expected = Exception.class)
    public void performActionWithBadTabId() throws Exception
    {
        final Long tabId = NO_SUCH_TAB_ID;
        final Integer newIndex = 0;

        setupExpectations();

        SetTabOrderRequest currentRequest = new SetTabOrderRequest(TabGroupType.START, tabId, newIndex);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Do common setup for all performAction tests.
     */
    private void setupExpectations()
    {
        final Person person = context.mock(Person.class);
        final String username = "testuser";
        context.checking(new Expectations()
        {
            {
                atLeast(1).of(principalMock).getAccountId();
                will(returnValue(username));

                oneOf(personMapper).findByAccountId(username);
                will(returnValue(person));

                oneOf(person).getTabs(TabGroupType.START);
                will(returnValue(tabs));

                oneOf(personMapper).flush();
            }
        });
    }

    /**
     * Make sure that all tabs ended up in the right places.
     *
     * @param id0
     *            id that should be in the 1st location
     * @param id1
     *            id that should be in the 2nd location
     * @param id2
     *            id that should be in the 3rd location
     * @param id3
     *            id that should be in the 4th location
     * @param id4
     *            id that should be in the 5th location
     */
    private void assertPositions(final long id0, final long id1, final long id2, final long id3, final long id4)
    {
        assertEquals("Tab 0 has wrong id", id0, tabs.get(0).getId());
        assertEquals("Tab 1 has wrong id", id1, tabs.get(1).getId());
        assertEquals("Tab 2 has wrong id", id2, tabs.get(2).getId());
        assertEquals("Tab 3 has wrong id", id3, tabs.get(3).getId());
        assertEquals("Tab 4 has wrong id", id4, tabs.get(4).getId());
    }

    /**
     * A collection of mocked tabs that will be used by the performAction tests.
     *
     * @return collection of mocked tabs
     */
    private List<Tab> buildTabs()
    {
        final List<Tab> tabList = new ArrayList<Tab>();

        context.checking(new Expectations()
        {
            {
                tabList.add(setupTab(TAB0_ID));
                tabList.add(setupTab(TAB1_ID));
                tabList.add(setupTab(TAB2_ID));
                tabList.add(setupTab(TAB3_ID));
                tabList.add(setupTab(TAB4_ID));
            }

            private Tab setupTab(final long index)
            {
                Tab tab = context.mock(Tab.class, "tab" + index);

                allowing(tab).getId();
                will(returnValue(index));

                return tab;
            }
        });

        return tabList;
    }
}
