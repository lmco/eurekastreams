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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.StreamRequestMoreEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.model.StreamModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory.SortType;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Page to test advanced search features.
 */
public class StreamPanel extends FlowPanel
{
    /**
     * ID of last seen activity.
     */
    private long lastSeenId = 0L;

    /**
     * Error label.
     */
    private Label error = new Label("");

    /**
     * Stream panel.
     */
    private StreamListPanel stream = null;

    /**
     * Search JSON.
     */
    private String searchJson = "";

    /**
     * Panel for posting contents.
     */
    private FlowPanel shadowPanel = new FlowPanel();

    /**
     * The post widget.
     */
    private PostToStreamComposite postComposite = null;

    /**
     * Stream search.
     */
    private StreamSearchComposite streamSearch;

    /**
     * The stream name.
     */
    private String streamName = "";

    /**
     * Posting disabled panel.
     */
    private FlowPanel postingDisabled = new FlowPanel();

    /**
     * Post content panel.
     */
    private FlowPanel postContent = new FlowPanel();

    /**
     * Initialize page.
     * 
     * @param showRecipients
     *            if recipients should be shown.
     */
    public StreamPanel(final Boolean showRecipients)
    {
        this.addStyleName("layout-container");

        stream = new StreamListPanel(new StreamMessageItemRenderer(showRecipients));
        stream.addStyleName("stream");
        stream.setVisible(false);

        shadowPanel.addStyleName("post-to-stream-container");
        shadowPanel.setVisible(false);

        streamSearch = new StreamSearchComposite();

        postContent.add(shadowPanel);
        this.add(postContent);
        this.add(streamSearch);
        this.add(new UnseenActivityNotificationPanel());
        this.add(error);
        this.add(stream);

        stream.reinitialize();

        EventBus.getInstance().addObserver(StreamRequestMoreEvent.class, new Observer<StreamRequestMoreEvent>()
        {
            public void update(final StreamRequestMoreEvent arg1)
            {
                JSONValue jsonVal = JSONParser.parse(searchJson);
                JSONObject obj = jsonVal.isObject();

                if (null != obj)
                {
                    obj.put("maxId", new JSONString(Long.toString(lastSeenId)));
                    StreamModel.getInstance().fetch(jsonVal.toString(), false);
                }
            }
        });

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                PagedSet<ActivityDTO> activity = event.getStream();

                int numberOfActivities = activity.getPagedSet().size();

                if (numberOfActivities > 0)
                {
                    lastSeenId = activity.getPagedSet().get(numberOfActivities - 1).getId();
                }

                MessageStreamUpdateEvent updateEvent = new MessageStreamUpdateEvent(activity);
                updateEvent.setMoreResults(activity.getTotal() > activity.getPagedSet().size());

                error.setText("");
                EventBus.getInstance().notifyObservers(updateEvent);
                stream.setVisible(true);
            }
        });

        EventBus.getInstance().addObserver(StreamReinitializeRequestEvent.class,
                new Observer<StreamReinitializeRequestEvent>()
                {
                    public void update(final StreamReinitializeRequestEvent event)
                    {
                        stream.reinitialize();
                        StreamModel.getInstance().fetch(searchJson, false);
                    }
                });

        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent evt)
            {
                stream.reinitialize();
                StreamModel.getInstance().fetch(searchJson, false);
            }
        });

        EventBus.getInstance().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent event)
            {
                stream.reinitialize();
                searchJson = event.getJson();
                streamName = event.getStreamName();
                streamSearch.setTitleText(streamName);
                StreamModel.getInstance().fetch(event.getJson(), false);
            }
        });

        EventBus.getInstance().addObserver(StreamSearchBeginEvent.class, new Observer<StreamSearchBeginEvent>()
        {
            public void update(final StreamSearchBeginEvent event)
            {
                searchJson = StreamJsonRequestFactory.setSort(
                        SortType.DATE,
                        StreamJsonRequestFactory.setSearchTerm(event.getSearchText(), StreamJsonRequestFactory
                                .getJSONRequest(searchJson))).toString();
                EventBus.getInstance().notifyObservers(new StreamRequestEvent(streamName, searchJson));
            }
        });

        EventBus.getInstance().addObserver(DeletedActivityResponseEvent.class,
                new Observer<DeletedActivityResponseEvent>()
                {
                    public void update(final DeletedActivityResponseEvent ev)
                    {
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("Activity has been deleted")));
                    }
                });
    }

    /**
     * Set the stream scope to post to.
     * 
     * @param streamScope
     *            the scope.
     * @param postingEnabled
     *            if posting is enabled.
     */
    public void setStreamScope(final StreamScope streamScope, final Boolean postingEnabled)
    {
        postingDisabled.setVisible(!postingEnabled);
        shadowPanel.setVisible(postingEnabled);

        if (postComposite == null && postingEnabled)
        {
            postComposite = new PostToStreamComposite(Session.getInstance().getActionProcessor(), streamScope);
            shadowPanel.add(postComposite);
            DeferredCommand.addCommand(new Command()
            {
                public void execute()
                {
                    postComposite.setUpMinimizer();
                }
            });
        }
        else if (postingEnabled)
        {
            postComposite.setScope(streamScope);
        }
        else
        {
            FlowPanel postingDisabledMessage = new FlowPanel();
            postingDisabledMessage.getElement().setInnerHTML("Posting messages has been disabled by this group");
            postingDisabled.addStyleName("posting-disabled-box");
            postingDisabled.add(postingDisabledMessage);
            postContent.add(postingDisabled);
        }
    }
}
