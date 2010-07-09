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
package org.eurekastreams.server.action.request.start;

import java.io.Serializable;

/**
 * This class handles the request parameters for GadgetUserPref requests.
 *
 */
public class GadgetUserPrefActionRequest implements Serializable
{
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = 8525434895330197652L;

    /**
     * Local instance of the gadget instance id.
     */
    private Long gadgetId;

    /**
     * Local instance of the user preferences.
     */
    private String gadgetUserPref;

    /**
     * Constructor.
     * @param inGadgetId - instance id of the gadget for which user prefs are requested.
     */
    public GadgetUserPrefActionRequest(final Long inGadgetId)
    {
        gadgetId = inGadgetId;
    }

    /**
     * Constructor.
     * @param inGadgetId - instance id of the gadget for which user prefs are requested.
     * @param inGadgetUserPref - JSON representation of user prefs.
     */
    public GadgetUserPrefActionRequest(final Long inGadgetId, final String inGadgetUserPref)
    {
        gadgetId = inGadgetId;
        gadgetUserPref = inGadgetUserPref;
    }

    /**
     * Default constructor for serialization in GWT.
     */
    public GadgetUserPrefActionRequest()
    {
        //Intentionally left blank for GWT serialization purposes.
    }

    /**
     * Getter for the gadget instance id.
     * @return gadget instance id.
     */
    public Long getGadgetId()
    {
        return gadgetId;
    }

    /**
     * Setter for the gadget instance id.
     * @param inGadgetId - gadget instance id.
     */
    public void setGadgetId(final Long inGadgetId)
    {
        gadgetId = inGadgetId;
    }

    /**
     * Getter for the user preferences.
     * @return gadget user preferences for this request.
     */
    public String getGadgetUserPref()
    {
        return gadgetUserPref;
    }

    /**
     * Setter for the user preferences.
     * @param inGadgetUserPref - gadget user preferences for this request.
     */
    public void setGadgetUserPref(final String inGadgetUserPref)
    {
        gadgetUserPref = inGadgetUserPref;
    }
}
