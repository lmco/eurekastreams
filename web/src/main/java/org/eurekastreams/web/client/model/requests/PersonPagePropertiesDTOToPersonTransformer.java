/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.search.modelview.GadgetDTO;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.eurekastreams.server.search.modelview.TabDTO;

/**
 * DTO to Person transformer. Temporary solution until we refactor.
 *
 */
public class PersonPagePropertiesDTOToPersonTransformer
{
    /**
     * Transform a PersonPagePropertiesDTO to a Person object.
     *
     * @param dto
     *            the dto.
     * @return the person.
     */
    public Person transform(final PersonPagePropertiesDTO dto)
    {
        Person person = new Person();
        if (dto.getThemeCssFile() == null)
        {
            person.setTheme(null);
        }
        else
        {
            person.setTheme(new Theme("", "", "", dto.getThemeCssFile(), "", "", "", ""));
        }

        TabGroup startTabGroup = new TabGroup();

        for (TabDTO tabDTO : dto.getTabDTOs())
        {
            Tab tab = new Tab(tabDTO.getTabName(), tabDTO.getTabLayout(), tabDTO.getId());
            tab.setTabIndex(tabDTO.getTabIndex());
            List<Gadget> gadgets = new ArrayList<Gadget>();

            for (GadgetDTO gadgetDTO : tabDTO.getGadgets())
            {
                GadgetDefinition gadgetDef = new GadgetDefinition(gadgetDTO.getGadgetDefinition().getUrl(), gadgetDTO
                        .getGadgetDefinition().getUuid());
                gadgetDef.setId(gadgetDTO.getGadgetDefinition().getId());
                Gadget gadget = new Gadget(gadgetDef, gadgetDTO.getZoneNumber(), gadgetDTO.getZoneIndex(), null,
                        gadgetDTO.getGadgetUserPref());
                gadget.setId(gadgetDTO.getId());
                gadget.setMaximized(gadgetDTO.isMaximized());
                gadget.setMinimized(gadgetDTO.isMinimized());
                gadgets.add(gadget);
            }

            tab.setGadgets(gadgets);
            startTabGroup.addTab(tab);
        }

        person.setStartTabGroup(startTabGroup);

        return person;
    }
}
