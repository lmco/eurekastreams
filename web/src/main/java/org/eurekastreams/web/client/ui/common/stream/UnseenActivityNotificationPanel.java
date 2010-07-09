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

import org.eurekastreams.server.action.request.stream.GetActivitiesByCompositeStreamRequest;
import org.eurekastreams.server.action.request.stream.GetStreamSearchResultsRequest;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.events.data.GotUnseenActivitiesCountResponseEvent;
import org.eurekastreams.web.client.model.UnseenActivityCountForSearchModel;
import org.eurekastreams.web.client.model.UnseenActivityCountForViewModel;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
     * Default constructor.
     */
    public UnseenActivityNotificationPanel()
    {
        final FlowPanel thisBuffered = this;
        this.addStyleName("unseen-activity");
        this.setVisible(false);

        unseenActivityCount.addStyleName("unseen-label");

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

        Session.getInstance().getEventBus().addObserver(MessageStreamUpdateEvent.class,
                new Observer<MessageStreamUpdateEvent>()
                {
                    public void update(final MessageStreamUpdateEvent event)
                    {
                        thisBuffered.setVisible(false);

                        // stop the timer if we are viewing a single activity
                        if (Session.getInstance().getParameterValue("activityId") != null)
                        {
                            Session.getInstance().getTimer().pauseJob("getUnseenActivityJob");
                        }
                        else if (Session.getInstance().getParameterValue("streamSearch") == null)
                        {
                            Session.getInstance().getTimer().unPauseJob("getUnseenActivityJob");
                            Session.getInstance().getTimer().changeFetchable("getUnseenActivityJob",
                                    UnseenActivityCountForViewModel.getInstance());
                            Session.getInstance().getTimer().changeRequest(
                                    "getUnseenActivityJob",
                                    new GetActivitiesByCompositeStreamRequest(event.getStreamId(), event
                                            .getLatestActivity()));
                        }
                        else
                        {
                            Session.getInstance().getTimer().unPauseJob("getUnseenActivityJob");
                            Session.getInstance().getTimer().changeFetchable("getUnseenActivityJob",
                                    UnseenActivityCountForSearchModel.getInstance());
                            Session.getInstance().getTimer().changeRequest(
                                    "getUnseenActivityJob",
                                    new GetStreamSearchResultsRequest(event.getStreamId(), Session.getInstance()
                                            .getParameterValue("streamSearch"), event.getLatestActivity()));
                        }

                    }
                });

        Session.getInstance().getEventBus().addObserver(GotUnseenActivitiesCountResponseEvent.class,
                new Observer<GotUnseenActivitiesCountResponseEvent>()
                {
                    public void update(final GotUnseenActivitiesCountResponseEvent ev)
                    {
                        if (ev.getResponse() > 0)
                        {
                            thisBuffered.setVisible(true);
                            if (ev.getResponse() == 1)
                            {
                                unseenActivityCount.setHTML("<div><strong>"
                                        + ev.getResponse().toString() + "</strong> new update</div>");
                            }
                            else
                            {
                                unseenActivityCount.setHTML("<div><strong>"
                                        + ev.getResponse().toString() + "</strong> new updates</div>");
                            }

                        }
                        else
                        {
                            thisBuffered.setVisible(false);
                        }
                    }
                });

        Session.getInstance().getTimer().addTimerJob("getUnseenActivityJob", 1,
                UnseenActivityCountForViewModel.getInstance(),
                new GetActivitiesByCompositeStreamRequest(0L, 0L), false);

    }
}
