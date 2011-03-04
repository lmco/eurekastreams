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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateGroupResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
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
    public GroupProfileAboutTabPanel(final DomainGroupModelView group)
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
            overview.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileAboutOverview());
            overviewPanel.add(overview);
        }

        if (group.getCapabilities() == null || group.getCapabilities().isEmpty())
        {
            keywordsHyperlink.setText("Add keywords.");
            keywordsHyperlink.setTargetHistoryToken(Session.getInstance().generateUrl(target));
            keywordsHyperlink.setVisible(false);

            keywordsPanel.add(keywordsHyperlink);
            keywordsPanel.add(noKeywords);
        }
        else
        {
            List<String> caps = group.getCapabilities();
            List<BackgroundItem> bgitems = new ArrayList<BackgroundItem>();
            for (String cap : caps)
            {
                bgitems.add(new BackgroundItem(cap, BackgroundItemType.NOT_SET));
            }
            keywordsPanel.add(new BackgroundItemLinksPanel("keywords", bgitems));
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
