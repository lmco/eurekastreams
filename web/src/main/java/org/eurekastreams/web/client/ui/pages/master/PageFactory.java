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
package org.eurekastreams.web.client.ui.pages.master;

import java.util.List;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.ui.ActionExecutorPanel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.help.HelpContent;
import org.eurekastreams.web.client.ui.pages.oauth.OAuthAuthorizeContent;
import org.eurekastreams.web.client.ui.pages.profile.GroupProfilePanel;
import org.eurekastreams.web.client.ui.pages.profile.OrganizationProfilePanel;
import org.eurekastreams.web.client.ui.pages.profile.PersonalProfilePanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.CreateGroupPanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.CreateOrganizationPanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.GroupProfileSettingsPanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.OrganizationProfileSettingsPanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.PersonalProfileSettingsPanel;
import org.eurekastreams.web.client.ui.pages.search.AdvancedSearchContent;
import org.eurekastreams.web.client.ui.pages.search.SearchContent;
import org.eurekastreams.web.client.ui.pages.settings.SettingsContent;
import org.eurekastreams.web.client.ui.pages.start.StartPageContent;
import org.eurekastreams.web.client.ui.pages.start.gallery.GalleryContent;
import org.eurekastreams.web.client.ui.pages.stream.StreamContent;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates a page given a page and view.
 *
 */
public class PageFactory
{
    /**
     * Creates a page given a page and view.
     *
     * @param page
     *            the page.
     * @param views
     *            the views.
     * @return the page widget.
     */
    public Widget createPage(final Page page, final List<String> views)
    {
        RootPanel.get().setStyleName("");

        String view = "";
        if (views.size() > 0)
        {
            view = views.get(0);
        }

        switch (page)
        {
        case ACTION:
            return new ActionExecutorPanel(Session.getInstance().getActionProcessor(), view);
        case ADVANCED_SEARCH:
            return new AdvancedSearchContent();
        case SEARCH:
            return new SearchContent();
        case SETTINGS:
            return new SettingsContent(Session.getInstance().getActionProcessor());
        case AUTHORIZE:
            return new OAuthAuthorizeContent(Session.getInstance().getActionProcessor(), view);
        case GALLERY:
            return new GalleryContent();
        case ACTIVITY:
            return new StreamContent();
        case PEOPLE:
            return new PersonalProfilePanel(view);
        case PERSONAL_SETTINGS:
            return new PersonalProfileSettingsPanel();
        case GROUPS:
            return new GroupProfilePanel(view);
        case GROUP_SETTINGS:
            return new GroupProfileSettingsPanel(view);
        case NEW_GROUP:
            return new CreateGroupPanel(view);
        case ORGANIZATIONS:
            return new OrganizationProfilePanel(view);
        case ORG_SETTINGS:
            return new OrganizationProfileSettingsPanel(view);
        case NEW_ORG:
            return new CreateOrganizationPanel(view);
        case HELP:
            return new HelpContent();
        default:
            return new StartPageContent();
        }

    }
}
