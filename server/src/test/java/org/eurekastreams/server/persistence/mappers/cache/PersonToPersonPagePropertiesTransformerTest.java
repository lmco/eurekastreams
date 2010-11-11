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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PersonToPersonPagePropertiesTransformer.
 * 
 */
public class PersonToPersonPagePropertiesTransformerTest
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
     * mocked tab for testing results.
     */
    private Person person = context.mock(Person.class);

    /**
     * {@link Tab}.
     */
    private Tab tab = context.mock(Tab.class);

    /**
     * {@link Gadget}.
     */
    private Gadget gadget = context.mock(Gadget.class);

    /**
     * {@link GadgetDefinition}.
     */
    private GadgetDefinition gadgetDef = context.mock(GadgetDefinition.class);

    /**
     * {@link Theme}.
     */
    private Theme theme = context.mock(Theme.class);

    /**
     * System under test.
     */
    private PersonToPersonPagePropertiesTransformer sut = new PersonToPersonPagePropertiesTransformer();

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final List<Tab> tabs = new ArrayList<Tab>(Collections.singletonList(tab));
        final List<Gadget> gadgets = new ArrayList<Gadget>(Collections.singletonList(gadget));

        context.checking(new Expectations()
        {
            {
                oneOf(person).getTabs(TabGroupType.START);
                will(returnValue(tabs));

                oneOf(tab).getId();
                will(returnValue(5L));

                oneOf(tab).getTabIndex();
                will(returnValue(1));

                oneOf(tab).getTabLayout();
                will(returnValue(Layout.ONECOLUMN));

                oneOf(tab).getTabName();
                will(returnValue("tabname"));

                oneOf(tab).getGadgets();
                will(returnValue(gadgets));

                oneOf(gadget).getId();
                will(returnValue(4L));

                oneOf(gadget).isMaximized();
                will(returnValue(false));

                oneOf(gadget).isMinimized();
                will(returnValue(true));

                oneOf(gadget).getZoneIndex();
                will(returnValue(1));

                oneOf(gadget).getZoneNumber();
                will(returnValue(1));

                oneOf(gadget).getGadgetUserPref();
                will(returnValue("userPref"));

                oneOf(gadget).getGadgetDefinition();
                will(returnValue(gadgetDef));

                oneOf(gadgetDef).getId();
                will(returnValue(3L));

                oneOf(gadgetDef).getUrl();
                will(returnValue("URL"));

                oneOf(gadgetDef).getUUID();
                will(returnValue("UUID"));

                allowing(person).getTheme();
                will(returnValue(theme));

                oneOf(theme).getCssFile();
                will(returnValue("CSSfileURL"));

            }
        });

        PersonPagePropertiesDTO ppp = sut.transform(person);

        assertEquals(5L, ppp.getTabDTOs().get(0).getId());
        assertEquals(1, ppp.getTabDTOs().get(0).getTabIndex());
        assertEquals(Layout.ONECOLUMN, ppp.getTabDTOs().get(0).getTabLayout());
        assertEquals("tabname", ppp.getTabDTOs().get(0).getTabName());
        assertEquals(4L, ppp.getTabDTOs().get(0).getGadgets().get(0).getId());
        assertEquals(false, ppp.getTabDTOs().get(0).getGadgets().get(0).isMaximized());
        assertEquals(true, ppp.getTabDTOs().get(0).getGadgets().get(0).isMinimized());
        assertEquals(1, ppp.getTabDTOs().get(0).getGadgets().get(0).getZoneIndex());
        assertEquals(1, ppp.getTabDTOs().get(0).getGadgets().get(0).getZoneNumber());
        assertEquals("userPref", ppp.getTabDTOs().get(0).getGadgets().get(0).getGadgetUserPref());
        assertEquals(3L, ppp.getTabDTOs().get(0).getGadgets().get(0).getGadgetDefinition().getId());
        assertEquals("URL", ppp.getTabDTOs().get(0).getGadgets().get(0).getGadgetDefinition().getUrl());
        assertEquals("UUID", ppp.getTabDTOs().get(0).getGadgets().get(0).getGadgetDefinition().getUuid());

        context.assertIsSatisfied();
    }
}
