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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Represents one display of a connection count, complete with label.
 */
public class ConnectionCountPanel extends FlowPanel
{
    /**
     * The label that acts as a button.
     */
    private Label countLabel;

    /**
     * The number to divide by to get "K".
     */
    private static final Integer ONETHOUSAND = 1000;

    /**
     * Constructor.
     *
     * @param description
     *            the descriptive label to be put under this panel.
     * @param count
     *            the number to be displayed
     */
    public ConnectionCountPanel(final String description, final int count)
    {
        this(description, count, "");
    }
    
    /**
     * Constructor.
     *
     * @param description
     *            the descriptive label to be put under this panel.
     * @param count
     *            the number to be displayed
     * @param style
     *            the custom css style class to add
     */
    public ConnectionCountPanel(final String description, final int count, final String style)
    {
        String countStr = String.valueOf(count);

        if (count > ONETHOUSAND)
        {
            double countInK = new Double(count) / ONETHOUSAND;
            String countInKStr = String.valueOf(countInK);
            countStr = countInKStr.substring(0, countInKStr.indexOf(".") + 2) + "K";
        }

        this.addStyleName("profile-connection-subpanel");
        if (!style.equals(""))
        {
            this.addStyleName(style);
        }

        countLabel = new Label(countStr);
        countLabel.addStyleName("profile-connection-count");
        this.add(countLabel);

        Label followersLabel = new Label(description);
        followersLabel.addStyleName("profile-connection-label");
        this.add(followersLabel);
    }

    /**
     * Add a listener to the count label/button.
     *
     * @param listener
     *            add the specified listener to the follower button.
     */
    public void addClickHandler(final ClickHandler listener)
    {
        countLabel.addClickHandler(listener);
    }

    /**
     * Used to update the count dynamically.
     *
     * @param newCount
     *            The new count value.
     */
    public void updateCount(final int newCount)
    {
        countLabel.setText(String.valueOf(newCount));
    }
}
