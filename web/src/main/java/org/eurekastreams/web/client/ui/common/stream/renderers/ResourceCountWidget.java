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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.ResourceLikeChangeEvent;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.LikeResourceModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Like count widget.
 */
public class ResourceCountWidget extends Composite
{
    /**
     * How far down the popout arrow needs to be from the top of the bubble given the button it points to.
     */
    private static final int POPOUT_ARROW_TOP_OFFSET = -6;

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
     * Arrow pointing from the bubble to the button.
     */
    private static Label popoutArrow = new Label();

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
     * Liked status.
     */
    private Boolean thisIsLiked;

    /**
     * The current panel.
     */
    private static ResourceCountWidget currentPanel;

    /**
     * Link for the like count link.
     */
    private static Anchor innerLikeCountLink = new Anchor();
    /**
     * The view all link.
     */
    private static Anchor viewAll = new Anchor("view all");

    /**
     * The body.
     */
    private static FlowPanel userLikedBody = new FlowPanel();
    /**
     * How many do we show before the view all link shows up.
     */
    private static final int MAXLIKERSSHOWN = 4;

    /**
     * If the user has mouse over.
     */
    private static int hasMousedOver = 0;

    /**
     * Resouce url.
     */
    private static String resourceUrl;

    /**
     * Count type.
     */
    private static CountType countType;

    /**
     * Count Type.
     */
    public enum CountType
    {
        /**
         * Like count.
         */
        LIKES,
        /**
         * Share count.
         */
        SHARES
    }

    /**
     * Timer factory.
     */
    private static TimerFactory timerFactory = new TimerFactory();

    /**
     * Timer expiration.
     */
    private static final int TIMER_EXPIRATION = 250;

    /**
     * Timer expiration for intial show.
     */
    private static final int INITIAL_TIMER_EXPIRATION = 2500;

    /**
     * Setup the floating avatar panel.
     */
    private static void setup()
    {

        // Reimplementing Focus panel, GWT seems to break otherwise.
        usersWhoLikedPanelWrapper = new FlowPanel()
        {
            private boolean actuallyOut = false;

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
                    hasMousedOver++;
                }
                else if (DOM.eventGetType(event) == Event.ONCLICK)
                {
                    usersWhoLikedPanelWrapper.setVisible(false);
                }
            }
        };

        popoutArrow.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectPopoutArrow());

        usersWhoLikedPanelWrapper.add(popoutArrow);

        viewAll.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectShowAllUsersWhoLikedActivity());
        viewAll.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
        // viewAll.setVisible(false);
        viewAll.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                showActorsPopup(countType, resourceUrl);
            }
        });

        usersWhoLikedPanelWrapper.setVisible(false);
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityWrapper());
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss()
                .eurekaConnectLikedActivityWrapper());
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likeCountWidget());
        RootPanel.get().add(usersWhoLikedPanelWrapper);

        usersWhoLikedPanelWrapper.add(usersWhoLikedPanel);
        usersWhoLikedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivity());
        usersWhoLikedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectUsersWhoLikedActivity());
        usersWhoLikedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikedActivity());

        FlowPanel userLikedHeader = new FlowPanel();
        userLikedHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityHeader());
        usersWhoLikedPanel.add(userLikedHeader);

        userLikedBody.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityBody());
        usersWhoLikedPanel.add(userLikedBody);

        FlowPanel userLikedFooter = new FlowPanel();
        userLikedFooter.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityFooter());
        usersWhoLikedPanel.add(userLikedFooter);

        userLikedBody.add(avatarPanel);
        userLikedBody.add(viewAll);
        usersWhoLikedPanelWrapper.sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);

        hasUsersWhoLikedBeenAddedToRoot = true;
    }

    /**
     * Constructor.
     *
     * @param inCountType
     *            the count type.
     * @param inResoureceUrl
     *            activity url.
     * @param thumbs
     *            thumbnails.
     * @param desc
     *            description.
     * @param title
     *            title.
     * @param inLikeCount
     *            like count.
     * @param inLikers
     *            who has liked this activity.
     * @param inIsLiked
     *            if the person has liked the current activity.
     */
    public ResourceCountWidget(final CountType inCountType, final String inResoureceUrl, final String title,
            final String desc, final String[] thumbs, final Integer inLikeCount, final List<PersonModelView> inLikers,
            final Boolean inIsLiked)
    {
        thisIsLiked = inIsLiked;
        countType = inCountType;
        resourceUrl = inResoureceUrl;

        initWidget(widget);

        if (!hasUsersWhoLikedBeenAddedToRoot)
        {
            setup();
        }

        widget.addMouseOverHandler(new MouseOverHandler()
        {
            public void onMouseOver(final MouseOverEvent arg0)
            {
                countType = inCountType;
                resourceUrl = inResoureceUrl;
                if (likeCount > 0)
                {
                    showPanel();
                    usersWhoLikedPanelWrapper.setVisible(true);
                }
            }
        });

        likers = inLikers;
        likeCount = inLikeCount;

        likeCountLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectCount());
        likeCountLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                showActorsPopup(inCountType, inResoureceUrl);
            }
        });

        final FlowPanel likeCountPanel = new FlowPanel();

        if (inCountType.equals(CountType.LIKES))
        {
            final FlowPanel likeContainer = new FlowPanel();
            likeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikeButton());

            if (thisIsLiked)
            {
                likeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectUnlikeButton());
            }
            else
            {
                likeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikeButton());
            }

            Anchor likeLink = new Anchor();

            EventBus.getInstance().addObserver(ResourceLikeChangeEvent.class, new Observer<ResourceLikeChangeEvent>()
            {
                public void update(final ResourceLikeChangeEvent event)
                {
                    if (event.getResponse())
                    {
                        likeContainer.removeStyleName(StaticResourceBundle.INSTANCE.coreCss()
                                .eurekaConnectLikeButton());
                        likeContainer
                                .addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectUnlikeButton());
                        updatePanel(LikeActionType.ADD_LIKE);
                        currentPanel.showPanel();
                        usersWhoLikedPanelWrapper.setVisible(true);
                    }
                    else
                    {
                        likeContainer.removeStyleName(StaticResourceBundle.INSTANCE.coreCss()
                                .eurekaConnectUnlikeButton());
                        likeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikeButton());
                        updatePanel(LikeActionType.REMOVE_LIKE);
                        currentPanel.showPanel();
                        usersWhoLikedPanelWrapper.setVisible(false);
                    }

                    likeCountLink.setText(likeCount.toString());
                }
            });

            likeLink.addClickHandler(new ClickHandler()
            {

                public void onClick(final ClickEvent arg0)
                {
                    thisIsLiked = !thisIsLiked;
                    final HashMap<String, Serializable> request = new HashMap<String, Serializable>();
                    request.put("resourceurl", inResoureceUrl);
                    request.put("liked", thisIsLiked);

                    LikeResourceModel.getInstance().insert(request);
                }
            });

            likeContainer.add(likeLink);
            likeContainer.add(likeCountLink);
            likeCountPanel.add(likeContainer);
        }
        else
        {
            FlowPanel shareContainer = new FlowPanel();
            shareContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectShareButton());

            Anchor shareLink = new Anchor();
            shareLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent arg0)
                {
                    String thumbsStr = "";
                    final int height = 380;
                    final int width = 800;

                    for (String thumb : thumbs)
                    {
                        if (thumbsStr.length() > 0)
                        {
                            thumbsStr += ",";
                        }
                        thumbsStr += thumb;
                    }

                    Window.open("/widget.html?widget=sharedialog&thumbs=" + thumbsStr + "&desc=" + desc + "&title="
                            + title + "&resourceurl=" + inResoureceUrl, null,
                            WidgetJSNIFacadeImpl.nativeGetCenteredPopupFeatureString(width, height)
                                    + ",status=yes,toolbar=no,menubar=no,location=no");
                }
            });

            shareContainer.add(shareLink);
            shareContainer.add(likeCountLink);
            likeCountPanel.add(shareContainer);
        }

        widget.add(likeCountPanel);

        updatePanel(null);
    }

    /**
     * Called to update the panel.
     */
    private void showPanel()
    {
        hasMousedOver++;
        final int hasMouseOverVal = hasMousedOver;
        currentPanel = this;
        viewAll.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
        // viewAll.setVisible(false);
        avatarPanel.clear();

        int widgetTop = widget.getAbsoluteTop();
        int newTopMargin = widgetTop + POPOUT_ARROW_TOP_OFFSET;
        DOM.setStyleAttribute(popoutArrow.getElement(), "marginTop", newTopMargin + "px");

        for (PersonModelView liker : likers)
        {
            avatarPanel.add(AvatarLinkPanel.create(liker, Size.VerySmall));
        }

        if (likeCount > MAXLIKERSSHOWN)
        {
            viewAll.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
            // viewAll.setVisible(true);
        }

        innerLikeCountLink.setText(likeCount.toString());

        timerFactory.runTimer(INITIAL_TIMER_EXPIRATION, new TimerHandler()
        {
            public void run()
            {
                if (hasMousedOver == hasMouseOverVal)
                {
                    usersWhoLikedPanelWrapper.setVisible(false);
                }
            }
        });

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
    }

    /**
     * Pops up a window showing a list of people.
     *
     * @param inCountType
     *            Type of people.
     * @param inResoureceUrl
     *            URL of resource.
     */
    private static void showActorsPopup(final CountType inCountType, final String inResoureceUrl)
    {
        final int height = 320;
        final int width = 400;
        String actorType = CountType.LIKES.equals(inCountType) ? "likes" : "shares";
        Window.open("/widget.html?widget=actordialog&actortype=" + actorType + "&resourceurl=" + inResoureceUrl, null,
                WidgetJSNIFacadeImpl.nativeGetCenteredPopupFeatureString(width, height)
                        + ",status=yes,toolbar=no,menubar=no,location=no");
    }
}
