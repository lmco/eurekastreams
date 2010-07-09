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
package org.eurekastreams.web.client.ui.common.stream.filters.search;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.SwitchedToSavedSearchEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * Saved search panel.
 * 
 */
public class StreamSearchPanel extends Composite implements FilterPanel
{
    /**
     * The view associated with it.
     */
    private StreamSearch search;

    /**
     * Label Container.
     */
    private FlowPanel labelContainer;

    /**
     * Label.
     */
    private Label label;

    /**
     * The move handle.
     */
    private Label moveHandle;

    /**
     * The renderer.
     */
    private StreamSearchRenderer renderer;

    /**
     * Default constructor.
     * 
     * @param inView
     *            the view.
     * @param inRenderer
     *            the renderer.
     */
    public StreamSearchPanel(final StreamSearch inView, final StreamSearchRenderer inRenderer)
    {
        renderer = inRenderer;
        FocusPanel container = new FocusPanel();
        container.addStyleName("filter");

        FlowPanel panel = new FlowPanel();

        labelContainer = new FlowPanel();
        labelContainer.addStyleName("filter-label");
        label = new Label(inView.getName());
        labelContainer.add(label);
        search = inView;

        panel.addStyleName("stream-list-item");

        container.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                updateHistory();
            }
        });

        panel.add(labelContainer);

        InlineLabel seperator = new InlineLabel();
        seperator.addStyleName("filter-seperator");
        panel.add(seperator);

        seperator.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                updateHistory();
            }
        });

        moveHandle = new Label("move");
        moveHandle.addStyleName("move-handle");
        panel.add(moveHandle);

        Anchor editButton = new Anchor("edit");
        editButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
                StreamSearchDialogContent dialogContent = renderer.getEditDialog(search);
                Dialog dialog = new Dialog(dialogContent);
                dialog.setBgVisible(true);
                dialog.center();
            }
        });
        editButton.addStyleName("edit-button");
        panel.add(editButton);

        container.add(panel);
        initWidget(container);
    }

    /**
     * Set view.
     * 
     * @param inView
     *            the view.
     */
    public void setFilter(final StreamFilter inView)
    {
        search = (StreamSearch) inView;
        label.setText(search.getName());
    }

    /**
     * Get item id.
     * 
     * @return the item id.
     */
    public Long getItemId()
    {
        return search.getId();
    }

    /**
     * Get the mode handle.
     * 
     * @return the move handle.
     */
    public Label getMoveHandle()
    {
        return moveHandle;
    }

    /**
     * Activates the view item.
     */
    public void activate()
    {
        EventBus.getInstance().notifyObservers(new SwitchedToSavedSearchEvent(search));

        String keywords = search.getKeywordsAsString();

        EventBus.getInstance().notifyObservers(new StreamSearchBeginEvent(keywords, search));
        this.addStyleName("active");
    }

    /**
     * Unactivates the view item.
     */
    public void unActivate()
    {
        this.removeStyleName("active");
    }

    /**
     * Returns the filter.
     * 
     * @return the filter.
     */
    public StreamFilter getFilter()
    {
        return search;
    }

    /**
     * Updates the history.
     */
    public void updateHistory()
    {
        String keywords = "";
        for (String keyword : search.getKeywords())
        {
            keywords += keyword;
            keywords += " ";
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(renderer.getFilterHistoryToken(), String.valueOf(search.getId()));
        params.put("streamSearch", keywords);
        params.put("viewId", String.valueOf(search.getStreamView().getId()));

        Session.getInstance().getEventBus().notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(params, true)));
    }
}
