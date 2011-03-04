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
package org.eurekastreams.web.client.ui.common;

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;


/**
 * Styled Horizontal UL Panel.
 * 
 */
public class HorizontalULPanel extends ULPanel
{
    /**
     * Primary constructor sets up the style name.
     */
    public HorizontalULPanel()
    {
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().horizontal());
    }

    /**
     * Change display to show bars or not around the panel.
     * 
     * @param showBars
     *            flag to show bars around panel or not.
     */
    public void setShowBars(final boolean showBars)
    {
        if (showBars)
        {
            this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().barred());
        } 
        else
        {
            this.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().barred());
        }
    }
}
