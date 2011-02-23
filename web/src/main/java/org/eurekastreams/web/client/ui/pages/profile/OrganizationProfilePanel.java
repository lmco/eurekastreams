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
package org.eurekastreams.web.client.ui.pages.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.eurekastreams.server.action.request.profile.GetPendingGroupsRequest;
import org.eurekastreams.server.action.request.stream.GetFlaggedActivitiesByOrgRequest;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.ResourceSortCriteria;
import org.eurekastreams.server.domain.ResourceSortCriterion;
import org.eurekastreams.server.domain.ResourceSortCriterion.SortDirection;
import org.eurekastreams.server.domain.ResourceSortCriterion.SortField;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.SwitchToFilterOnPagedFilterPanelEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateGroupResponseEvent;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateOrganizationResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotFlaggedActivitiesResponseEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationEmployeesResponseEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationGroupsResponseEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationSubOrgsResponseEvent;
import org.eurekastreams.web.client.events.data.GotPendingGroupsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedActivityFlagResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedReviewPendingGroupResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.FlaggedActivityModel;
import org.eurekastreams.web.client.model.OrganizationEmployeesModel;
import org.eurekastreams.web.client.model.OrganizationGroupsModel;
import org.eurekastreams.web.client.model.OrganizationModel;
import org.eurekastreams.web.client.model.OrganizationSubOrgsModel;
import org.eurekastreams.web.client.model.PendingGroupsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LeftBarPanel;
import org.eurekastreams.web.client.ui.common.Pager;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.pagedlist.GroupRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.OrganizationRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.PendingGroupRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.SingleColumnPagedListRenderer;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.BreadcrumbPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.ConnectionsPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.OrgAboutPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.PeopleListPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.PopularHashtagsPanel;
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
     * Panel that shows the bread crumb navigation.
     */
    private final BreadcrumbPanel breadCrumbPanel;

    /**
     * Link to go to the profile settings page.
     */
    private final Hyperlink profileSettingsLink;
    /**
     * Link to add a new sub org.
     */
    private final Hyperlink addSubOrgLink;
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
     * The org.
     */
    private OrganizationModelView org;

    /**
     * Connections Panel Holds the Small boxes with the connect counts.
     */
    private ConnectionsPanel connectionsPanel;

    /**
     * Action Processor.
     */
    private final ActionProcessor processor = Session.getInstance().getActionProcessor();

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
        RootPanel.get().addStyleName("profile");

        profileSettingsLink = new Hyperlink("Configure", "");
        addSubOrgLink = new Hyperlink("", "");
        final Hyperlink addGroupLink = new Hyperlink("", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.NEW_GROUP, accountId)));

        ActionProcessor inProcessor = Session.getInstance().getActionProcessor();

        addGroupLink.setVisible(false);
        portalPageContainer.addStyleName("profile-page-container");
        profileSettingsLink.addStyleName("configure-tab");
        profileSettingsLink.addStyleName("hidden");
        addSubOrgLink.addStyleName("profile-add-sub-org");
        addSubOrgLink.addStyleName("hidden");
        addGroupLink.addStyleName("profile-add-group");
        leftBarContainer.addStyleName("left-bar-container");
        breadCrumbPanel = new BreadcrumbPanel(inProcessor);

        this.add(breadCrumbPanel);
        this.add(addSubOrgLink);
        this.add(addGroupLink);
        this.add(profileSettingsLink);
        leftBarContainer.add(leftBarPanel);
        this.add(leftBarContainer);
        this.add(portalPageContainer);
        this.add(clearPanel);

        this.addStyleName("profile-page");

        EventBus.getInstance().addObserver(GotOrganizationModelViewInformationResponseEvent.class,
                new Observer<GotOrganizationModelViewInformationResponseEvent>()
                {
                    public void update(final GotOrganizationModelViewInformationResponseEvent event)
                    {
                        addGroupLink.setVisible(true);
                        setEntity(event.getResponse());
                    }
                });

        OrganizationModel.getInstance().fetch(accountId, false);
    }

    /**
     * We have the Person, so set up the Profile summary.
     * 
     * @param inOrg
     *            the person whose profile is being displayed
     */
    public void setEntity(final OrganizationModelView inOrg)
    {
        org = inOrg;

        leftBarPanel.clear();
        portalPageContainer.clear();

        // Set the banner.
        Session.getInstance().getEventBus().notifyObservers(new SetBannerEvent(org));

        breadCrumbPanel.setOrganization(org);

        // Update the Profile summary
        leftBarPanel.clear();

        leftBarPanel.addChildWidget(new OrgAboutPanel(org.getName(), org.getEntityId(), org.getAvatarId(),
                org.getUrl(), org.getDescription()));
        leftBarPanel.addChildWidget(new PopularHashtagsPanel(ScopeType.ORGANIZATION, org.getShortName()));

        connectionsPanel = new ConnectionsPanel();
        orgDescendantGroupCount = org.getDescendantGroupCount();
        connectionsPanel.addConnection("Employees", "Recently Added", org.getDescendantEmployeeCount());
        connectionsPanel.addConnection("Groups", "Recently Added", orgDescendantGroupCount, "center");
        connectionsPanel.addConnection("Sub Orgs", null, org.getChildOrganizationCount());
        connectionsPanel.addStyleName("org-connections");

        leftBarPanel.addChildWidget(connectionsPanel);

        leftBarPanel.addChildWidget(new PeopleListPanel(new HashSet<PersonModelView>(org.getLeaders()), "Leadership",
                PeopleListPanel.DISPLAY_ALL, null));

        final StreamPanel streamContent = new StreamPanel(true);
        String jsonRequest = StreamJsonRequestFactory.setOrganization(org.getShortName(),
                StreamJsonRequestFactory.getEmptyRequest()).toString();

        EventBus.getInstance().notifyObservers(new StreamRequestEvent(org.getName(), jsonRequest));

        portalPage = new TabContainerPanel();
        portalPage.addTab(new SimpleTab("Stream", streamContent));
        portalPage.addTab(buildConnectionsTab());

        Session.getInstance().getEventBus().addObserver(AuthorizeUpdateOrganizationResponseEvent.class,
                new Observer<AuthorizeUpdateGroupResponseEvent>()
                {
                    public void update(final AuthorizeUpdateGroupResponseEvent event)
                    {
                        if (event.getResponse())
                        {

                            profileSettingsLink.setTargetHistoryToken(Session.getInstance().generateUrl(
                                    new CreateUrlRequest(Page.ORG_SETTINGS, org.getShortName())));
                            addSubOrgLink.setTargetHistoryToken(Session.getInstance().generateUrl(
                                    new CreateUrlRequest(Page.NEW_ORG, org.getShortName())));

                            profileSettingsLink.removeStyleName("hidden");
                            addSubOrgLink.removeStyleName("hidden");
                            RootPanel.get().addStyleName("authenticated");

                            final SimpleTab adminTab = buildAdminTab();
                            portalPage.addTab(adminTab);

                            // if heading for admin tab on initial entry to the org profile page, then do it
                            switchToAdminTabFilterIfRequested();

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
                        }
                    }
                });

        OrganizationModel.getInstance().authorize(org.getShortName(), false);

        portalPage.init();
        portalPage.setStyleName("profile-gadgets-container");
        portalPageContainer.add(portalPage);
    }

    /**
     * Switches to the admin tab and filters thereon if URL parameters dictate.
     */
    private void switchToAdminTabFilterIfRequested()
    {
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
     * Builds the connections tab.
     * 
     * @return The tab.
     */
    private SimpleTab buildConnectionsTab()
    {
        // Connection Tab
        List<ResourceSortCriterion> critsForDataAdded = new ArrayList<ResourceSortCriterion>();
        critsForDataAdded.add(new ResourceSortCriterion(SortField.DATE_ADDED, SortDirection.DESCENDING));

        List<ResourceSortCriterion> critsForFollowers = new ArrayList<ResourceSortCriterion>();
        critsForFollowers.add(new ResourceSortCriterion(SortField.FOLLOWERS_COUNT, SortDirection.DESCENDING));

        final PagedListPanel connectionTabContent = new PagedListPanel("connections", "tab", "Connections");

        Session.getInstance().getEventBus().addObserver(GotOrganizationEmployeesResponseEvent.class,
                new Observer<GotOrganizationEmployeesResponseEvent>()
                {
                    public void update(final GotOrganizationEmployeesResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "No people are in this organization.");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotOrganizationGroupsResponseEvent.class,
                new Observer<GotOrganizationGroupsResponseEvent>()
                {
                    public void update(final GotOrganizationGroupsResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "No groups are in this organization.");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotOrganizationSubOrgsResponseEvent.class,
                new Observer<GotOrganizationSubOrgsResponseEvent>()
                {
                    public void update(final GotOrganizationSubOrgsResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(),
                                "No sub organizations are in this organization.");
                    }
                });

        connectionTabContent.addSet("Employees", OrganizationEmployeesModel.getInstance(), new PersonRenderer(false),
                new GetDirectorySearchResultsRequest(org.getShortName(), 0, 0, new ResourceSortCriteria(
                        critsForDataAdded)), "Recently Added");

        connectionTabContent.addSet("Employees", OrganizationEmployeesModel.getInstance(), new PersonRenderer(false),
                new GetDirectorySearchResultsRequest(org.getShortName(), 0, 0, new ResourceSortCriteria(
                        critsForFollowers)), "Followers");

        connectionTabContent.addSet("Groups", OrganizationGroupsModel.getInstance(), new GroupRenderer(),
                new GetDirectorySearchResultsRequest(org.getShortName(), 0, 0, new ResourceSortCriteria(
                        critsForDataAdded)), "Recently Added");

        connectionTabContent.addSet("Groups", OrganizationGroupsModel.getInstance(), new GroupRenderer(),
                new GetDirectorySearchResultsRequest(org.getShortName(), 0, 0, new ResourceSortCriteria(
                        critsForFollowers)), "Followers");

        connectionTabContent.addSet("Sub Orgs", OrganizationSubOrgsModel.getInstance(), new OrganizationRenderer(),
                new GetDirectorySearchResultsRequest(org.getShortName(), 0, 0, new ResourceSortCriteria(
                        critsForDataAdded)));

        SimpleTab connTab = new SimpleTab("Connections", connectionTabContent);
        connTab.setParamsToClear(PagedListPanel.URL_PARAM_LIST_ID, PagedListPanel.URL_PARAM_FILTER,
                PagedListPanel.URL_PARAM_SORT, Pager.URL_PARAM_START_INDEX, Pager.URL_PARAM_END_INDEX);

        return connTab;
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
        FlaggedActivityModel.getInstance().fetch(new GetFlaggedActivitiesByOrgRequest(org.getEntityId(), 0, 1), false);
        PendingGroupsModel.getInstance().fetch(new GetPendingGroupsRequest(org.getShortName(), 0, 1), false);

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

        // prepare the "filters"
        // flagged content "filter"
        StreamMessageItemRenderer flaggedRenderer = new StreamMessageItemRenderer(true);
        flaggedRenderer.setShowManageFlagged(true);
        flaggedRenderer.setShowComment(true);
        BaseActivityLinkBuilder activityLinkBuilder = new InContextActivityLinkBuilder();
        activityLinkBuilder.addExtraParameter("manageFlagged", "true");
        flaggedRenderer.setActivityLinkBuilder(activityLinkBuilder);
        adminTabContent.addSet(flaggedActivitiesFilterName, FlaggedActivityModel.getInstance(), flaggedRenderer,
                new GetFlaggedActivitiesByOrgRequest(org.getEntityId(), 0, 0));
        // pending groups "filter"
        adminTabContent.addSet(pendingGroupsFilterName, PendingGroupsModel.getInstance(), new PendingGroupRenderer(),
                new GetPendingGroupsRequest(org.getShortName(), 0, 0));

        return adminTab;
    }
}
