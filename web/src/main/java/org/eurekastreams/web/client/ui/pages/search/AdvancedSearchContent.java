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
package org.eurekastreams.web.client.ui.pages.search;

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestMoreEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamListPanel;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Page to test advanced search features.
 */
public class AdvancedSearchContent extends FlowPanel
{
    /**
     * ID of last seen activity.
     */
    private long lastSeenId = 0L;

    private Label error = new Label("");

    private StreamListPanel stream = new StreamListPanel(new StreamMessageItemRenderer(true));

    /**
     * Initialize page.
     */
    public AdvancedSearchContent()
    {
        RootPanel.get().addStyleName("advanced-search");

        final TextArea json = new TextArea();
        final Anchor search = new Anchor("search");
        stream.addStyleName("stream");
        stream.setVisible(false);

        this.add(json);
        this.add(search);
        this.add(error);
        this.add(stream);

        search.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                performSearch(json.getText());
            }
        });

        EventBus.getInstance().addObserver(StreamRequestMoreEvent.class, new Observer<StreamRequestMoreEvent>()
        {
            public void update(final StreamRequestMoreEvent arg1)
            {
                JSONValue jsonVal = JSONParser.parse(json.getText());
                JSONObject obj = jsonVal.isObject();

                if (null != obj)
                {
                    obj.put("maxId", new JSONString(Long.toString(lastSeenId)));
                    json.setText(obj.toString());

                    performSearch(json.getText());
                }
            }
        });
    }

    private void performSearch(final String json)
    {
        Session.getInstance().getActionProcessor().makeRequest(
                new ActionRequestImpl<PagedSet<ActivityDTO>>("getActivitiesByRequest", json),
                new AsyncCallback<PagedSet<ActivityDTO>>()
                {
                    public void onFailure(final Throwable err)
                    {
                        error.setText(err.toString());
                        stream.setVisible(false);
                    }

                    public void onSuccess(final PagedSet<ActivityDTO> activity)
                    {
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

    }
}
