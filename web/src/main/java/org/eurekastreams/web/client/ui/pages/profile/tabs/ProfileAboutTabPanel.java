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
package org.eurekastreams.web.client.ui.pages.profile.tabs;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base for panels used on the About tab of a profile to display information about the given user/group.
 */
public abstract class ProfileAboutTabPanel extends FlowPanel
{
    /** Left column. */
    private Panel leftColumn = new FlowPanel();

    /** Right column. */
    private Panel rightColumn = new FlowPanel();

    /**
     * Constructor.
     */
    protected ProfileAboutTabPanel()
    {
        addStyleName("layout-container profile-about");

        leftColumn.addStyleName("profile-about-column");
        rightColumn.addStyleName("profile-about-column");

        add(leftColumn);
        add(rightColumn);
    }

    /**
     * Adds a widget to the left column.
     *
     * @param widget
     *            Widget to add.
     */
    protected void addLeft(final Widget widget)
    {
        leftColumn.add(widget);
    }

    /**
     * Adds a widget to the right column.
     *
     * @param widget
     *            Widget to add.
     */
    protected void addRight(final Widget widget)
    {
        rightColumn.add(widget);
    }

    /**
     * Creates and styles a panel with the given title.
     *
     * @param title
     *            Title to display as a separator.
     * @return Panel.
     */
    protected Panel createTitledPanel(final String title)
    {
        Panel panel = new FlowPanel();
        panel.addStyleName("profile-about-background");

        Label label = new Label(title);
        label.addStyleName("profile-about-section-header");
        panel.add(label);

        return panel;
    }


}
