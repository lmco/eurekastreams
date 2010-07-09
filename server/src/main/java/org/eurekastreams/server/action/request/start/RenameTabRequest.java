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
 * Request for renaming tab.
 * 
 */
public class RenameTabRequest implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 6137314135559121007L;

    /**
     * Tab id.
     */
    private Long tabId;

    /**
     * Tab name.
     */
    private String tabName;

    /**
     * For serialization only.
     */
    @SuppressWarnings("unused")
    private RenameTabRequest()
    {
        // no op.
    }

    /**
     * Constructor.
     * 
     * @param inTabId
     *            Tab id.
     * @param inTabName
     *            Tab name.
     */
    public RenameTabRequest(final Long inTabId, final String inTabName)
    {
        tabId = inTabId;
        tabName = inTabName;
    }

    /**
     * @return the tabId
     */
    public Long getTabId()
    {
        return tabId;
    }

    /**
     * @param inTabId
     *            the tabId to set
     */
    public void setTabId(final Long inTabId)
    {
        tabId = inTabId;
    }

    /**
     * @return the tabName
     */
    public String getTabName()
    {
        return tabName;
    }

    /**
     * @param inTabName
     *            the tabName to set
     */
    public void setTabName(final String inTabName)
    {
        tabName = inTabName;
    }

}
