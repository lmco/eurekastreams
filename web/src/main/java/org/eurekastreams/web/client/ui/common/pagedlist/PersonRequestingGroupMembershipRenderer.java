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
package org.eurekastreams.web.client.ui.common.pagedlist;

import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.GroupMembershipRequestModel;
import org.eurekastreams.web.client.ui.common.PersonPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders people who are requesting group membership.
 */
public class PersonRequestingGroupMembershipRenderer implements ItemRenderer<PersonModelView>
{
    /** Group id for group renderer is used for. */
    private long groupId;

    /** Group shortname for group renderer is used for. */
    private String groupShortname;

    /**
     * Constructor.
     *
     * @param inGroupId
     *            Group id for group renderer is used for.
     * @param inGroupShortname
     *            Group shortname for group renderer is used for.
     */
    public PersonRequestingGroupMembershipRenderer(final long inGroupId, final String inGroupShortname)
    {
        groupId = inGroupId;
        groupShortname = inGroupShortname;
    }

    /**
     * {@inheritDoc}
     */
    public Panel render(final PersonModelView item)
    {
        Panel panel = new FlowPanel();
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().pendingGroupMembershipItem());

        // -- build UI --

        // buttons panel (left side)

        final Panel buttonsPanel = new FlowPanel();
        buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().pendingButtons());

        final Label approveButton = new Label("Approve");
        approveButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().approveButton());
        buttonsPanel.add(approveButton);

        final Label denyButton = new Label("Deny");
        denyButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().denyButton());
        buttonsPanel.add(denyButton);

        // person panel (right side)
        panel.add(new PersonPanel(item, false, true));
        panel.add(buttonsPanel);

        // -- wire events --

        approveButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitActive());

                GroupMembersModel.getInstance().insert(
                        new SetFollowingStatusRequest(item.getAccountId(), groupShortname, EntityType.GROUP, false,
                                Follower.FollowerStatus.FOLLOWING));
            }
        });

        denyButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to deny this user membership?"))
                {
                    buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitActive());

                     GroupMembershipRequestModel.getInstance().delete(
                            new RequestForGroupMembershipRequest(groupId, item.getEntityId()));
                }
            }
        });

        return panel;
    }
}
