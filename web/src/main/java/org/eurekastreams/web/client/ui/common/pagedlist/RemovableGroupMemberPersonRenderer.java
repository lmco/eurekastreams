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

import java.util.List;

import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.domain.EntityType;
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

    /** The group. */
    private DomainGroupModelView group;

    /**
     * Constructor.
     * 
     * @param inGroupUniqueId
     *            Unique ID of group user is a member of.
     * 
     * @param inGroup
     *            The group for which this Person is getting rendered.
     */
    public RemovableGroupMemberPersonRenderer(final String inGroupUniqueId, final DomainGroupModelView inGroup)
    {
        groupUniqueId = inGroupUniqueId;
        group = inGroup;
    }

    /**
     * {@inheritDoc}
     */
    public Panel render(final PersonModelView item)
    {
        PersonPanel panel = new PersonPanel(item, false, false, false);

        boolean currentUserIsAdmin = Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN);

        boolean currentUserIsGroupCoordinator = isGroupCoordinator(Session.getInstance().getCurrentPerson());

        // conditions by which a person should show up as 'Remove'-able:
        // cannot delete himself AND private group AND (current user is ADMIN or GROUP COORD)
        if ((Session.getInstance().getCurrentPerson().getId() != item.getEntityId())
                && (currentUserIsAdmin || currentUserIsGroupCoordinator) && (!group.isPublic()))
        {
            boolean toBeRemovedFollowerIsGroupCoordinator = isGroupCoordinator(item);

            int numberOfGroupCoordinators = group.getCoordinators().size();

            // Cannot remove Group Coordinator if he/she is the last Group Coordinator.
            if (toBeRemovedFollowerIsGroupCoordinator && (numberOfGroupCoordinators == 1))
            {
                // short-circuit as this Person is non-removable
                return panel;
            }

            panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().removablePerson());

            Label deleteLink = new Label("Remove");
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
                                        false, FollowerStatus.NOTFOLLOWING, group.getShortName()));
                    }
                }
            });
        }

        return panel;
    }

    /**
     * Checks whether the current user is a Group Coordinator.
     * 
     * @param toBeRemovedPerson
     *            - Group Coordinator to be removed.
     * 
     * @return Whether the current user is a Group Coordinator
     */
    private boolean isGroupCoordinator(final PersonModelView toBeRemovedPerson)
    {
        String currentUserAccountId = toBeRemovedPerson.getAccountId();

        List<PersonModelView> groupCoordinators = group.getCoordinators();

        for (PersonModelView p : groupCoordinators)
        {
            if (p.getAccountId().equals(currentUserAccountId))
            {
                return true;
            }
        }

        return false;
    }
}
