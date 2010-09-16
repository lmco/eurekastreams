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

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory.SortType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
    private SortType sort = SortType.DATE;

    /**
     * The active sort.
     */
    private Anchor activeSort = null;

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

        options.add(new Label("Sort:"));

        final Anchor dateSort = new Anchor("Recent");
        activeSort = dateSort;
        dateSort.addStyleName("sort-option active");
        options.add(dateSort);

        final Anchor interestingSort = new Anchor("Popular");
        interestingSort.addStyleName("sort-option");
        options.add(interestingSort);

        final Anchor commentSort = new Anchor("Last Comment");
        commentSort.addStyleName("sort-option");
        options.add(commentSort);

        dateSort.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                activeSort.removeStyleName("active");
                activeSort = dateSort;
                activeSort.addStyleName("active");

                sort = SortType.DATE;
                EventBus.getInstance().notifyObservers(StreamReinitializeRequestEvent.getEvent());
            }
        });

        interestingSort.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                activeSort.removeStyleName("active");
                activeSort = interestingSort;
                activeSort.addStyleName("active");

                sort = SortType.INTERESTING;
                EventBus.getInstance().notifyObservers(StreamReinitializeRequestEvent.getEvent());
            }
        });

        commentSort.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                activeSort.removeStyleName("active");
                activeSort = commentSort;
                activeSort.addStyleName("active");

                sort = SortType.COMMENT_DATE;
                EventBus.getInstance().notifyObservers(StreamReinitializeRequestEvent.getEvent());
            }
        });
    }

    /**
     * Get the sort.
     * 
     * @return the sort.
     */
    public SortType getSort()
    {
        return sort;
    }
}
