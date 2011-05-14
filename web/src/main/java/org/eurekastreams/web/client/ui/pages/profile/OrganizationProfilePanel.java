/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.profile;

import java.util.HashMap;

import org.eurekastreams.server.action.request.profile.GetPendingGroupsRequest;
import org.eurekastreams.server.action.request.stream.GetFlaggedActivitiesRequest;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.SwitchToFilterOnPagedFilterPanelEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotFlaggedActivitiesResponseEvent;
import org.eurekastreams.web.client.events.data.GotPendingGroupsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedActivityFlagResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedReviewPendingGroupResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.FlaggedActivityModel;
import org.eurekastreams.web.client.model.OrganizationModel;
import org.eurekastreams.web.client.model.PendingGroupsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LeftBarPanel;
import org.eurekastreams.web.client.ui.common.Pager;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.PendingGroupRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.SingleColumnPagedListRenderer;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.profile.widgets.ConnectionsPanel;
import org.eurekastreams.web.client.utility.BaseActivityLinkBuilder;
import org.eurekastreams.web.client.utility.InContextActivityLinkBuilder;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Displays a summary of a person's profile.
 */
public class OrganizationProfilePanel extends FlowPanel
{
    /**
     * Spacing panel -- we use floats in on the ProfilePage, so make sure nothing below it intrudes.
     */
    SimplePanel clearPanel = new SimplePanel();

    /**
     * Holds the PortalPage section of the profile display.
     */
    private TabContainerPanel portalPage = null;

    /**
     * Link to go to the profile settings page.
     */
    private final Hyperlink profileSettingsLink;

    /**
     * Panel that holds the tabbed portion of the profile display.
     */
    private final FlowPanel portalPageContainer = new FlowPanel();

    /**
     * Left bar container.
     */
    private final FlowPanel leftBarContainer = new FlowPanel();
    /**
     * panel that holds the profile summary.
     */
    private final LeftBarPanel leftBarPanel = new LeftBarPanel();

    /**
     * Connections Panel Holds the Small boxes with the connect counts.
     */
    private ConnectionsPanel connectionsPanel;

    /** Number of flagged activities (for use on the admin tab label). */
    private int flaggedActivityCount = 0;

    /** Number of pending groups (for use on the admin tab label). */
    private int pendingGroupCount = 0;

    /**
     * Number of group decendents. Track this local so we can adjust connctions tab number when approving pending groups
     * without going back to server.
     */
    private int orgDescendantGroupCount = 0;

    /**
     * Constructor.
     * 
     * @param accountId
     *            the account id.
     */
    public OrganizationProfilePanel(final String accountId)
    {
        RootPanel.get().addStyleName(StaticResourceBundle.INSTANCE.coreCss().profile());

        profileSettingsLink = new Hyperlink("Configure", "");

        final Hyperlink addGroupLink = new Hyperlink("", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.NEW_GROUP, accountId)));

        addGroupLink.setVisible(true);
        portalPageContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profilePageContainer());
        profileSettingsLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().configureTab());
        profileSettingsLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
        addGroupLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileAddGroup());
        leftBarContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().leftBarContainer());

        this.add(addGroupLink);

        if (Session.getInstance().getCurrentPerson().getRoles().contains(Role.SYSTEM_ADMIN))
        {
            this.add(profileSettingsLink);
        }
        leftBarContainer.add(leftBarPanel);
        this.add(leftBarContainer);
        this.add(portalPageContainer);
        this.add(clearPanel);

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profilePage());

        setEntity(null);

        OrganizationModel.getInstance().fetch(accountId, false);
    }

    /**
     * We have the org, so set up the Profile summary.
     * 
     * @param inOrg
     *            the org whose profile is being displayed
     */
    public void setEntity(final OrganizationModelView inOrg)
    {
        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.notifyObservers(new SetBannerEvent(null));

        leftBarPanel.clear();
        portalPageContainer.clear();

        // Update the Profile summary
        leftBarPanel.clear();

        final StreamPanel streamContent = new StreamPanel(ShowRecipient.ALL);

        portalPage = new TabContainerPanel();
        portalPage.addTab(new SimpleTab("Stream", streamContent));

        profileSettingsLink.setTargetHistoryToken(Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ORG_SETTINGS, "")));

        profileSettingsLink.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
        RootPanel.get().addStyleName(StaticResourceBundle.INSTANCE.coreCss().authenticated());

        if (Session.getInstance().getCurrentPerson().getRoles().contains(Role.SYSTEM_ADMIN))
        {
            final SimpleTab adminTab = buildAdminTab();
            portalPage.addTab(adminTab);

            // if heading for admin tab on initial entry to the org profile page, then do it
            switchToAdminTabFilterIfRequested();
        }

        // listen for history change event so that notifications can later send user to admin
        // tab
        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent inArg1)
                    {
                        switchToAdminTabFilterIfRequested();
                    }
                });

        portalPage.init();
        portalPage.setStyleName(StaticResourceBundle.INSTANCE.coreCss().profileGadgetsContainer());
        portalPageContainer.add(portalPage);
    }

    /**
     * Switches to the admin tab and filters thereon if URL parameters dictate.
     */
    private void switchToAdminTabFilterIfRequested()
    {
        if (!Session.getInstance().getCurrentPerson().getRoles().contains(Role.SYSTEM_ADMIN))
        {
            return;
        }

        if ("Admin".equals(Session.getInstance().getParameterValue("tab")))
        {
            portalPage.switchToTab("Admin");
            String adminFilter = Session.getInstance().getParameterValue("adminFilter");
            if (adminFilter != null)
            {
                // remove parameter from the URL. Since changing filters does not keep it updated, we
                // don't want to be on a different filter with this still in the URL.
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("adminFilter", null);
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdateHistoryEvent(new CreateUrlRequest(params, false)));

                Session.getInstance().getEventBus().notifyObservers(
                        new SwitchToFilterOnPagedFilterPanelEvent("admin", adminFilter, "", true));
            }
        }
    }

    /**
     * Builds the admin tab.
     * 
     * @return The tab.
     */
    private SimpleTab buildAdminTab()
    {
        final EventBus eventBus = Session.getInstance().getEventBus();
        final String flaggedActivitiesFilterName = "Flagged Activities";
        final String pendingGroupsFilterName = "Group Requests";

        // set up the tab itself
        final PagedListPanel adminTabContent = new PagedListPanel("admin", new SingleColumnPagedListRenderer(), "tab",
                "Admin");
        final SimpleTab adminTab = new SimpleTab("Admin", adminTabContent);
        adminTab.setParamsToClear(PagedListPanel.URL_PARAM_LIST_ID, PagedListPanel.URL_PARAM_FILTER,
                PagedListPanel.URL_PARAM_SORT, Pager.URL_PARAM_START_INDEX, Pager.URL_PARAM_END_INDEX);

        // wire up the data retrieval events
        eventBus.addObserver(GotFlaggedActivitiesResponseEvent.class, new Observer<GotFlaggedActivitiesResponseEvent>()
        {
            public void update(final GotFlaggedActivitiesResponseEvent event)
            {
                if (flaggedActivitiesFilterName.equals(adminTabContent.getCurrentFilter()))
                {
                    adminTabContent.render(event.getResponse(), "No flagged activities.");
                }
                flaggedActivityCount = event.getResponse().getTotal();
                adminTabContent.setFilterTitle(flaggedActivitiesFilterName, "Flagged Activities ("
                        + flaggedActivityCount + ")");
                adminTab.setTitle("Admin (" + (flaggedActivityCount + pendingGroupCount) + ")");
            }
        });

        eventBus.addObserver(GotPendingGroupsResponseEvent.class, new Observer<GotPendingGroupsResponseEvent>()
        {
            public void update(final GotPendingGroupsResponseEvent event)
            {
                if (pendingGroupsFilterName.equals(adminTabContent.getCurrentFilter()))
                {
                    adminTabContent.render(event.getResponse(), "No pending group requests.");
                }
                pendingGroupCount = event.getResponse().getTotal();
                adminTabContent.setFilterTitle(pendingGroupsFilterName, "Group Requests (" + pendingGroupCount + ")");
                adminTab.setTitle("Admin (" + (flaggedActivityCount + pendingGroupCount) + ")");
            }
        });

        // request the data to get the counts
        // We need the counts for both of the lists, but at most one list will perform an initial data load, we need to
        // force the load. (Only the list which is visible will load; if the tab is inactive then there are zero
        // visible lists.)
        FlaggedActivityModel.getInstance().fetch(new GetFlaggedActivitiesRequest(0, 1), false);
        PendingGroupsModel.getInstance().fetch(new GetPendingGroupsRequest(0, 1), false);

        // wire up events to refresh the list when something is removed
        eventBus.addObserver(UpdatedActivityFlagResponseEvent.class, new Observer<UpdatedActivityFlagResponseEvent>()
        {
            public void update(final UpdatedActivityFlagResponseEvent ev)
            {
                eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                        "The flagged activity has been allowed")));
                adminTabContent.reload();
            }
        });

        eventBus.addObserver(UpdatedReviewPendingGroupResponseEvent.class,
                new Observer<UpdatedReviewPendingGroupResponseEvent>()
                {
                    public void update(final UpdatedReviewPendingGroupResponseEvent ev)
                    {
                        adminTabContent.reload();
                        if ((ev.getResponse()).getApproved())
                        {
                            orgDescendantGroupCount++;
                            connectionsPanel.updateCount("Groups", orgDescendantGroupCount);
                        }
                    }
                });

        eventBus.addObserver(DeletedActivityResponseEvent.class, new Observer<DeletedActivityResponseEvent>()
        {
            public void update(final DeletedActivityResponseEvent ev)
            {
                adminTabContent.reload();
            }
        });

        // prepare the StaticResourceBundle.INSTANCE.coreCss().filters()
        // flagged content StaticResourceBundle.INSTANCE.coreCss().filter()
        StreamMessageItemRenderer flaggedRenderer = new StreamMessageItemRenderer(ShowRecipient.ALL);
        flaggedRenderer.setShowManageFlagged(true);
        flaggedRenderer.setShowComment(true);
        BaseActivityLinkBuilder activityLinkBuilder = new InContextActivityLinkBuilder();
        activityLinkBuilder.addExtraParameter("manageFlagged", "true");
        flaggedRenderer.setActivityLinkBuilder(activityLinkBuilder);
        adminTabContent.addSet(flaggedActivitiesFilterName, FlaggedActivityModel.getInstance(), flaggedRenderer,
                new GetFlaggedActivitiesRequest(0, 0));
        // pending groups StaticResourceBundle.INSTANCE.coreCss().filter()
        adminTabContent.addSet(pendingGroupsFilterName, PendingGroupsModel.getInstance(), new PendingGroupRenderer(),
                new GetPendingGroupsRequest(0, 0));

        return adminTab;
    }
}
