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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.search.modelview.GadgetDTO;
import org.eurekastreams.server.search.modelview.GadgetDefinitionDTO;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.eurekastreams.server.search.modelview.TabDTO;

/**
 * Transformer to convert Person to PersonPagePropertiesDTO.
 * 
 */
public class PersonToPersonPagePropertiesTransformer implements Transformer<Person, PersonPagePropertiesDTO>
{

    /**
     * Transform Person to PersonPagePropertiesDTO.
     * 
     * @param inPerson
     *            Person to transform.
     * @return {@link PersonPagePropertiesDTO}.
     */
    @Override
    public PersonPagePropertiesDTO transform(final Person inPerson)
    {
        long start = System.currentTimeMillis();
        List<TabDTO> tabDtos = new ArrayList<TabDTO>();
        List<Tab> tabs = inPerson.getTabs(TabGroupType.START);
        for (Tab tab : tabs)
        {
            TabDTO tdto = new TabDTO();
            tdto.setId(tab.getId());
            tdto.setTabIndex(tab.getTabIndex());
            tdto.setTabLayout(tab.getTabLayout());
            tdto.setTabName(tab.getTabName());

            List<GadgetDTO> gadgetDtos = new ArrayList<GadgetDTO>();
            for (Gadget gadget : tab.getGadgets())
            {
                // create gadget dto from gadget.
                GadgetDTO gdto = new GadgetDTO();
                gdto.setId(gadget.getId());
                gdto.setMaximized(gadget.isMaximized());
                gdto.setMinimized(gadget.isMinimized());
                gdto.setZoneIndex(gadget.getZoneIndex());
                gdto.setZoneNumber(gadget.getZoneNumber());
                gdto.setGadgetUserPref(gadget.getGadgetUserPref());

                // create gadget definintion dto.
                GadgetDefinition gadgetDef = gadget.getGadgetDefinition();
                GadgetDefinitionDTO gadgetDefDto = new GadgetDefinitionDTO();
                gadgetDefDto.setId(gadgetDef.getId());
                gadgetDefDto.setUrl(gadgetDef.getUrl());
                gadgetDefDto.setUuid(gadgetDef.getUUID());

                // set gadget def dto in gadget dto.
                gdto.setGadgetDefinition(gadgetDefDto);

                // add to collection.
                gadgetDtos.add(gdto);
            }

            // set gadgets for tab.
            tdto.setGadgets(gadgetDtos);

            // add tab to list of tabs.
            tabDtos.add(tdto);
        }

        PersonPagePropertiesDTO ppp = new PersonPagePropertiesDTO();
        ppp.setTabDTOs(tabDtos);
        ppp.setThemeCssFile(inPerson.getTheme() == null ? null : inPerson.getTheme().getCssFile());
        return ppp;
    }

}
