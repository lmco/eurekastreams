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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;

/**
 * Gadget DTO.
 * 
 */
public class GadgetDTO implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 6965272550978087278L;

    /**
     * Gadget id.
     */
    private long id;

    /**
     * This field will maintain a link to the corresponding gadget definition for this gadget instance.
     */
    private GadgetDefinitionDTO gadgetDefinition;

    /**
     * This field contains the user preferences for this gadget instance.
     */
    private String gadgetUserPref;

    /**
     * The zone number describes which zone the gadget is to be displayed in.
     */
    private int zoneNumber;

    /**
     * The minimized bits tracks whether to display the gadget as minimized or normal.
     */
    private boolean minimized = false;

    /**
     * The maximized bits tracks whether to display the gadget as maximized or normal.
     */
    private Boolean maximized = false;

    /**
     * The zone index describes what order in the zone the gadget will be displayed in.
     */
    private int zoneIndex;

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * @return the gadgetDefinition
     */
    public GadgetDefinitionDTO getGadgetDefinition()
    {
        return gadgetDefinition;
    }

    /**
     * @param inGadgetDefinition
     *            the gadgetDefinition to set
     */
    public void setGadgetDefinition(final GadgetDefinitionDTO inGadgetDefinition)
    {
        gadgetDefinition = inGadgetDefinition;
    }

    /**
     * @return the gadgetUserPref
     */
    public String getGadgetUserPref()
    {
        return gadgetUserPref;
    }

    /**
     * @param inGadgetUserPref
     *            the gadgetUserPref to set
     */
    public void setGadgetUserPref(final String inGadgetUserPref)
    {
        gadgetUserPref = inGadgetUserPref;
    }

    /**
     * @return the zoneNumber
     */
    public int getZoneNumber()
    {
        return zoneNumber;
    }

    /**
     * @param inZoneNumber
     *            the zoneNumber to set
     */
    public void setZoneNumber(final int inZoneNumber)
    {
        zoneNumber = inZoneNumber;
    }

    /**
     * @return the minimized
     */
    public boolean isMinimized()
    {
        return minimized;
    }

    /**
     * @param inMinimized
     *            the minimized to set
     */
    public void setMinimized(final boolean inMinimized)
    {
        minimized = inMinimized;
    }

    /**
     * @return the maximized
     */
    public Boolean isMaximized()
    {
        return maximized;
    }

    /**
     * @param inMaximized
     *            the maximized to set
     */
    public void setMaximized(final Boolean inMaximized)
    {
        maximized = inMaximized;
    }

    /**
     * @return the zoneIndex
     */
    public int getZoneIndex()
    {
        return zoneIndex;
    }

    /**
     * @param inZoneIndex
     *            the zoneIndex to set
     */
    public void setZoneIndex(final int inZoneIndex)
    {
        zoneIndex = inZoneIndex;
    }
}
