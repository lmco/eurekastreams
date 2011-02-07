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
package org.eurekastreams.web.client.model.requests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.search.modelview.GadgetDTO;
import org.eurekastreams.server.search.modelview.GadgetDefinitionDTO;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.eurekastreams.server.search.modelview.TabDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PersonPagePropertiesDTOToPersonTransformer.
 * 
 */
public class PersonPagePropertiesDTOToPersonTransformerTest
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
    private PersonPagePropertiesDTO ppp = context.mock(PersonPagePropertiesDTO.class);

    /**
     * {@link TabDTO}.
     */
    private TabDTO tab = context.mock(TabDTO.class);

    /**
     * {@link GadgetDTO}.
     */
    private GadgetDTO gadget = context.mock(GadgetDTO.class);

    /**
     * {@link GadgetDefinitionDTO}.
     */
    private GadgetDefinitionDTO gadgetDef = context.mock(GadgetDefinitionDTO.class);

    /**
     * {@link Theme}.
     */
    private Theme theme = context.mock(Theme.class);

    /**
     * System under test.
     */
    private PersonPagePropertiesDTOToPersonTransformer sut = new PersonPagePropertiesDTOToPersonTransformer();

    /**
     * Test.
     */
    @Test
    public void testNullTheme()
    {
        final List<TabDTO> tabs = new ArrayList<TabDTO>(Collections.singletonList(tab));
        final List<GadgetDTO> gadgets = new ArrayList<GadgetDTO>(Collections.singletonList(gadget));

        context.checking(new Expectations()
        {
            {
                oneOf(ppp).getThemeCssFile();
                will(returnValue(null));

                oneOf(ppp).getTabDTOs();
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

                oneOf(gadget).getZoneIndex();
                will(returnValue(1));

                oneOf(gadget).getZoneNumber();
                will(returnValue(1));

                oneOf(gadget).getGadgetUserPref();
                will(returnValue("userPref"));

                allowing(gadget).getGadgetDefinition();
                will(returnValue(gadgetDef));

                oneOf(gadgetDef).getId();
                will(returnValue(3L));

                oneOf(gadgetDef).getUrl();
                will(returnValue("URL"));

                oneOf(gadgetDef).getUuid();
                will(returnValue("UUID"));
            }
        });

        Person p = sut.transform(ppp);

        assertNull(p.getTheme());
        assertEquals(1, p.getStartTabGroup().getTabs().size());
        assertEquals(1, p.getStartTabGroup().getTabs().get(0).getGadgets().size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNonNullTheme()
    {
        final List<TabDTO> tabs = new ArrayList<TabDTO>(Collections.singletonList(tab));
        final List<GadgetDTO> gadgets = new ArrayList<GadgetDTO>(Collections.singletonList(gadget));

        context.checking(new Expectations()
        {
            {
                allowing(ppp).getThemeCssFile();
                will(returnValue("/ThemeCssFile"));

                oneOf(ppp).getTabDTOs();
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

                oneOf(gadget).getZoneIndex();
                will(returnValue(1));

                oneOf(gadget).getZoneNumber();
                will(returnValue(1));

                oneOf(gadget).getGadgetUserPref();
                will(returnValue("userPref"));

                allowing(gadget).getGadgetDefinition();
                will(returnValue(gadgetDef));

                oneOf(gadgetDef).getId();
                will(returnValue(3L));

                oneOf(gadgetDef).getUrl();
                will(returnValue("URL"));

                oneOf(gadgetDef).getUuid();
                will(returnValue("UUID"));
            }
        });

        Person p = sut.transform(ppp);

        assertEquals(p.getTheme().getCssFile(), "/ThemeCssFile");
        assertEquals(1, p.getStartTabGroup().getTabs().size());
        assertEquals(1, p.getStartTabGroup().getTabs().get(0).getGadgets().size());

        context.assertIsSatisfied();
    }

}
