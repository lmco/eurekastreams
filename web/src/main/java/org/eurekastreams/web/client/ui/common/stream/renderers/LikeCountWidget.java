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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import java.util.List;

import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.ActivityLikedChangeEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Like count widget.
 */
public class LikeCountWidget extends Composite
{
    /**
     * Main widget.
     */
    private FlowPanel widget = new FlowPanel();

    /**
     * Shows users who have liked this.
     */
    private FlowPanel usersWhoLikedPanel = new FlowPanel();

    /**
     * Like count.
     */
    private Integer likeCount = 0;

    /**
     * Link for the like count link.
     */
    final Anchor likeCountLink = new Anchor();

    /**
     * Avatar renderer.
     */
    private AvatarRenderer avatarRenderer = new AvatarRenderer();

    /**
     * Liked label.
     */
    private Label likedLabel = new Label();

    /**
     * Avatar panel.
     */
    final FlowPanel avatarPanel = new FlowPanel();

    /**
     * Likers.
     */
    private List<PersonModelView> likers;

    /**
     * Constructor.
     * 
     * @param activityId
     *            activity id.
     * @param inLikeCount
     *            like count.
     * @param inLikers
     *            who has liked this activity.
     */
    public LikeCountWidget(final Long activityId, final Integer inLikeCount, final List<PersonModelView> inLikers)
    {
        likers = inLikers;
        widget.addStyleName("like-count-widget");
        likeCount = inLikeCount;
        initWidget(widget);

        final FlowPanel likeCountPanel = new FlowPanel();
        likeCountPanel.addStyleName("like-count");

        likeCountPanel.add(likeCountLink);

        widget.add(likeCountPanel);

        usersWhoLikedPanel.addStyleName("users-who-liked-activity");

        FlowPanel userLikedHeader = new FlowPanel();
        userLikedHeader.addStyleName("users-who-liked-activity-header");
        usersWhoLikedPanel.add(userLikedHeader);

        FlowPanel userLikedBody = new FlowPanel();
        userLikedBody.addStyleName("users-who-liked-activity-body");
        usersWhoLikedPanel.add(userLikedBody);

        FlowPanel userLikedFooter = new FlowPanel();
        userLikedFooter.addStyleName("users-who-liked-activity-footer");
        usersWhoLikedPanel.add(userLikedFooter);

        userLikedBody.add(likedLabel);
        userLikedBody.add(avatarPanel);

        widget.add(usersWhoLikedPanel);

        EventBus.getInstance().addObserver(ActivityLikedChangeEvent.class, new Observer<ActivityLikedChangeEvent>()
        {
            public void update(final ActivityLikedChangeEvent event)
            {
                if (event.getActivityId().equals(activityId))
                {
                    updatePanel(event.getActionType());
                }
            }
        });

        updatePanel(null);
    }

    /**
     * Update the panel.
     * 
     * @param likeActionType
     *            the action that's being taken.
     */
    private void updatePanel(final LikeActionType likeActionType)
    {
        avatarPanel.clear();

        if (null != likeActionType)
        {
            if (likeActionType == LikeActionType.ADD_LIKE)
            {
                PersonModelView currentPerson = new PersonModelView();
                currentPerson.setEntityId(Session.getInstance().getCurrentPerson().getId());
                currentPerson.setAvatarId(Session.getInstance().getCurrentPerson().getAvatarId());
                currentPerson.setDisplayName(Session.getInstance().getCurrentPerson().getDisplayName());

                likers.add(0, currentPerson);

                likeCount++;

                if (!widget.isVisible())
                {
                    EffectsFacade.nativeFadeIn(widget.getElement());
                }
            }
            else
            {
                for (PersonModelView liker : likers)
                {
                    if (likeActionType == LikeActionType.REMOVE_LIKE
                            && liker.getId() == Session.getInstance().getCurrentPerson().getId())
                    {
                        likers.remove(liker);
                    }
                }

                likeCount--;
            }
        }

        for (PersonModelView liker : likers)
        {
            Widget avatar = avatarRenderer.render(liker.getId(), liker.getAvatarId());
            avatar.addStyleName("avatar-image-VerySmall");

            avatar.setTitle(liker.getDisplayName());

            avatarPanel.add(avatar);
        }

        likeCountLink.setText(likeCount.toString());
        likedLabel.setText(likeCount + " people liked this");

        widget.setVisible(likeCount > 0);
    }
}
