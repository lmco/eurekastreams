/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.ActionExecutorPanel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.NotificationSettingsPage;
import org.eurekastreams.web.client.ui.pages.activity.ActivityContent;
import org.eurekastreams.web.client.ui.pages.discover.DiscoverContent;
import org.eurekastreams.web.client.ui.pages.metrics.MetricsSummaryContent;
import org.eurekastreams.web.client.ui.pages.oauth.OAuthAuthorizeContent;
import org.eurekastreams.web.client.ui.pages.profile.settings.CreateGroupPanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.GroupProfileSettingsPanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.PersonalProfileSettingsPanel;
import org.eurekastreams.web.client.ui.pages.search.SearchContent;
import org.eurekastreams.web.client.ui.pages.settings.SettingsContent;
import org.eurekastreams.web.client.ui.pages.start.StartPageContent;
import org.eurekastreams.web.client.ui.pages.start.gallery.GalleryContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

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
     * @param contentPanel
     *            panel to add page to.
     * @return the page.
     */
    public String createPage(final Page page, final List<String> views, final FlowPanel contentPanel)
    {
        RootPanel.get().setStyleName("");

        String viewStr = "";
        if (views.size() > 0)
        {
            viewStr = views.get(0);
        }

        final String view = viewStr;

        switch (page)
        {
        case ACTION:
            Session.getInstance().setPageTitle("Action");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new ActionExecutorPanel(Session.getInstance().getActionProcessor(), view));
                }
            });
            break;
        case SEARCH:
            Session.getInstance().setPageTitle("Search");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new SearchContent());
                }
            });
            break;
        case SETTINGS:
            Session.getInstance().setPageTitle("Settings");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new SettingsContent(Session.getInstance().getActionProcessor()));
                }
            });
            break;
        case AUTHORIZE:
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new OAuthAuthorizeContent(Session.getInstance().getActionProcessor(), view));
                }
            });
            break;
        case GALLERY:
            Session.getInstance().setPageTitle("Gallery");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new GalleryContent());
                }
            });
            break;
        case ACTIVITY:
            Session.getInstance().setPageTitle("Activity");
            contentPanel.add(new ActivityContent());
            break;
        case PEOPLE_LEGACY:
        	if (Session.getInstance().getHistoryHandler().getParameters().containsKey("activityId"))
        	{
                return Session.getInstance().getHistoryHandler().getParameters().get("activityId");
        	}
        	else
        	{
        		return Session.getInstance().generateUrl(new CreateUrlRequest(Page.PEOPLE, views));
        	}
        case GROUPS_LEGACY:
        	if (Session.getInstance().getHistoryHandler().getParameters().containsKey("activityId"))
        	{
                return Session.getInstance().getHistoryHandler().getParameters().get("activityId");
        	}
        	else
        	{
        		return Session.getInstance().generateUrl(new CreateUrlRequest(Page.GROUPS, views));
        	}
        case PERSONAL_SETTINGS:
            Session.getInstance().setPageTitle("Settings");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new PersonalProfileSettingsPanel());
                }
            });
            break;
        case GROUP_SETTINGS:
            Session.getInstance().setPageTitle("Settings");            
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new GroupProfileSettingsPanel(view));
                }
            });
            break;
        case NEW_GROUP:
            Session.getInstance().setPageTitle("New Group");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new CreateGroupPanel(view));
                }
            });
            break;
        case METRICS:
            Session.getInstance().setPageTitle("Metrics");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new MetricsSummaryContent(view));
                }
            });
            break;
        case DISCOVER:
            Session.getInstance().setPageTitle("Discover");
            GWT.runAsync(new RunAsyncCallback()
            {
                public void onFailure(final Throwable reason)
                {
                }

                public void onSuccess()
                {
                    contentPanel.add(new DiscoverContent());
                }
            });
            break;
        case NOTIFICATION_SETTINGS:
            Session.getInstance().setPageTitle("Notification Settings");            
            contentPanel.add(new NotificationSettingsPage());
            break;
        default:
            Session.getInstance().setPageTitle("Start Page");            
            contentPanel.add(new StartPageContent());
            break;
        }
        
        return null;
    }
}
