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

import java.io.Serializable;

/**
 * Add a gadget to the start page request. Does not go to server.
 *
 */
public class AddGadgetToStartPageRequest implements Serializable
{
    /**
     * Prefs.
     */
    private String prefs = null;
    /**
     * Url.
     */
    private String url;
    /**
     * Tab id.
     */
    private Long tabId;

    /**
     * Constructor without prefs.
     *
     * @param inUrl
     *            gadget url.
     * @param inTabId
     *            tab id to add to.
     */
    public AddGadgetToStartPageRequest(final String inUrl, final Long inTabId)
    {
        url = inUrl;
        tabId = inTabId;
    }

    /**
     * Constructor with prefs.
     *
     * @param inUrl
     *            gadget url.
     * @param inTabId
     *            tab id to add to.
     * @param inPrefs
     *            prefs.
     */
    public AddGadgetToStartPageRequest(final String inUrl, final Long inTabId, final String inPrefs)
    {
        url = inUrl;
        tabId = inTabId;
        prefs = inPrefs;
    }

    /**
     * Get the prefs.
     *
     * @return prefs.
     */
    public String getPrefs()
    {
        return prefs;
    }

    /**
     * Get the url.
     *
     * @return the url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Get the tab id.
     *
     * @return the tab id.
     */
    public Long getTabId()
    {
        return tabId;
    }
}
