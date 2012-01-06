/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.web.client.events.ChangeActivityModeEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.events.UserActiveEvent;
import org.eurekastreams.web.client.events.UserInactiveEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.events.data.GotUnseenActivitiesCountResponseEvent;
import org.eurekastreams.web.client.model.MouseActivityModel;
import org.eurekastreams.web.client.model.UnseenActivityIDsForViewModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * Unseen notifications panel.
 * 
 */
public class UnseenActivityNotificationPanel extends FlowPanel
{
    /**
     * The unseen activity count label.
     */
    private HTML unseenActivityCount = new HTML();

    /**
     * Max number of unseen activities to look for.
     */
    private static final int MAX_UNSEEN = 100;

    /**
     * List of IDs of the activities authored by current employee during this session.
     */
    private List<Long> authoredActivityIds;

    /**
     * Default constructor.
     */
    public UnseenActivityNotificationPanel()
    {
        final FlowPanel thisBuffered = this;
        this.authoredActivityIds = new ArrayList<Long>();
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().unseenActivity());
        this.setVisible(false);

        unseenActivityCount.addStyleName(StaticResourceBundle.INSTANCE.coreCss().unseenLabel());

        Anchor refreshStream = new Anchor("Refresh Stream");
        refreshStream.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                Session.getInstance().getEventBus().notifyObservers(StreamReinitializeRequestEvent.getEvent());
            }
        });

        this.add(unseenActivityCount);
        this.add(refreshStream);

        // listen to new activities getting posted by this user - ignore those activity ids when they come back from
        // "getActivityIDs" requests
        Session.getInstance().getEventBus()
                .addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
                {
                    public void update(final MessageStreamAppendEvent response)
                    {
                        authoredActivityIds.add(response.getMessage().getId());
                    }
                });

        Session.getInstance().getEventBus()
                .addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
                {
                    public void update(final GotStreamResponseEvent event)
                    {
                        thisBuffered.setVisible(false);

                        // remove job if present and clear job from paused list.
                        Session.getInstance().getTimer().removeTimerJob("getUnseenActivityJob");
                        Session.getInstance().getTimer().unPauseJob("getUnseenActivityJob");

                        // Only show unseen activity if sorted by date.
                        if ("date".equals(event.getSortType()) && event.getStream().getPagedSet().size() > 0)
                        {
                            JSONObject request = StreamJsonRequestFactory.getJSONRequest(event.getJsonRequest());
                            request = StreamJsonRequestFactory.setMinId(
                                    event.getStream().getPagedSet().get(0).getId(), request);
                            request = StreamJsonRequestFactory.setMaxResults(MAX_UNSEEN, request);

                            // add and configure
                            Session.getInstance()
                                    .getTimer()
                                    .addTimerJob("getUnseenActivityJob", 1,
                                            UnseenActivityIDsForViewModel.getInstance(), request.toString(), false);

                            // unpause just to be sure it's cleared.
                            Session.getInstance().getTimer().unPauseJob("getUnseenActivityJob");
                        }
                    }
                });

        Session.getInstance()
                .getEventBus()
                .addObserver(GotUnseenActivitiesCountResponseEvent.class,
                        new Observer<GotUnseenActivitiesCountResponseEvent>()
                        {
                            public void update(final GotUnseenActivitiesCountResponseEvent ev)
                            {
                                ArrayList<Long> activityIds = new ArrayList<Long>();
                                activityIds.addAll(ev.getResponse());

                                // Remove any from this list that we've posted ourself
                                activityIds.removeAll(authoredActivityIds);

                                if (activityIds.size() > 0)
                                {
                                    thisBuffered.setVisible(true);
                                    if (activityIds.size() == 1)
                                    {
                                        unseenActivityCount.setHTML("<div><strong>" + activityIds.size()
                                                + "</strong> new update</div>");
                                    }
                                    else
                                    {
                                        unseenActivityCount.setHTML("<div><strong>" + activityIds.size()
                                                + "</strong> new updates</div>");
                                    }
                                }
                                else
                                {
                                    thisBuffered.setVisible(false);
                                }
                            }
                        });

        // runs a job to detect mouse movement changes once a minute, triggering a timeout after 5 mins of inactivity
        Session.getInstance().getTimer()
                .addTimerJob("getMouseActivityJob", 1, MouseActivityModel.getInstance(), 5, false);

        // Session.getInstance().getTimer().addTimerJob("getUnseenActivityJob", 1,
        // UnseenActivityCountForViewModel.getInstance(), StreamJsonRequestFactory.getEmptyRequest().toString(),
        // false);

        // user is inactive - pauses the job that gets new activity counts
        Session.getInstance().getEventBus().addObserver(UserInactiveEvent.class, new Observer<UserInactiveEvent>()
        {
            public void update(final UserInactiveEvent ev)
            {
                Session.getInstance().getTimer().pauseJob("getUnseenActivityJob");
            }
        });

        // user is active - unpauses the job that gets new activity counts
        Session.getInstance().getEventBus().addObserver(UserActiveEvent.class, new Observer<UserActiveEvent>()
        {
            public void update(final UserActiveEvent ev)
            {
                Session.getInstance().getTimer().unPauseJob("getUnseenActivityJob");
            }
        });

        EventBus.getInstance().addObserver(ChangeActivityModeEvent.class, new Observer<ChangeActivityModeEvent>()
        {
            public void update(final ChangeActivityModeEvent event)
            {
                if (event.isSingleMode())
                {
                    Session.getInstance().getTimer().removeTimerJob("getUnseenActivityJob");
                    Session.getInstance().getTimer().unPauseJob("getUnseenActivityJob");
                }
            }
        });
    }
}
