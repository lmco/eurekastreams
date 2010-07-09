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
package org.eurekastreams.web.client.events;

/**
 * Even that gets fired when a gadget's user prefs are updated.
 *
 */
public class UpdateGadgetPrefsEvent
{
    /**
     * Local instance of the id of the gadget being updated.
     */
    private Long id;

    /**
     * Local instance of the user prefs for the gadget being updated.
     */
    private String userPrefs;

    /**
     * Constructor.
     * @param inId - id of the gadget to update the user preferences for.
     * @param inUserPrefs - user preferences that have changed for the specified gadget.
     */
    public UpdateGadgetPrefsEvent(final Long inId, final String inUserPrefs)
    {
        id = inId;
        userPrefs = inUserPrefs;
    }

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param inId the id to set
     */
    public void setId(final Long inId)
    {
        this.id = inId;
    }

    /**
     * @return the userPrefs
     */
    public String getUserPrefs()
    {
        return userPrefs;
    }

    /**
     * @param inUserPrefs the userPrefs to set
     */
    public void setUserPrefs(final String inUserPrefs)
    {
        this.userPrefs = inUserPrefs;
    }
}
