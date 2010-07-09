/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import java.io.Serializable;

/**
 * This class represents a Gadget User Preferences instance.
 *
 */
@SuppressWarnings("serial")
public class GadgetUserPrefDTO implements Serializable
{
    /**
     * Id of the Gadget Instance.
     */
    private Long gadgetInstanceId;
    
    /**
     * JSON Representation of the user preferences.
     */
    private String jsonUserPref = "";
    
    /**
     * Default public constructor for hibernate.
     */
    public GadgetUserPrefDTO()
    {
        //intententially left blank.
    }
    
    /**
     * GadgetUserPref constructor that sets the Gadget instance and json user pref.
     * @param inGadgetInstanceId - Id of the gadget to initialize the GadgetUserPref with.
     * @param inJsonUserPref - json representation of the user preferences to intialize with.
     */
    public GadgetUserPrefDTO(final Long inGadgetInstanceId, final String inJsonUserPref)
    {
        gadgetInstanceId = inGadgetInstanceId;
        jsonUserPref = inJsonUserPref;
    }
    
    /**
     * Getter for the Gadget instance.
     * @return - gadget instance.
     */
    public Long getGadgetInstanceId()
    {
        return gadgetInstanceId;
    }
    
    /**
     * Setter for the Gadget instance.
     * @param inGadgetInstanceId - gadget instance.
     */
    public void setGadgetInstanceId(final Long inGadgetInstanceId)
    {
        gadgetInstanceId = inGadgetInstanceId;
    }
    
    /**
     * Getter for the Json User Pref.
     * @return - json user prefs.
     */
    public String getJsonUserPref()
    {
        return jsonUserPref;
    }
    
    /**
     * Setter for the Json User Pref.
     * @param inJsonUserPref - json user prefs.
     */
    public void setJsonUserPref(final String inJsonUserPref)
    {
        jsonUserPref = inJsonUserPref;
    }
}
