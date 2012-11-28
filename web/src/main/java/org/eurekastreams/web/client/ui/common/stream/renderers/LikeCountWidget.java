/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.ActivityLikedChangeEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.model.ActivityLikeModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

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

/**
 * Like count widget.
 */
public class LikeCountWidget extends Composite
{
    /**
     * Main widget.
     */
    private final FocusPanel widget = new FocusPanel();

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
    private final List<PersonModelView> likers;

    /**
     * Liker overflow.
     */
    private PersonModelView likerOverflow;

    /**
     * Activity ID.
     */
    private final Long thisActivityId;

    /**
     * Liked status.
     */
    private Boolean thisIsLiked;

    /**
     * The current panel.
     */
    private static LikeCountWidget currentPanel;

    /**
     * Link for the like count link.
     */
    private static Anchor innerLikeCountLink = new Anchor();
    /**
     * The view all link.
     */
    private static Anchor viewAll = new Anchor("view all");

    /**
     * Current activity ID being shown.
     */
    private static Long currentActivityId = 0L;

    /**
     * Current activity like status..
     */
    private static Boolean isLiked;

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
            private static final int TIMER_EXPIRATION = 250;

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
                                usersWhoLikedPanelWrapper.setVisible(false);
                            }

                        }
                    });
                }
                else if (DOM.eventGetType(event) == Event.ONMOUSEOVER)
                {
                    actuallyOut = false;
                }
                else if (DOM.eventGetType(event) == Event.ONCLICK)
                {
                    usersWhoLikedPanelWrapper.setVisible(false);
                }
            }
        };

        final Anchor transparentLikeLink = new Anchor();
        transparentLikeLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().transparentLikeLink());

        transparentLikeLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                if (!isLiked)
                {
                    ActivityLikeModel.getInstance().update(
                            new SetActivityLikeRequest(currentActivityId, LikeActionType.ADD_LIKE));

                    arg0.stopPropagation();
                }
            }
        });

        usersWhoLikedPanelWrapper.add(transparentLikeLink);

        viewAll.setVisible(false);
        viewAll.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                DialogContent dialogContent = new LikersDialogContent(currentActivityId);
                Dialog dialog = new Dialog(dialogContent);
                dialog.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likerModal());
                dialog.showCentered();
            }

        });

        usersWhoLikedPanelWrapper.setVisible(false);
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityWrapper());
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likeCountWidget());
        RootPanel.get().add(usersWhoLikedPanelWrapper);

        final FocusPanel innerLikeCountPanel = new FocusPanel();
        innerLikeCountPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likeCount());
        innerLikeCountPanel.add(innerLikeCountLink);

        usersWhoLikedPanelWrapper.add(innerLikeCountPanel);

        usersWhoLikedPanelWrapper.add(usersWhoLikedPanel);
        usersWhoLikedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivity());

        FlowPanel userLikedHeader = new FlowPanel();
        userLikedHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityHeader());
        usersWhoLikedPanel.add(userLikedHeader);

        userLikedBody.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityBody());
        usersWhoLikedPanel.add(userLikedBody);

        FlowPanel userLikedFooter = new FlowPanel();
        userLikedFooter.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityFooter());
        usersWhoLikedPanel.add(userLikedFooter);

        userLikedBody.add(likedLabel);
        userLikedBody.add(avatarPanel);
        userLikedBody.add(viewAll);
        usersWhoLikedPanelWrapper.sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);

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
     * @param inIsLiked
     *            if the person has liked the current activity.
     */
    public LikeCountWidget(final Long inActivityId, final Integer inLikeCount, final List<PersonModelView> inLikers,
            final Boolean inIsLiked)
    {
        thisActivityId = inActivityId;
        thisIsLiked = inIsLiked;

        initWidget(widget);

        if (!hasUsersWhoLikedBeenAddedToRoot)
        {
            setup();
        }

        widget.addMouseOverHandler(new MouseOverHandler()
        {
            public void onMouseOver(final MouseOverEvent arg0)
            {
                showPanel();

                usersWhoLikedPanelWrapper.setVisible(true);
            }
        });

        likers = inLikers;
        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likeCountWidget());
        likeCount = inLikeCount;

        final FlowPanel likeCountPanel = new FlowPanel();
        likeCountPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likeCount());
        likeCountPanel.add(likeCountLink);

        widget.add(likeCountPanel);

        EventBus.getInstance().addObserver(ActivityLikedChangeEvent.class, new Observer<ActivityLikedChangeEvent>()
        {
            public void update(final ActivityLikedChangeEvent event)
            {
                if (event.getActivityId().equals(inActivityId))
                {
                    updatePanel(event.getActionType());
                    currentPanel.showPanel();
                }
            }
        });

        updatePanel(null);
    }

    /**
     * Called to update the panel.
     */
    private void showPanel()
    {
        currentPanel = this;
        isLiked = thisIsLiked;
        currentActivityId = thisActivityId;
        viewAll.setVisible(false);
        avatarPanel.clear();
        DOM.setStyleAttribute(usersWhoLikedPanelWrapper.getElement(), "top", likeCountLink.getAbsoluteTop() + "px");
        DOM.setStyleAttribute(usersWhoLikedPanelWrapper.getElement(), "left", likeCountLink.getAbsoluteLeft() + "px");

        for (PersonModelView liker : likers)
        {
            avatarPanel.add(AvatarLinkPanel.create(liker, Size.VerySmall));
        }

        if (likeCount > MAXLIKERSSHOWN)
        {
            viewAll.setVisible(true);
        }
        if (likeCount == 1)
        {
            likedLabel.setText(likeCount + " person liked this");
        }
        else
        {
            likedLabel.setText(likeCount + " people liked this");
        }
        innerLikeCountLink.setText(likeCount.toString());
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
            thisIsLiked = !thisIsLiked;
            if (likeActionType == LikeActionType.ADD_LIKE)
            {
                // if user is already in list, then don't add again. This can happen if the server is slow. Since it is
                // the response from the server to a like request that "disables" the like button, the user can continue
                // to click until the response comes back. Each click would cause another like request to be sent, and
                // each will eventually come back with success.
                boolean ignore = false;
                long id = Session.getInstance().getCurrentPerson().getId();
                for (PersonModelView liker : likers)
                {
                    if (liker.getId() == id)
                    {
                        // could just do a return here, but if we're in this state, it's probably good to insure the
                        // text and visibility are set correctly (as done at the bottom of the method)
                        ignore = true;
                        break;
                    }
                }
                if (!ignore)
                {
                    PersonModelView currentPerson = new PersonModelView();
                    currentPerson.setEntityId(Session.getInstance().getCurrentPerson().getId());
                    currentPerson.setAvatarId(Session.getInstance().getCurrentPerson().getAvatarId());
                    currentPerson.setDisplayName(Session.getInstance().getCurrentPerson().getDisplayName());
                    currentPerson.setAccountId(Session.getInstance().getCurrentPerson().getAccountId());

                    if (likers.size() >= MAXLIKERSSHOWN)
                    {
                        likerOverflow = likers.get(MAXLIKERSSHOWN - 1);
                        likers.remove(MAXLIKERSSHOWN - 1);
                    }

                    likers.add(0, currentPerson);

                    likeCount++;

                    if (!widget.isVisible())
                    {
                        EffectsFacade.nativeFadeIn(widget.getElement(), true);
                    }
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

                        if (likerOverflow != null)
                        {
                            likers.add(9, likerOverflow);
                        }
                    }
                }

                likeCount--;
            }
        }

        likeCountLink.setText(likeCount.toString());

        widget.setVisible(likeCount > 0);
    }
}
