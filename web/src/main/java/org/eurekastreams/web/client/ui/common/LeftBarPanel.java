/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a panel of widgets aligned vertically along the left side.
 */
public class LeftBarPanel extends FlowPanel
{
    /**
     * The panel that holds the child widgets.
     */
    //Panel panel;

    /**
     * Constructor.
     */
    public LeftBarPanel()
    {
        //panel = new FlowPanel();
        //this.initWidget(panel);
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().leftBar());
    }

    /**
     * Add a widget to the panel.
     * 
     * @param widget
     *            the new widget
     */
    public void addChildWidget(final Widget widget)
    {
        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().leftBarChild());
        this.add(widget);
    }
}
