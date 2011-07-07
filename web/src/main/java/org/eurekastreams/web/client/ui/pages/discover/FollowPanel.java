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
package org.eurekastreams.web.client.ui.pages.discover;

import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.FollowerStatusable;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Panel to show the following status of a person or group, allowing the user to update the status.
 */
public class FollowPanel extends FlowPanel
{
    /**
     * Constructor.
     *
     * @param inFollowable
     *            the stream to show the follow widget for.
     */
    public FollowPanel(final FollowerStatusable inFollowable)
    {
        FollowerStatus status = inFollowable.getFollowerStatus();

        if (status != null)
        {
            final Label unfollowLink = new Label("");
            unfollowLink.setVisible(false);
            final Label followLink = new Label("");
            followLink.setVisible(false);

            unfollowLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().unFollowLink());
            unfollowLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().followLink());
            unfollowLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    SetFollowingStatusRequest request = new SetFollowingStatusRequest(Session.getInstance()
                            .getCurrentPerson().getAccountId(), inFollowable.getUniqueId(), inFollowable
                            .getEntityType(), false, Follower.FollowerStatus.NOTFOLLOWING);

                    if (inFollowable.getEntityType() == EntityType.PERSON)
                    {
                        PersonFollowersModel.getInstance().insert(request);
                    }
                    else if (inFollowable.getEntityType() == EntityType.GROUP)
                    {
                        GroupMembersModel.getInstance().insert(request);
                    }
                    else
                    {
                        Window.alert("Unsupported");
                    }

                    unfollowLink.setVisible(false);
                    followLink.setVisible(true);
                }
            });

            this.add(unfollowLink);

            followLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().followLink());
            followLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    SetFollowingStatusRequest request = new SetFollowingStatusRequest(Session.getInstance()
                            .getCurrentPerson().getAccountId(), inFollowable.getUniqueId(), inFollowable
                            .getEntityType(), false, Follower.FollowerStatus.FOLLOWING);

                    if (inFollowable.getEntityType() == EntityType.PERSON)
                    {
                        PersonFollowersModel.getInstance().insert(request);
                    }
                    else if (inFollowable.getEntityType() == EntityType.GROUP)
                    {
                        GroupMembersModel.getInstance().insert(request);
                    }
                    else
                    {
                        Window.alert("Unsupported");
                    }

                    unfollowLink.setVisible(true);
                    followLink.setVisible(false);
                }
            });

            this.add(followLink);

            switch (status)
            {
            case FOLLOWING:
                unfollowLink.setVisible(true);
                break;
            case NOTFOLLOWING:
                followLink.setVisible(true);
                break;
            default:
                break;
            }
        }

    }
}
