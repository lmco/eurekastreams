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

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Stream sort panel.
 */
public class StreamSortPanel extends Composite
{
    /**
     * The widget.
     */
    private FlowPanel widget = new FlowPanel();

    /**
     * The sort type.
     */
    private String sort = "date";

    /**
     * The active sort.
     */
    private Anchor activeSort = null;

    /**
     * Map of the links to the sorts.
     */
    final Map<String, Anchor> linkMap = new HashMap<String, Anchor>();

    /**
     * Stream to URL transformer.
     */
    private StreamToUrlTransformer streamUrlTransformer = new StreamToUrlTransformer();

    /**
     * Constructor.
     */
    public StreamSortPanel()
    {
        initWidget(widget);

        widget.addStyleName("navpanel");

        FlowPanel options = new FlowPanel();
        options.addStyleName("options");

        widget.add(options);

        final Anchor atomLink = new Anchor();
        atomLink.addStyleName("stream-atom-link");
        atomLink.setTarget("_NEW");
        widget.add(atomLink);

        EventBus.getInstance().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent event)
            {
                atomLink.setVisible(Session.getInstance().getParameterValue("search") == null
                        || Session.getInstance().getParameterValue("search").length() == 0);

                if (event.getStreamId() != null)
                {
                    atomLink.setHref("/resources/atom/stream/saved/" + event.getStreamId());
                }
                else
                {
                    JSONObject query = JSONParser.parse(event.getJson()).isObject().get("query").isObject();

                    if (query.containsKey("organization"))
                    {
                        atomLink.setHref("/resources/atom/stream/query/organization/"
                                + query.get("organization").isString().stringValue());
                    }
                    else if (query.containsKey("recipient"))
                    {
                        JSONArray recipients = query.get("recipient").isArray();
                        StringBuilder recipientLink = new StringBuilder();
                        recipientLink.append("/resources/atom/stream/query/recipient/");

                        for (int i = 0; i < recipients.size(); i++)
                        {
                            if (i > 0)
                            {
                                recipientLink.append(",");
                            }
                            JSONObject entityObject = recipients.get(i).isObject();
                            recipientLink.append(entityObject.get("type").isString().stringValue());
                            recipientLink.append(":");
                            recipientLink.append(entityObject.get("name").isString().stringValue());

                        }

                        atomLink.setHref(recipientLink.toString());
                    }

                }
            }
        });

        options.add(new Label("Sort:"));

        final Anchor dateSort = new Anchor("Recent");
        dateSort.setTitle("Sorted by activity post date.");
        dateSort.addStyleName("sort-option");
        options.add(dateSort);
        linkMap.put("date", dateSort);

        final Anchor interestingSort = new Anchor("Popular");
        interestingSort.addStyleName("sort-option");
        options.add(interestingSort);
        linkMap.put("interesting", interestingSort);

        final Anchor commentSort = new Anchor("Active");
        commentSort.setTitle("Sorted by last comment date");
        commentSort.addStyleName("sort-option");
        options.add(commentSort);
        linkMap.put("commentdate", commentSort);

        EventBus.getInstance().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (event.getParameters().containsKey("sort"))
                        {
                            sort = event.getParameters().get("sort");
                        }
                        else
                        {
                            sort = "date";
                        }

                        updateSelected(sort, false);
                    }
                }, true);

        dateSort.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                updateSelected("date", true);
            }
        });

        interestingSort.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                updateSelected("interesting", true);
            }
        });

        commentSort.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                updateSelected("commentdate", true);
            }
        });
    }

    /**
     * Update the selected sort.
     * 
     * @param updatedSort
     *            the new sort.
     * @param setHistory
     *            if the history should be set.
     */
    public void updateSelected(final String updatedSort, final boolean setHistory)
    {
        sort = updatedSort;
        if (null != activeSort)
        {
            activeSort.removeStyleName("active");
        }

        activeSort = linkMap.get(sort);
        activeSort.addStyleName("active");

        if (setHistory)
        {
            Session.getInstance().getEventBus().notifyObservers(
                    new UpdateHistoryEvent(new CreateUrlRequest("sort", String.valueOf(sort), false)));
        }
    }

    /**
     * Get the sort.
     * 
     * @return the sort.
     */
    public String getSort()
    {
        return sort;
    }
}
