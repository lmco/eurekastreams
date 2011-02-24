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

import java.util.HashSet;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.action.request.profile.GetRequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.ChangeActivityModeEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.SwitchToFilterOnPagedFilterPanelEvent;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateGroupResponseEvent;
import org.eurekastreams.web.client.events.data.BaseDataResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedGroupMemberResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupCoordinatorsResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupMembersResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGroupMemberResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.AllPopularHashTagsModel;
import org.eurekastreams.web.client.model.GroupCoordinatorsModel;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.GroupMembershipRequestModel;
import org.eurekastreams.web.client.model.GroupModel;
import org.eurekastreams.web.client.model.OrganizationModel;
import org.eurekastreams.web.client.model.requests.GetGroupCoordinatorsRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LeftBarPanel;
import org.eurekastreams.web.client.ui.common.SpinnerLabelButton;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.pagedlist.ItemRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRequestingGroupMembershipRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.RemovableGroupMemberPersonRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.SingleColumnPagedListRenderer;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.profile.tabs.GroupProfileAboutTabPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.BreadcrumbPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.ChecklistProgressBarPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.ConnectionsPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.GroupAboutPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.PeopleListPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.PopularHashtagsPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays a summary of a group's profile.
 */
public class GroupProfilePanel extends FlowPanel
{

    /**
     * Spacing panel -- we use floats in on the ProfilePage, so make sure nothing below it intrudes.
     */
    SimplePanel clearPanel = new SimplePanel();

    /**
     * About box containing the logo.
     */
    private final GroupAboutPanel about;

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
     * The group whose profile we're looking at.
     */
    private DomainGroupModelView group;

    /**
     * The group entity for this panel.
     */
    private DomainGroupModelView groupEntity;

    /**
     * The panel that shows the checklist.
     */
    private ChecklistProgressBarPanel checklistPanel;

    /**
     * The divider separating the checklistPanel from the rest of the left-bar content.
     */
    private final FlowPanel checklistDivider = new FlowPanel();

    /**
     * Connections Panel Holds the Small boxes with the connect counts.
     */
    private ConnectionsPanel connectionsPanel;

    /**
     * Followers.
     */
    private int members;

    /** Number of membership requests (for use on the admin tab label). */
    private int membershipRequestsCount = 0;

    /**
     * Constructor.
     * 
     * @param accountId
     *            the account id.
     */
    public GroupProfilePanel(final String accountId)
    {
        RootPanel.get().addStyleName("profile");
        RootPanel.get().addStyleName("group");

        profileSettingsLink = new Hyperlink("Configure", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.GROUP_SETTINGS, accountId)));

        ActionProcessor inProcessor = Session.getInstance().getActionProcessor();

        about = new GroupAboutPanel(accountId);
        portalPageContainer.addStyleName("profile-page-container");
        profileSettingsLink.addStyleName("configure-tab");
        profileSettingsLink.addStyleName("hidden");
        leftBarContainer.addStyleName("left-bar-container");
        breadCrumbPanel = new BreadcrumbPanel(inProcessor);

        this.add(breadCrumbPanel);
        this.add(profileSettingsLink);
        leftBarContainer.add(leftBarPanel);
        this.add(leftBarContainer);
        this.add(portalPageContainer);
        this.add(clearPanel);

        this.addStyleName("profile-page");

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        setEntity(event.getResponse());
                    }
                });

        EventBus.getInstance().addObserver(ChangeActivityModeEvent.class, new Observer<ChangeActivityModeEvent>()
        {
            public void update(final ChangeActivityModeEvent event)
            {
                if (groupEntity != null)
                {
                    breadCrumbPanel.setGroup(groupEntity, event.isSingleMode());
                }
            }
        });

        inProcessor.setQueueRequests(true);
        GroupModel.getInstance().fetch(accountId, false);
        AllPopularHashTagsModel.getInstance().fetch(null, true);
        inProcessor.fireQueuedRequests();
        inProcessor.setQueueRequests(false);
    }

    /**
     * We have the Group, so set up the Profile summary.
     * 
     * @param inGroup
     *            the group whose profile is being displayed
     */
    private void setEntity(final DomainGroupModelView inGroup)
    {
        groupEntity = inGroup;
        ActionProcessor inProcessor = Session.getInstance().getActionProcessor();
        inProcessor.setQueueRequests(true);

        if (inGroup == null)
        {
            showInvalidGroupMessage();
            inProcessor.fireQueuedRequests();
            inProcessor.setQueueRequests(false);
            return;
        }

        // create the subset of UI items which are shown even if user does not have access

        // Set the banner.
        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.notifyObservers(new SetBannerEvent(inGroup));

        breadCrumbPanel.setGroup(inGroup, false);

        if (inGroup.isRestricted())
        {
            showRestrictedGroupMessage(inGroup);
            inProcessor.fireQueuedRequests();
            inProcessor.setQueueRequests(false);
            return;
        }
        group = inGroup;

        if (group.isPending())
        {
            showPendingGroupMessage();
            inProcessor.fireQueuedRequests();
            inProcessor.setQueueRequests(false);
            return;
        }

        checklistPanel = new ChecklistProgressBarPanel("Group Profile Checklist",
                "Completing the group profile is easy, upload a group avatar, "
                        + "enter a short description of the group stream " + "and an overview of the group "
                        + "and the members you are looking to participate in the stream. "
                        + "Groups that fill out their profile are more likely "
                        + "to be found by others across your organization.", new CreateUrlRequest(Page.GROUP_SETTINGS,
                        group.getShortName()));

        leftBarPanel.clear();
        portalPageContainer.clear();

        members = group.getFollowersCount();

        // Update the Profile summary
        about.setGroup(inGroup.getName(), inGroup.getId(), inGroup.getAvatarId(), inGroup.getUrl(), inGroup
                .getDescription());
        connectionsPanel = new ConnectionsPanel();
        connectionsPanel.addConnection("Members", null, group.getFollowersCount());

        eventBus.addObserver(InsertedGroupMemberResponseEvent.class, new Observer<InsertedGroupMemberResponseEvent>()
        {
            public void update(final InsertedGroupMemberResponseEvent event)
            {
                members++;
                connectionsPanel.updateCount("Members", members);
            }
        });

        eventBus.addObserver(DeletedGroupMemberResponseEvent.class, new Observer<DeletedGroupMemberResponseEvent>()
        {
            public void update(final DeletedGroupMemberResponseEvent event)
            {
                members--;
                connectionsPanel.updateCount("Members", members);
            }
        });

        PeopleListPanel coordinatorPanel = new PeopleListPanel(new HashSet<PersonModelView>(inGroup.getCoordinators()),
                "Coordinators", 2, new CreateUrlRequest("tab", "Connections", true),
                new SwitchToFilterOnPagedFilterPanelEvent("connections", "Coordinators"), null);

        leftBarPanel.addChildWidget(about);
        leftBarPanel.addChildWidget(new PopularHashtagsPanel(ScopeType.GROUP, group.getShortName()));
        leftBarPanel.addChildWidget(connectionsPanel);
        leftBarPanel.addChildWidget(coordinatorPanel);

        final StreamPanel streamContent = new StreamPanel(false);
        StreamScope groupStreamScope = new StreamScope(group.getName(), ScopeType.GROUP, group.getShortName(), group
                .getStreamId());

        streamContent.setStreamScope(groupStreamScope, group.isStreamPostable());

        String jsonRequest = StreamJsonRequestFactory.addRecipient(EntityType.GROUP, group.getShortName(),
                StreamJsonRequestFactory.getEmptyRequest()).toString();

        EventBus.getInstance().notifyObservers(new StreamRequestEvent(group.getName(), jsonRequest));

        if (group.isStreamPostable())
        {
            streamContent.setStreamScope(new StreamScope(ScopeType.GROUP, inGroup.getShortName()), true);
        }

        eventBus.addObserver(AuthorizeUpdateGroupResponseEvent.class, new Observer<AuthorizeUpdateGroupResponseEvent>()
        {
            public void update(final AuthorizeUpdateGroupResponseEvent event)
            {
                if (event.getResponse())
                {
                    profileSettingsLink.removeStyleName("hidden");
                    RootPanel.get().addStyleName("authenticated");
                    setUpChecklist();

                    if (!group.isPublic())
                    {
                        final SimpleTab adminTab = buildAdminTab();
                        portalPage.addTab(adminTab);

                        if ("Admin".equals(Session.getInstance().getParameterValue("tab")))
                        {
                            portalPage.switchToTab("Admin");
                        }
                    }

                    // if posting is disabled for group, re-enable it since
                    // user has org/group coord permissions.
                    if (!group.isStreamPostable())
                    {
                        streamContent.setStreamScope(new StreamScope(ScopeType.GROUP, inGroup.getShortName()), true);
                    }
                }
            }
        });

        portalPage = new TabContainerPanel();
        portalPage.addTab(new SimpleTab("Stream", streamContent));
        portalPage.addTab(new SimpleTab("Connections", buildConnectionsTabContent()));
        portalPage.addTab(new SimpleTab("About", new GroupProfileAboutTabPanel(group)));
        portalPage.init();

        portalPage.setStyleName("profile-gadgets-container");

        portalPageContainer.add(portalPage);

        GroupModel.getInstance().authorize(inGroup.getShortName(), false);

        inProcessor.setQueueRequests(false);
        inProcessor.fireQueuedRequests();
    }

    /**
     * Builds the connections tab content.
     * 
     * @return The tab.
     */
    private Widget buildConnectionsTabContent()
    {
        final EventBus eventBus = Session.getInstance().getEventBus();

        // Make the Connections Tab
        final PagedListPanel connectionTabContent = new PagedListPanel("connections");

        eventBus.addObserver(GotGroupMembersResponseEvent.class, new Observer<GotGroupMembersResponseEvent>()
        {
            public void update(final GotGroupMembersResponseEvent event)
            {
                connectionTabContent.render(event.getResponse(), "No one is following this group");
            }
        });

        eventBus.addObserver(GotGroupCoordinatorsResponseEvent.class, new Observer<GotGroupCoordinatorsResponseEvent>()
        {
            public void update(final GotGroupCoordinatorsResponseEvent event)
            {
                connectionTabContent.render(event.getResponse(), "This group has no coordinators");
            }
        });

        // update the list of members after joining/leaving the group
        eventBus.addObservers(new Observer<BaseDataResponseEvent<Integer>>()
        {
            public void update(final BaseDataResponseEvent<Integer> ev)
            {
                if ("Members".equals(connectionTabContent.getCurrentFilter()))
                {
                    connectionTabContent.refreshData();
                }
            }
        }, InsertedGroupMemberResponseEvent.class, DeletedGroupMemberResponseEvent.class);

        ItemRenderer<PersonModelView> memberRenderer = group.isPublic() ? new PersonRenderer(false)
                : new RemovableGroupMemberPersonRenderer(group.getUniqueId());
        connectionTabContent.addSet("Members", GroupMembersModel.getInstance(), memberRenderer,
                new GetFollowersFollowingRequest(EntityType.GROUP, group.getShortName(), 0, 0));

        connectionTabContent.addSet("Coordinators", GroupCoordinatorsModel.getInstance(), new PersonRenderer(false),
                new GetGroupCoordinatorsRequest(group.getShortName(), new HashSet<PersonModelView>(group
                        .getCoordinators())));

        return connectionTabContent;
    }

    /**
     * Set up the checklist.
     */
    private void setUpChecklist()
    {
        checklistDivider.addStyleName("left-bar-child-divider");
        leftBarPanel.add(checklistDivider);
        leftBarPanel.add(checklistPanel);

        checklistPanel.addTask(new Task("Basic Information",
                "Upload your avatar and provide a short description of the group stream."),
                (group.getAvatarId() != null && group.getDescription() != null));

        checklistPanel.addTask(new Task("Overview",
                "Provide an overview of the group and the members you are looking to participate in the stream."),
                (group.getOverview() != null));

        checklistPanel.addTask(new Task("Keywords",
                "List some keywords that describe the activity that will be posted to the stream."), (group
                .getCapabilities() != null && group.getCapabilities().size() > 0));
    }

    /**
     * Creates a new error report box and centers it on the page.
     * 
     * @return The error report box, ready to have content added to it.
     */
    private Panel addNewCenteredErrorBox()
    {
        // create panel
        Panel errorReport = new FlowPanel();
        errorReport.addStyleName("warning-report");

        // center on page
        FlowPanel centeringPanel = new FlowPanel();
        centeringPanel.addStyleName("warning-report-container");
        centeringPanel.add(errorReport);
        add(centeringPanel);

        return errorReport;
    }

    /**
     * Tell the user that the group does not exist.
     */
    private void showInvalidGroupMessage()
    {
        OrganizationModel.getInstance().fetch("", true);

        clear();
        Panel errorReport = addNewCenteredErrorBox();

        FlowPanel msgPanel = new FlowPanel();

        Label msgHeader = new Label("Profile not found");
        msgHeader.addStyleName("warning-message");

        Hyperlink directoryLink = new Hyperlink("profiles", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ORGANIZATIONS, "")));

        Label msgText1 = new Label("The group profile you were looking for could not be found. Try browsing the  ");
        Label msgText2 = new Label(
                " or searching the profiles by entering the name in the \"search profiles\" box above.");
        FlowPanel msgText = new FlowPanel();
        msgText.add(msgText1);
        msgText.add(directoryLink);
        msgText.add(msgText2);
        msgText.addStyleName("error-message-text");

        msgPanel.add(msgHeader);
        msgPanel.add(msgText);

        errorReport.add(msgPanel);
    }

    /**
     * Tell the user that this group is restricted.
     * 
     * @param inGroup
     *            the restricted access group
     */
    private void showRestrictedGroupMessage(final DomainGroupModelView inGroup)
    {
        Panel errorReport = addNewCenteredErrorBox();
        errorReport.addStyleName("group-error-msg-panel");
        errorReport.addStyleName("private-group");

        Label title = new Label("Private Group");
        title.addStyleName("group-error-msg-panel-title");
        errorReport.add(title);

        Label message = new Label("To access this group you must request access from the group's coordinator.  "
                + "The group coordinator will respond to your request via email.");
        message.addStyleName("group-error-msg-panel-text");
        errorReport.add(message);

        final SpinnerLabelButton button = new SpinnerLabelButton(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                GroupMembershipRequestModel.getInstance().insert(inGroup.getShortName());
            }
        });
        button.addStyleName("request-access-button");
        errorReport.add(button);

        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(InsertedRequestForGroupMembershipResponseEvent.class,
                new Observer<InsertedRequestForGroupMembershipResponseEvent>()
                {
                    public void update(final InsertedRequestForGroupMembershipResponseEvent inArg1)
                    {
                        button.disable();
                        eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                                "Your request for access has been sent")));
                    }
                });
    }

    /**
     * Tell the user that this group is pending approval.
     * 
     */
    private void showPendingGroupMessage()
    {
        Panel errorReport = addNewCenteredErrorBox();
        errorReport.addStyleName("group-error-msg-panel");
        errorReport.addStyleName("pending-group");

        Label title = new Label("Pending Group");
        title.addStyleName("group-error-msg-panel-title");
        errorReport.add(title);

        Label message = new Label(
                "This group is awaiting an organization coordinator to approve it.  Please try again later.");
        message.addStyleName("group-error-msg-panel-text");
        errorReport.add(message);
    }

    /**
     * Builds the admin tab.
     * 
     * @return The tab.
     */
    @SuppressWarnings("unchecked")
    private SimpleTab buildAdminTab()
    {
        final EventBus eventBus = Session.getInstance().getEventBus();
        final String membershipRequestsFilterName = "Membership Requests";

        // set up the tab itself
        final PagedListPanel adminTabContent = new PagedListPanel("admin", new SingleColumnPagedListRenderer());
        final SimpleTab adminTab = new SimpleTab("Admin", adminTabContent);

        // wire up the data retrieval events
        eventBus.addObserver(GotRequestForGroupMembershipResponseEvent.class,
                new Observer<GotRequestForGroupMembershipResponseEvent>()
                {
                    public void update(final GotRequestForGroupMembershipResponseEvent event)
                    {
                        if (membershipRequestsFilterName.equals(adminTabContent.getCurrentFilter()))
                        {
                            adminTabContent.render(event.getResponse(), "No membership requests.");
                        }
                        membershipRequestsCount = event.getResponse().getTotal();
                        adminTabContent.setFilterTitle(membershipRequestsFilterName, membershipRequestsFilterName
                                + " (" + membershipRequestsCount + ")");
                        adminTab.setTitle("Admin (" + (membershipRequestsCount) + ")");
                    }
                });

        // request the non-active "filters" to get the counts
        // Note: if there was more than one list on this tab with counts, we would need to trigger all of the lists to
        // be loaded so that we could get their counts. per example here:
        // PendingGroupsModel.getInstance().fetch(new GetPendingGroupsRequest(org.getShortName(), 0, 1), false);

        // reload list when an item is removed (this handles pulling from next page and pulling in new requests)
        eventBus.addObservers(new Observer()
        {
            public void update(final Object inArg1)
            {
                adminTabContent.reload();
            }

        }, DeletedRequestForGroupMembershipResponseEvent.class, InsertedGroupMemberResponseEvent.class);

        // prepare the "filters"
        // membership request "filter"
        adminTabContent.addSet(membershipRequestsFilterName, GroupMembershipRequestModel.getInstance(),
                new PersonRequestingGroupMembershipRenderer(group.getId(), group.getShortName()),
                new GetRequestForGroupMembershipRequest(group.getId(), 0, 0));

        return adminTab;
    }
}
