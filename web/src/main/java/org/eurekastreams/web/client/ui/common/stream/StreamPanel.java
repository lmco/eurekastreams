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
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.StreamRequestMoreEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.model.StreamModel;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

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
    private StreamListPanel stream = new StreamListPanel(new StreamMessageItemRenderer(true));

    /**
     * Search JSON.
     */
    private String searchJson = "";

    /**
     * Initialize page.
     */
    public StreamPanel()
    {
        RootPanel.get().addStyleName("advanced-search");

        stream.addStyleName("stream");
        stream.setVisible(false);

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
                StreamModel.getInstance().fetch(event.getJson(), false);
            }
        });
    }
}
