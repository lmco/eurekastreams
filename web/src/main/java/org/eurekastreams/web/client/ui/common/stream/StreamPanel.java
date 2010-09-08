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
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
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

        this.add(shadowPanel);
        this.add(streamSearch);
        this.add(error);
        this.add(stream);

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

                error.setText("");
                EventBus.getInstance().notifyObservers(new MessageStreamUpdateEvent(activity));
                stream.setVisible(true);
            }
        });

        EventBus.getInstance().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent event)
            {
                searchJson = event.getJson();
                streamSearch.setTitleText(event.getStreamName());
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
                StreamModel.getInstance().fetch(searchJson, false);
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
     */
    public void setStreamScope(final StreamScope streamScope)
    {
        if (postComposite == null)
        {
            postComposite = new PostToStreamComposite(Session.getInstance().getActionProcessor(), streamScope);
            shadowPanel.add(postComposite);
            shadowPanel.setVisible(true);
            DeferredCommand.addCommand(new Command()
            {
                public void execute()
                {
                    postComposite.setUpMinimizer();
                }
            });
        }
        else
        {
            postComposite.setScope(streamScope);
        }
    }
}
