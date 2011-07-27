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
 * Parameters for AddGadgetExecution.
 */
public class AddGadgetRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 5892599544872492014L;

    /**
     * Tab id.
     */
    private Long tabId;

    /**
     * Gadget definition URL.
     */
    private String gadgetDefinitionUrl;

    /**
     * Gadget user prefs.
     */
    private String userPrefs;

    /**
     * For serialization only.
     */
    @SuppressWarnings("unused")
    private AddGadgetRequest()
    {
        // no op.
    }

    /**
     * Constructor.
     * 
     * @param inTabId
     *            Tab id.
     * @param inGadgetDefinitionUrl
     *            Tab name.
     */
    public AddGadgetRequest(final Long inTabId, final String inGadgetDefinitionUrl)
    {
        this(inTabId, inGadgetDefinitionUrl, null);
    }

    /**
     * Constructor.
     * 
     * @param inTabId
     *            Tab id.
     * @param inGadgetDefinitionUrl
     *            Tab name.
     * @param inUserPrefs
     *            user prefs.
     */
    public AddGadgetRequest(final Long inTabId, final String inGadgetDefinitionUrl, final String inUserPrefs)
    {
        this.tabId = inTabId;
        this.gadgetDefinitionUrl = inGadgetDefinitionUrl;
        this.userPrefs = inUserPrefs;
    }

    /**
     * @param inTabId
     *            the tabId to set.
     */
    public void setTabId(final Long inTabId)
    {
        this.tabId = inTabId;
    }

    /**
     * @return the tabId.
     */
    public Long getTabId()
    {
        return tabId;
    }

    /**
     * @param inGadgetDefinitionUrl
     *            the gadgetDefinitionUrl to set.
     */
    public void setGadgetDefinitionUrl(final String inGadgetDefinitionUrl)
    {
        this.gadgetDefinitionUrl = inGadgetDefinitionUrl;
    }

    /**
     * @return the gadgetDefinitionUrl.
     */
    public String getGadgetDefinitionUrl()
    {
        return gadgetDefinitionUrl;
    }

    /**
     * @return the userPrefs
     */
    public String getUserPrefs()
    {
        return userPrefs;
    }

    /**
     * @param inUserPrefs
     *            the userPrefs to set
     */
    public void setUserPrefs(final String inUserPrefs)
    {
        userPrefs = inUserPrefs;
    }
}
