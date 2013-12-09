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
package org.eurekastreams.web.client.ui.common.pagedlist;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.action.request.profile.ReviewPendingGroupRequest;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.UpdatedReviewPendingGroupResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.PendingGroupsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Render a Pending Group.
 *
 */
public class PendingGroupRenderer implements ItemRenderer<DomainGroupModelView>
{
    /** Bullet character. */
    private static final String BULLET = "\u2219";

    /**
     * {@inheritDoc}
     */
    public Panel render(final DomainGroupModelView group)
    {
        final FlowPanel groupPanel = new FlowPanel();
        groupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().group());
        groupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().directoryItem());
        groupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        groupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().pendingGroup());

        // -- buttons panel (left side) --

        final Panel buttonsPanel = new FlowPanel();
        buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().pendingGroupButtons());
        groupPanel.add(buttonsPanel);

        final Label confirmButton = new Label("Confirm");
        confirmButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().approveButton());
        buttonsPanel.add(confirmButton);

        final Label denyButton = new Label("Deny");
        denyButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().denyButton());
        buttonsPanel.add(denyButton);

        // -- group info (right side) --

        FlowPanel groupAbout = new FlowPanel();
        groupAbout.addStyleName(StaticResourceBundle.INSTANCE.coreCss().description());
        groupPanel.add(groupAbout);

        Label groupName = new Label(group.getName());
        groupName.addStyleName(StaticResourceBundle.INSTANCE.coreCss().displayName());
        groupAbout.add(groupName);

        Label groupDescription = new Label(group.getDescription());
        groupDescription.addStyleName(StaticResourceBundle.INSTANCE.coreCss().pendingGroupDescription());
        groupAbout.add(groupDescription);

        FlowPanel groupMetaData = new FlowPanel();
        groupMetaData.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowers());
        groupMetaData.add(new InlineLabel(new DateFormatter().timeAgo(group.getDateAdded(), true)));
        insertActionSeparator(groupMetaData);
        groupMetaData.add(new InlineLabel("By: "));
        String url =
                Session.getInstance().generateUrl(
                        new CreateUrlRequest(Page.PEOPLE, group.getPersonCreatedByAccountId()));
        groupMetaData.add(new InlineHyperlink(group.getPersonCreatedByDisplayName(), url));
        
        // Display Business Area (BA) information 
        insertActionSeparator(groupMetaData);
        groupMetaData.add(new InlineLabel("BA: "));
        Label label = new InlineLabel(group.getPersonCreatedByCompanyName());
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowersData());
        groupMetaData.add(label);
        insertActionSeparator(groupMetaData);
        
        groupMetaData.add(new InlineLabel("Privacy Setting: "));
        label = new InlineLabel(group.isPublic() ? "Public" : "Private");
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowersData());
        groupMetaData.add(label);

        groupAbout.add(groupMetaData);

        // -- actions --

        confirmButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitActive());

                PendingGroupsModel.getInstance().update(new ReviewPendingGroupRequest(group.getShortName(), true));
            }
        });

        denyButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to deny creation of this group?"))
                {
                    buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitActive());

                    PendingGroupsModel.getInstance()
                            .update(new ReviewPendingGroupRequest(group.getShortName(), false));
                }
            }
        });

        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(UpdatedReviewPendingGroupResponseEvent.class,
                new Observer<UpdatedReviewPendingGroupResponseEvent>()
                {
                    public void update(final UpdatedReviewPendingGroupResponseEvent ev)
                    {
                        if (ev.getResponse().getGroupShortName().equals(group.getShortName()))
                        {
                            eventBus.removeObserver(ev, this);
                            String msg =
                                    ev.getResponse().getApproved() ? "The " + group.getName()
                                            + " group has been approved" : "The request to create the "
                                            + group.getName() + " group has been denied";
                            eventBus.notifyObservers(new ShowNotificationEvent(new Notification(msg)));
                        }
                    }
                });

        return groupPanel;
    }

    /**
     * Adds a separator (dot).
     *
     * @param panel
     *            Panel to put the separator in.
     */
    private void insertActionSeparator(final Panel panel)
    {
        Label sep = new InlineLabel(BULLET);
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }
}
