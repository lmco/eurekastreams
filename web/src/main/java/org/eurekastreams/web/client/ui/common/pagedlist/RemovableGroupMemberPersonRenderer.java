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

import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.PersonPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders a person with an extra link to let them be removed from a group.
 */
public class RemovableGroupMemberPersonRenderer implements ItemRenderer<PersonModelView>
{
    /** Unique ID of group. */
    private String groupUniqueId;

    /**
     * Constructor.
     *
     * @param inGroupUniqueId
     *            Unique ID of group user is a member of.
     */
    public RemovableGroupMemberPersonRenderer(final String inGroupUniqueId)
    {
        groupUniqueId = inGroupUniqueId;
    }

    /**
     * {@inheritDoc}
     */
    public Panel render(final PersonModelView item)
    {
        PersonPanel panel = new PersonPanel(item, true, true, false, true);

        // don't allow user to delete themselves
        if (Session.getInstance().getCurrentPerson().getId() != item.getEntityId())
        {
            panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().removablePerson());

            Label deleteLink = new Label("Delete");
            deleteLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
            deleteLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().delete());
            panel.add(deleteLink);

            deleteLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent inArg0)
                {
                    if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to remove " + item.getDisplayName()
                            + " from this group?"))
                    {
                        GroupMembersModel.getInstance().delete(
                                new SetFollowingStatusRequest(item.getAccountId(), groupUniqueId, EntityType.GROUP,
                                        false, FollowerStatus.NOTFOLLOWING));
                    }
                }
            });
        }

        return panel;
    }
}
