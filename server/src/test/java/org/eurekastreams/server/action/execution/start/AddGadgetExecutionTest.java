/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.start.AddGadgetRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the AddGadgetAction.
 */
public class AddGadgetExecutionTest
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
     * The mock TabMapper to be used by the action.
     */
    private TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * The mock TabMapper to be used by the action.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * The mock GadgetDefinitionMapper to be used by the action.
     */
    private GadgetDefinitionMapper gadgetDefinitionMapper = context.mock(GadgetDefinitionMapper.class);

    /**
     * Mocked tab who will get the new gadget.
     */
    private Tab tab = context.mock(Tab.class);

    /**
     * Mocked GadgetDefinition.
     */
    private GadgetDefinition gadgetDefinition = context.mock(GadgetDefinition.class);

    /**
     * Subject under test.
     */
    private AddGadgetExecution sut = null;

    /**
     * The mock user information/parameters from the session.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mock.
     */
    private final AddGadgetRequest gadgetRequest = context.mock(AddGadgetRequest.class);

    /**
     * A gadget definition to use as a parameter.
     */
    private static final String GADGET_DEF_URL = "http://someurl.com/gadgetDefinition.xml";

    /**
     * Mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     *
     */
    @Before
    public final void setup()
    {
        sut = new AddGadgetExecution(tabMapper, personMapper, gadgetDefinitionMapper);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testExecuteValid() throws Exception
    {
        final Long tabId = 100L;
        final Long gadgetDefinitionId = 9928L;
        final Layout tabLayout = Layout.THREECOLUMNLEFTWIDEHEADER;

        final List<Gadget> gadgets = new ArrayList<Gadget>();

        // Set up expectations
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(gadgetRequest));

                allowing(gadgetRequest).getTabId();
                will(returnValue(tabId));

                allowing(gadgetRequest).getGadgetDefinitionUrl();
                will(returnValue(GADGET_DEF_URL));

                // action has to load the tab by ID
                allowing(tabMapper).findById(tabId);
                will(returnValue(tab));

                // then find-or-create the gadget definition
                allowing(gadgetDefinitionMapper).findByUrl(GADGET_DEF_URL);
                will(returnValue(gadgetDefinition));

                // gadget definition ID is 9928
                allowing(gadgetDefinition).getId();
                will(returnValue(gadgetDefinitionId));

                // and check what the last zone number is
                allowing(tab).getTabLayout();
                will(returnValue(tabLayout));

                // setup the list of mocked gadgets

                // ----------------
                // -- Gadget #0 - zone 0, index 0
                Gadget gadget0 = context.mock(Gadget.class, "gadget0");

                // set the zone number - it MUST be requested once
                exactly(1).of(gadget0).getZoneNumber();
                will(returnValue(0));

                // should never have to ask this gadget for its zone index
                never(gadget0).getZoneIndex();
                // ----------------

                // ----------------
                // -- Gadget #1 - <LAST ZONE>, index 0
                Gadget gadget1 = context.mock(Gadget.class, "gadget1");
                exactly(1).of(gadget1).getZoneNumber();
                will(returnValue(tabLayout.getNumberOfZones() - 1));

                // ----------------
                // -- Gadget #2 - <LAST ZONE>, index 1
                Gadget gadget2 = context.mock(Gadget.class, "gadget2");

                // set the zone number - it MUST be requested once
                exactly(1).of(gadget2).getZoneNumber();
                will(returnValue(tabLayout.getNumberOfZones() - 1));

                // add the gadgets to the collection
                gadgets.add(gadget0);
                gadgets.add(gadget1);
                gadgets.add(gadget2);

                oneOf(gadget0).getZoneIndex();
                will(returnValue(0));

                oneOf(gadget0).setZoneIndex(1);

                // when asked for the gadgets, return the mocked list
                atLeast(1).of(tab).getGadgets();
                will(returnValue(gadgets));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue("username"));

                // action has to load the tab by ID
                allowing(personMapper).findByAccountId("username");
                will(returnValue(new Person()));

                // will need a flush() when all done
                exactly(1).of(tabMapper).flush();
            }

        });

        Gadget actual = null;
        try
        {
            actual = (Gadget) sut.execute(actionContext);
        }
        catch (Exception e)
        {
            fail("Caught an exception");
        }

        // make sure the new gadget is of the type passed in
        assertEquals(gadgetDefinitionId, (Long) actual.getGadgetDefinition().getId());

        // make sure the gadget is in the right zone and index
        assertEquals(0, actual.getZoneIndex());
        assertEquals(0, actual.getZoneNumber());

        context.assertIsSatisfied();
    }

    /**
     * Test that the method throws the correct exception when given bad data.
     * 
     * @throws Exception
     *             thrown as replacement for NoResultException.
     */
    @Test(expected = Exception.class)
    public void performActionWithInvalidTabId() throws Exception
    {
        final Long tabId = new Long(1000000000);

        // Set up expectations
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(gadgetRequest));

                allowing(gadgetRequest).getTabId();
                will(returnValue(tabId));

                allowing(gadgetRequest).getGadgetDefinitionUrl();
                will(returnValue(GADGET_DEF_URL));

                oneOf(tabMapper).findById(tabId);
                will(throwException(new NoResultException("no such tab")));
            }
        });

        sut.execute(actionContext);
    }
}
