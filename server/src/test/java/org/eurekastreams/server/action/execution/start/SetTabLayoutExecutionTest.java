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

import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.start.SetTabLayoutRequest;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.UpdateGadgetsWithNewTabLayoutMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateGadgetsWithNewTabLayoutRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetTabLayoutExecution} class.
 * 
 */
public class SetTabLayoutExecutionTest
{
    /**
     * System under test.
     */
    private SetTabLayoutExecution sut;

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
     * A mock strategy for updating gadget zone indexes.
     */
    private UpdateGadgetsWithNewTabLayoutMapper updateMapper = context.mock(UpdateGadgetsWithNewTabLayoutMapper.class);

    /**
     * The mock mapper the action will use to record the new ordering.
     */
    private TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * The mock tab returned by the tabMapper.
     */
    private Tab tab = context.mock(Tab.class);

    /**
     * The mocked instance of the Principal class.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * A tab id for the action to look up. The value doesn't matter.
     */
    private static final Long TAB_ID = Long.valueOf(37);

    /**
     * {@link DomainMapper}.
     */
    private DomainMapper<Set<String>, Boolean> deleteCacheKeysMapper = context.mock(DomainMapper.class);

    /**
     * Create the sut and gadget list.
     */
    @Before
    public final void setup()
    {
        sut = new SetTabLayoutExecution(tabMapper, updateMapper, deleteCacheKeysMapper);
    }

    /**
     * Make sure that ChangeLayout correctly passes its gadgets to the grow strategy.
     * 
     * @throws Exception
     *             performAction can throw an exception
     */
    @Test
    public final void testExecuteGrow() throws Exception
    {
        final Layout newLayout = Layout.THREECOLUMNLEFTWIDEHEADER;

        // set up expectations
        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findById(with(TAB_ID));
                will(returnValue(tab));

                oneOf(tab).getTabLayout();
                will(returnValue(Layout.THREECOLUMN));

                oneOf(tab).setTabLayout(newLayout);

                allowing(principalMock).getId();
                will(returnValue(1L));

                allowing(deleteCacheKeysMapper).execute(with(any(Set.class)));

                oneOf(tabMapper).flush();

                oneOf(tabMapper).findById(with(TAB_ID));
                will(returnValue(tab));
            }
        });

        SetTabLayoutRequest currentRequest = new SetTabLayoutRequest(newLayout, TAB_ID);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Make sure that ChangeLayout correctly passes its gadgets to the shrink strategy.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testExecuteShrink() throws Exception
    {
        final Layout newLayout = Layout.TWOCOLUMN;

        // set up expectations
        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findById(with(TAB_ID));
                will(returnValue(tab));

                oneOf(tab).getTabLayout();
                will(returnValue(Layout.THREECOLUMN));

                oneOf(tab).setTabLayout(newLayout);

                allowing(principalMock).getId();
                will(returnValue(1L));

                allowing(deleteCacheKeysMapper).execute(with(any(Set.class)));

                oneOf(tabMapper).flush();

                oneOf(updateMapper).execute(with(any(UpdateGadgetsWithNewTabLayoutRequest.class)));

                oneOf(tab).getTemplate();

                oneOf(tabMapper).findById(with(TAB_ID));
                will(returnValue(tab));
            }
        });

        SetTabLayoutRequest currentRequest = new SetTabLayoutRequest(newLayout, TAB_ID);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Make sure that no changes are made if the zone count stays the same.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionSameZoneCount() throws Exception
    {
        final Layout newLayout = Layout.THREECOLUMN;

        // set up expectations
        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findById(with(TAB_ID));
                will(returnValue(tab));

                oneOf(tab).getTabLayout();
                will(returnValue(Layout.THREECOLUMN));

                oneOf(tab).setTabLayout(newLayout);

                allowing(principalMock).getId();
                will(returnValue(1L));

                allowing(deleteCacheKeysMapper).execute(with(any(Set.class)));

                oneOf(tabMapper).flush();

                oneOf(tabMapper).findById(with(TAB_ID));
                will(returnValue(tab));
            }
        });

        SetTabLayoutRequest currentRequest = new SetTabLayoutRequest(newLayout, TAB_ID);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}
