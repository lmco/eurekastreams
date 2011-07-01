/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import com.google.gwt.user.client.ui.Widget;

/**
 * Pager response event.
 */
public class PagerResponseEvent
{
    /**
     * The widget.
     */
    private Widget widget;

    /**
     * The key.
     */
    private String key;

    /**
     * Set the key.
     * 
     * @param inKey
     *            the key.
     */
    public void setKey(final String inKey)
    {
        this.key = inKey;
    }

    /**
     * Set the widget.
     * 
     * @param inWidget
     *            the widget to set
     */
    public void setWidget(final Widget inWidget)
    {
        this.widget = inWidget;
    }

    /**
     * Return the widget to render.
     * 
     * @return the widget
     */
    public Widget getWidget()
    {
        return widget;
    }

    /**
     * Return the pager key.
     * 
     * @return pager key.
     */
    public String getKey()
    {
        return key;
    }
}
