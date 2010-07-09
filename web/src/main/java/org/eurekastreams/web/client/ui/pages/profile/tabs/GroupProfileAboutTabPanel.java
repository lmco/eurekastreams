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

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.web.client.ui.pages.profile.widgets.BackgroundItemLinksPanel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Panel used on the About tab of a group profile to display information about the given group.
 */
public class GroupProfileAboutTabPanel extends ProfileAboutTabPanel
{
    /**
     * Constructor.
     *
     * @param group
     *            Group to display.
     */
    public GroupProfileAboutTabPanel(final DomainGroup group)
    {
        final Panel overviewPanel = createTitledPanel("Overview");
        final Panel keywordsPanel = createTitledPanel("Keywords");

        addLeft(overviewPanel);
        addRight(keywordsPanel);

        if (group.getOverview() == null || group.getOverview().trim().isEmpty())
        {
            Label none = new Label("No overview entered.");
            none.addStyleName("profile-about-none-label");
            overviewPanel.add(none);
        }
        else
        {
            HTML overview = new HTML(group.getOverview());
            overview.addStyleName("profile-about-overview");
            overviewPanel.add(overview);
        }

        keywordsPanel.add(new BackgroundItemLinksPanel("keywords", group.getCapabilities()));
    }
}
