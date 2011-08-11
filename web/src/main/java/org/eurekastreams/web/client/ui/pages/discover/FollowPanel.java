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
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.FollowerStatusable;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.GroupMembershipRequestModel;
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
     * The unfollow link.
     */
    private final Label unfollowLink = new Label("");

    /**
     * The follow link.
     */
    private final Label followLink = new Label("");

    /**
     * A style name to switch this state to after the user requests access to a private group.
     */
    private String pendingGroupJoinedStateStyleName;

    /**
     * Constructor.
     * 
     * @param inFollowable
     *            the stream to show the follow widget for.
     */
    public FollowPanel(final FollowerStatusable inFollowable)
    {
        this(inFollowable, null, StaticResourceBundle.INSTANCE.coreCss().unFollowLink(), StaticResourceBundle.INSTANCE
                .coreCss().followLink(), false, (ClickHandler) null, (String) null);
    }

    /**
     * Constructor.
     * 
     * @param inFollowable
     *            the stream to show the follow widget for.
     * @param followStyle
     *            Style for the follow button.
     * @param unfollowStyle
     *            Style for the unfollow button.
     * @param commonStyle
     *            Style for both buttons.
     * @param showTooltips
     *            Show tooltips.
     * @param inPendingGroupJoinedStateStyleName
     *            style name to show pending group join
     */
    public FollowPanel(final FollowerStatusable inFollowable, final String followStyle, final String unfollowStyle,
            final String commonStyle, final boolean showTooltips, final String inPendingGroupJoinedStateStyleName)
    {
        this(inFollowable, followStyle, unfollowStyle, commonStyle, showTooltips, (ClickHandler) null,
                inPendingGroupJoinedStateStyleName);
        pendingGroupJoinedStateStyleName = inPendingGroupJoinedStateStyleName;
    }

    /**
     * Constructor.
     * 
     * @param inFollowable
     *            the stream to show the follow widget for.
     * @param followStyle
     *            Style for the follow button.
     * @param unfollowStyle
     *            Style for the unfollow button.
     * @param commonStyle
     *            Style for both buttons.
     * @param showTooltips
     *            Show tooltips.
     * @param onFollowHandler
     *            additional handler to call on follow.
     * @param inPendingGroupJoinedStateStyleName
     *            style name to show pending group join
     */
    public FollowPanel(final FollowerStatusable inFollowable, final String followStyle, final String unfollowStyle,
            final String commonStyle, final boolean showTooltips, final ClickHandler onFollowHandler,
            final String inPendingGroupJoinedStateStyleName)
    {
        FollowerStatus status = inFollowable.getFollowerStatus();

        if (status == null)
        {
            return;
        }

        unfollowLink.setVisible(false);
        followLink.setVisible(false);

        if (showTooltips)
        {
            followLink.setTitle("Follow this stream");
            unfollowLink.setTitle("Stop following this stream");
        }

        if (unfollowStyle != null)
        {
            unfollowLink.addStyleName(unfollowStyle);
        }
        if (commonStyle != null)
        {
            unfollowLink.addStyleName(commonStyle);
        }
        unfollowLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                SetFollowingStatusRequest request = new SetFollowingStatusRequest(Session.getInstance()
                        .getCurrentPerson().getAccountId(), inFollowable.getUniqueId(), inFollowable.getEntityType(),
                        false, Follower.FollowerStatus.NOTFOLLOWING);

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

        if (followStyle != null)
        {
            followLink.addStyleName(followStyle);
        }
        if (commonStyle != null)
        {
            followLink.addStyleName(commonStyle);
        }
        followLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                SetFollowingStatusRequest request = new SetFollowingStatusRequest(Session.getInstance()
                        .getCurrentPerson().getAccountId(), inFollowable.getUniqueId(), inFollowable.getEntityType(),
                        false, Follower.FollowerStatus.FOLLOWING);

                if (inFollowable.getEntityType() == EntityType.PERSON)
                {
                    PersonFollowersModel.getInstance().insert(request);
                }
                else if (inFollowable.getEntityType() == EntityType.GROUP)
                {
                    if (inFollowable instanceof DomainGroupModelView
                            && !((DomainGroupModelView) inFollowable).isPublic()
                            && !Session.getInstance().getCurrentPerson().getRoles().contains(Role.SYSTEM_ADMIN))
                    {
                        // private group, we're not admin - request access
                        GroupMembershipRequestModel.getInstance().insert(inFollowable.getUniqueId());

                        if (pendingGroupJoinedStateStyleName != null)
                        {
                            // the page gave us a style to switch to to show pending state
                            if (followLink != null)
                            {
                                followLink.setVisible(false);
                            }
                            if (unfollowLink != null)
                            {
                                unfollowLink.setVisible(false);
                            }
                            clear();
                            Label newLabel = new Label("");
                            newLabel.setStyleName(pendingGroupJoinedStateStyleName);
                            add(newLabel);
                        }
                    }
                    else
                    {
                        // public group - join
                        GroupMembersModel.getInstance().insert(request);
                    }
                }

                unfollowLink.setVisible(true);
                followLink.setVisible(false);
            }
        });

        if (null != onFollowHandler)
        {
            followLink.addClickHandler(onFollowHandler);
        }

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
