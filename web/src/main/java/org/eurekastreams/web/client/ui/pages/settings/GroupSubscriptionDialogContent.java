/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotGroupActivitySubscriptionsResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonJoinedGroupsResponseEvent;
import org.eurekastreams.web.client.events.data.StreamActivitySubscriptionChangedEvent;
import org.eurekastreams.web.client.model.GroupActivitySubscriptionModel;
import org.eurekastreams.web.client.model.PersonJoinedGroupsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.GroupPanel;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays the list of groups a user has joined and allows the user to elect whether to receive notifications for them.
 */
public class GroupSubscriptionDialogContent extends BaseDialogContent
{
    /** The main content panel. */
    private FlowPanel mainPanel;

    /** The panel containing the list of groups. */
    private FlowPanel listPanel;

    /** List of groups user is a member of. */
    private PagedSet<DomainGroupModelView> groups;

    /** IDs of groups for which user chose to subscribe to notifications. */
    private ArrayList<String> subscribedGroupIds;

    /** Subscribe buttons per group. */
    private final HashMap<String, Widget> subscribeButtons = new HashMap<String, Widget>();

    /** Unsubscribe buttons per group. */
    private final HashMap<String, Widget> unsubscribeButtons = new HashMap<String, Widget>();

    /**
     * {@inheritDoc}
     */
    public String getTitle()
    {
        return "Groups you have joined";
    }

    /**
     * {@inheritDoc}
     */
    public Widget getBody()
    {
        if (mainPanel == null)
        {
            mainPanel = new FlowPanel();

            Label label = new Label("Subscribe to the groups you wish to receive notifications from "
                    + "and unsubscribe from those you don't.");
            label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().groupNotifSubscriptionHelpText());
            mainPanel.add(label);

            listPanel = new FlowPanel();
            listPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().groupNotifSubscriptionPanel());
            mainPanel.add(listPanel);

            SimplePanel waitSpinner = new SimplePanel();
            waitSpinner.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitSpinner());
            listPanel.add(waitSpinner);
        }
        return mainPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show()
    {
        final EventBus eventBus = Session.getInstance().getEventBus();
        if (groups == null)
        {
            eventBus.addObserver(GotPersonJoinedGroupsResponseEvent.class,
                    new Observer<GotPersonJoinedGroupsResponseEvent>()
                    {
                        public void update(final GotPersonJoinedGroupsResponseEvent event)
                        {
                            eventBus.removeObserver(event, this);

                            groups = event.getResponse();
                            if (subscribedGroupIds != null)
                            {
                                populate();
                            }
                        }
                    });
            PersonJoinedGroupsModel.getInstance().fetch(
                    new GetFollowersFollowingRequest(EntityType.GROUP, Session.getInstance().getCurrentPerson()
                            .getAccountId(), 0, Integer.MAX_VALUE), true);
        }
        if (subscribedGroupIds == null)
        {
            eventBus.addObserver(GotGroupActivitySubscriptionsResponseEvent.class,
                    new Observer<GotGroupActivitySubscriptionsResponseEvent>()
                    {
                        public void update(final GotGroupActivitySubscriptionsResponseEvent event)
                        {
                            eventBus.removeObserver(event, this);

                            subscribedGroupIds = event.getResponse();
                            if (groups != null)
                            {
                                populate();
                            }
                        }
                    });
            GroupActivitySubscriptionModel.getInstance().fetch(null, false);
        }
    }

    /**
     * Builds the content from the two lists.
     */
    private void populate()
    {

        Session.getInstance()
                .getEventBus()
                .addObserver(StreamActivitySubscriptionChangedEvent.class,
                        new Observer<StreamActivitySubscriptionChangedEvent>()
                        {
                            public void update(final StreamActivitySubscriptionChangedEvent ev)
                            {
                                String groupName = ev.getResponse().getStreamEntityUniqueId();
                                boolean subscribed = ev.getResponse().getReceiveNewActivityNotifications();
                                Widget button;
                                button = subscribeButtons.get(groupName);
                                if (button != null)
                                {
                                    button.setVisible(!subscribed);
                                }
                                button = unsubscribeButtons.get(groupName);
                                if (button != null)
                                {
                                    button.setVisible(subscribed);
                                }
                            }
                        });

        // remove spinner
        listPanel.clear();

        // display message if no groups
        if (groups.getPagedSet().isEmpty())
        {
            Label label = new Label("You are not a member of any groups.");
            label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemEmpty());
            listPanel.add(label);
            return;
        }

        // sort groups by name
        TreeSet<DomainGroupModelView> orderedGroups = new TreeSet<DomainGroupModelView>(
                new Comparator<DomainGroupModelView>()
                {
                    public int compare(final DomainGroupModelView inO1, final DomainGroupModelView inO2)
                    {
                        return inO1.getName().compareToIgnoreCase(inO2.getName());
                    }
                });
        orderedGroups.addAll(groups.getPagedSet());

        // display groups
        for (final DomainGroupModelView group : orderedGroups)
        {
            GroupPanel groupWidget = new GroupPanel(group, false, true, false);

            final Label subscribeButton = new Label();
            subscribeButtons.put(group.getUniqueId(), subscribeButton);
            final Label unsubscribeButton = new Label();
            unsubscribeButtons.put(group.getUniqueId(), unsubscribeButton);

            subscribeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().groupNotifSubscribeButton());
            unsubscribeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().groupNotifUnsubscribeButton());

            subscribeButton.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent inArg0)
                {
                    GroupActivitySubscriptionModel.getInstance().insert(group.getUniqueId());
                }
            });
            unsubscribeButton.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent inArg0)
                {
                    GroupActivitySubscriptionModel.getInstance().delete(group.getUniqueId());
                }
            });

            boolean subscribed = subscribedGroupIds.contains(group.getUniqueId());
            subscribeButton.setVisible(!subscribed);
            unsubscribeButton.setVisible(subscribed);

            groupWidget.insert(subscribeButton, 0);
            groupWidget.insert(unsubscribeButton, 1);

            listPanel.add(groupWidget);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getCssName()
    {
        return StaticResourceBundle.INSTANCE.coreCss().groupNotifSubscriptionDialog();
    }
}
