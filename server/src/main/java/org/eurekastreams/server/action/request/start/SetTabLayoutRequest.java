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

import org.eurekastreams.server.domain.Layout;

/**
 * Request class for the SetTabLayout action.  Contains the Layout and Tab id for the
 * action.
 *
 */
public class SetTabLayoutRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -5034260972060095716L;

    /**
     * Local instance of the layout enum for this request.
     */
    private Layout layout;

    /**
     * Local instance of the tab id for this request.
     */
    private Long tabId;

    /**
     * Default constructor.
     */
    private SetTabLayoutRequest()
    {
        //default constructor for EJB requirements.
    }

    /**
     * Constructor for this request.
     * @param inLayout - value to assign to the {@link Layout} enum.
     * @param inTabId - value to assign to the Tab id.
     */
    public SetTabLayoutRequest(final Layout inLayout, final Long inTabId)
    {
        layout = inLayout;
        tabId = inTabId;
    }

    /**
     * @return the layout
     */
    public Layout getLayout()
    {
        return layout;
    }

    /**
     * @param inLayout the layout to set
     */
    public void setLayout(final Layout inLayout)
    {
        this.layout = inLayout;
    }

    /**
     * @return the tabId
     */
    public Long getTabId()
    {
        return tabId;
    }

    /**
     * @param inTabId the tabId to set
     */
    public void setTabId(final Long inTabId)
    {
        this.tabId = inTabId;
    }
}
