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
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Like count widget.
 */
public class LikeCountWidget extends Composite
{
    /**
     * Main widget.
     */
    private FocusPanel widget = new FocusPanel();

    /**
     * Shows users who have liked this.
     */
    private static FlowPanel usersWhoLikedPanel = new FlowPanel();

    /**
     * Shows users who have liked this.
     */
    private static FlowPanel usersWhoLikedPanelWrapper;

    /**
     * Added to root panel.
     */
    private static boolean hasUsersWhoLikedBeenAddedToRoot = false;

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
    private static AvatarRenderer avatarRenderer = new AvatarRenderer();

    /**
     * Liked label.
     */
    private static Label likedLabel = new Label();

    /**
     * Avatar panel.
     */
    private static FlowPanel avatarPanel = new FlowPanel();

    /**
     * Likers.
     */
    private List<PersonModelView> likers;

    /**
     * Link for the like count link.
     */
    private static Anchor innerLikeCountLink = new Anchor();
    /**
     * The view all link.
     */
    private static Anchor viewAll = new Anchor("view all");
    /**
     * The actiivty Id.
     */
    private static Long activityId;
    /**
     * The body.
     */
    private static FlowPanel userLikedBody = new FlowPanel();
    /**
     * How many do we show before the view all link shows up.
     */
    private static final int MAXLIKERSSHOWN = 10;

    /**
     * Setup the floating avatar panel.
     */
    private static void setup()
    {

        // Reimplementing Focus panel, GWT seems to break otherwise.
        usersWhoLikedPanelWrapper = new FlowPanel()
        {
            private final TimerFactory timerFactory = new TimerFactory();
            private boolean actuallyOut = false;
            private static final int TIMER_EXPIRATION = 500;

            @Override
            public void onBrowserEvent(final Event event)
            {
                super.onBrowserEvent(event);

                if (DOM.eventGetType(event) == Event.ONMOUSEOUT)
                {
                    actuallyOut = true;

                    timerFactory.runTimer(TIMER_EXPIRATION, new TimerHandler()
                    {
                        public void run()
                        {
                            if (actuallyOut)
                            {
                                EffectsFacade.nativeFadeOut(usersWhoLikedPanelWrapper.getElement(), false);
                            }

                        }
                    });
                }
                else
                {
                    actuallyOut = false;
                }
            }

        };

        viewAll.setVisible(false);
        viewAll.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                DialogContent dialogContent = new LikersDialogContent(activityId);
                Dialog dialog = new Dialog(dialogContent);
                dialog.setBgVisible(true);
                dialog.center();
            }

        });
        usersWhoLikedPanelWrapper.setVisible(false);
        usersWhoLikedPanelWrapper.addStyleName("users-who-liked-activity-wrapper like-count-widget");
        RootPanel.get().add(usersWhoLikedPanelWrapper);

        final FlowPanel innerLikeCountPanel = new FlowPanel();
        innerLikeCountPanel.addStyleName("like-count");
        innerLikeCountPanel.add(innerLikeCountLink);

        usersWhoLikedPanelWrapper.add(innerLikeCountPanel);

        usersWhoLikedPanelWrapper.add(usersWhoLikedPanel);
        usersWhoLikedPanel.addStyleName("users-who-liked-activity");

        FlowPanel userLikedHeader = new FlowPanel();
        userLikedHeader.addStyleName("users-who-liked-activity-header");
        usersWhoLikedPanel.add(userLikedHeader);

        userLikedBody.addStyleName("users-who-liked-activity-body");
        usersWhoLikedPanel.add(userLikedBody);

        FlowPanel userLikedFooter = new FlowPanel();
        userLikedFooter.addStyleName("users-who-liked-activity-footer");
        usersWhoLikedPanel.add(userLikedFooter);

        userLikedBody.add(likedLabel);
        userLikedBody.add(avatarPanel);
        userLikedBody.add(viewAll);
        usersWhoLikedPanelWrapper.sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER);

        hasUsersWhoLikedBeenAddedToRoot = true;
    }

    /**
     * Constructor.
     *
     * @param inActivityId
     *            activity id.
     * @param inLikeCount
     *            like count.
     * @param inLikers
     *            who has liked this activity.
     */
    public LikeCountWidget(final Long inActivityId, final Integer inLikeCount, final List<PersonModelView> inLikers)
    {

        initWidget(widget);

        if (!hasUsersWhoLikedBeenAddedToRoot)
        {
            setup();
        }

        widget.addMouseOverHandler(new MouseOverHandler()
        {
            public void onMouseOver(final MouseOverEvent arg0)
            {
                activityId = inActivityId;
                viewAll.setVisible(false);
                avatarPanel.clear();
                DOM.setStyleAttribute(usersWhoLikedPanelWrapper.getElement(), "top", likeCountLink.getAbsoluteTop()
                        + "px");
                DOM.setStyleAttribute(usersWhoLikedPanelWrapper.getElement(), "left", likeCountLink.getAbsoluteLeft()
                        + "px");

                for (PersonModelView liker : likers)
                {
                    Widget avatar = avatarRenderer.render(liker.getId(), liker.getAvatarId());
                    avatar.addStyleName("avatar-image-VerySmall");

                    avatar.setTitle(liker.getDisplayName());

                    avatarPanel.add(avatar);
                }

                if (likeCount > MAXLIKERSSHOWN)
                {
                    viewAll.setVisible(true);
                }
                likedLabel.setText(likeCount + " people liked this");
                innerLikeCountLink.setText(likeCount.toString());

                EffectsFacade.nativeFadeIn(usersWhoLikedPanelWrapper.getElement(), false);
            }
        });

        likers = inLikers;
        widget.addStyleName("like-count-widget");
        likeCount = inLikeCount;

        final FlowPanel likeCountPanel = new FlowPanel();
        likeCountPanel.addStyleName("like-count");
        likeCountPanel.add(likeCountLink);

        widget.add(likeCountPanel);

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
                    EffectsFacade.nativeFadeIn(widget.getElement(), true);
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

        likeCountLink.setText(likeCount.toString());

        widget.setVisible(likeCount > 0);
    }
}
