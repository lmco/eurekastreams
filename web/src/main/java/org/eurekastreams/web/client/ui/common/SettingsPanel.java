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

import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A settings panel should be extended to add a border, dim the banner, and add a close button. NOTE: This only works if
 * a banner is on the screen. More work would be needed otherwise.
 * 
 */
public class SettingsPanel extends FlowPanel
{
    /**
     * The content panel.
     */
    private FlowPanel contentPanel;

    /**
     * The close button.
     */
    private Hyperlink close;

    /**
     * The page title.
     */
    private Label pageTitleLabel = new Label();

    /**
     * Default constructor.
     * 
     * @param inContentPanel
     *            the panel containing the settings content.
     * 
     * @param inPageTitle
     *            the page title.
     */
    public SettingsPanel(final FlowPanel inContentPanel, final String inPageTitle)
    {
        contentPanel = inContentPanel;
        setPageTitle(inPageTitle);

        RootPanel.get().addStyleName("settings-panel");
        contentPanel.setStyleName("settings-content");

        FlowPanel title = new FlowPanel();
        title.addStyleName("settings-title-bar");
        close = new Hyperlink();
        title.add(close);
        pageTitleLabel.setText(inPageTitle);
        title.add(pageTitleLabel);
        contentPanel.add(title);

        this.add(contentPanel);
    }

    /**
     * Sets the previous page link.
     * 
     * @param urlRequest
     *           the request to process to go to the previous page. 
     * 
     * @param title
     *            the title for the link.
     */
    public void setPreviousPage(final CreateUrlRequest urlRequest, final String title)
    {
        close.setText(title);
        close.setTargetHistoryToken(Session.getInstance().generateUrl(urlRequest));
    }

    /**
     * Clears the panel, and adds the title bar.
     */
    public void clearContentPanel()
    {
        contentPanel.clear();

        FlowPanel title = new FlowPanel();
        title.addStyleName("settings-title-bar");
        title.add(close);
        title.add(pageTitleLabel);
        contentPanel.add(title);
    }

    /**
     * @param inPageTitle
     *            the pageTitle to set
     */
    public void setPageTitle(final String inPageTitle)
    {
        pageTitleLabel.setText(inPageTitle);
    }
}
