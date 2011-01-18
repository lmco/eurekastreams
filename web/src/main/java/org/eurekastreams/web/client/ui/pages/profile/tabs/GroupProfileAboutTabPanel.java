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

import java.util.HashMap;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateGroupResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.profile.widgets.BackgroundItemLinksPanel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
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
        final HashMap<String, String> basicInfoTabURL = new HashMap<String, String>();
        basicInfoTabURL.put("tab", "Basic Info");

        CreateUrlRequest target = new CreateUrlRequest(Page.GROUP_SETTINGS, group.getShortName());
        target.setParameters(basicInfoTabURL);

        Panel overviewPanel = createTitledPanel("Overview");
        Panel keywordsPanel = createTitledPanel("Keywords");

        final HTML noOverview = new HTML("An overview has not been added.");
        final HTML noKeywords = new HTML("Keywords have not been added.");

        final Hyperlink overviewHyperlink = new Hyperlink();
        final Hyperlink keywordsHyperlink = new Hyperlink();

        addLeft(overviewPanel);
        addRight(keywordsPanel);

        if (group.getOverview() == null || group.getOverview().trim().isEmpty())
        {
            overviewHyperlink.setText("Add an overview.");
            overviewHyperlink.setTargetHistoryToken(Session.getInstance().generateUrl(target));
            overviewHyperlink.setVisible(false);

            overviewPanel.add(overviewHyperlink);
            overviewPanel.add(noOverview);
        }
        else
        {
            HTML overview = new HTML(group.getOverview());
            overview.addStyleName("profile-about-overview");
            overviewPanel.add(overview);
        }

        if (group.getCapabilities().isEmpty())
        {
            keywordsHyperlink.setText("Add keywords.");
            keywordsHyperlink.setTargetHistoryToken(Session.getInstance().generateUrl(target));
            keywordsHyperlink.setVisible(false);

            keywordsPanel.add(keywordsHyperlink);
            keywordsPanel.add(noKeywords);
        }
        else
        {
            keywordsPanel.add(new BackgroundItemLinksPanel("keywords", group.getCapabilities()));
        }

        // Shows the appropriate "add" links for group coordinators, if necessary.
        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(AuthorizeUpdateGroupResponseEvent.class, new Observer<AuthorizeUpdateGroupResponseEvent>()
        {
            public void update(final AuthorizeUpdateGroupResponseEvent event)
            {
                if (event.getResponse())
                {
                    if (!overviewHyperlink.getText().isEmpty())
                    {
                        overviewHyperlink.setVisible(true);
                        noOverview.setVisible(false);
                    }

                    if (!keywordsHyperlink.getText().isEmpty())
                    {
                        keywordsHyperlink.setVisible(true);
                        noKeywords.setVisible(false);
                    }
                }
            }
        });
    }
}
