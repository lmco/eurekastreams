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
package org.eurekastreams.server.persistence.mappers.requests.opensocial;

/**
 * Request object for Gadget User Pref mapper calls.
 *
 */
public class GadgetUserPrefRequest
{
    /**
     * Local instance of the gadget id.
     */
    private Long gadgetId;
    
    /**
     * Local instance of the String based JSON user Prefs.
     */
    private String userPrefs;
    
    /**
     * Constructor for the GadgetUserPrefRequest object.
     * @param inGadgetId - id of the gadget to request user preferences from the mapper for.
     * @param inUserPrefs - string based JSON representation of the User Preferences.
     */
    public GadgetUserPrefRequest(final Long inGadgetId, final String inUserPrefs)
    {
        gadgetId = inGadgetId;
        userPrefs = inUserPrefs;        
    }
    
    /**
     * Getter for the gadget id.
     * @return - the current gadget id of the request object.
     */
    public Long getGadgetId()
    {
        return gadgetId;
    }
    
    /**
     * Setter for the gadget id.
     * @param inGadgetId - the gadget id to use for the request.
     */
    public void setGadgetId(final Long inGadgetId)
    {
        gadgetId = inGadgetId;
    }
    
    /**
     * Getter for the user prefs.
     * @return - string based JSON representation of the user prefs to be used in this request.
     */
    public String getUserPrefs()
    {
        return userPrefs;
    }
    
    /**
     * Setter for the user prefs.
     * @param inUserPrefs - string based JSON representaiton of the user prefs to be used in this request.
     */
    public void setUserPrefs(final String inUserPrefs)
    {
        userPrefs = inUserPrefs;
    }
}
