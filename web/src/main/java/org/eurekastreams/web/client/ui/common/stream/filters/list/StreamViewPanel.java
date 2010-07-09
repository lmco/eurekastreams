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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.SwitchedToStreamViewEvent;
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
 * Represents a single list item.
 *
 */
public class StreamViewPanel extends Composite implements FilterPanel
{

    /**
     * The view associated with it.
     */
    private StreamView view;

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
     * Read only.
     */
    Boolean readOnly = false;

    /**
     * Default constructor.
     *
     * @param inView
     *            the view.
     */
    public StreamViewPanel(final StreamView inView)
    {
        FocusPanel container = new FocusPanel();
        container.addStyleName("filter");

        FlowPanel panel = new FlowPanel();

        labelContainer = new FlowPanel();
        labelContainer.addStyleName("filter-label");
        label = new Label(inView.getName());
        labelContainer.add(label);
        view = inView;

        panel.addStyleName("stream-list-item");
        readOnly = (inView.getType() != null);

        container.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                updateHistory();
            }
        });

        panel.add(labelContainer);

        moveHandle = new Label("move");
        moveHandle.addStyleName("move-handle");
        panel.add(moveHandle);

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
        
        if (!readOnly)
        {
            Anchor editButton = new Anchor("edit");
            editButton.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
                    StreamViewDialogContent dialogContent = new StreamViewDialogContent(view.getId());
                    Dialog dialog = new Dialog(dialogContent);
                    dialog.setBgVisible(true);
                    dialog.center();
                }
            });
            editButton.addStyleName("edit-button");
            panel.add(editButton);

        }

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
        view = (StreamView) inView;
        label.setText(view.getName());
    }

    /**
     * Get item id.
     *
     * @return the item id.
     */
    public Long getItemId()
    {
        return view.getId();
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
        EventBus.getInstance().notifyObservers(new SwitchedToStreamViewEvent(view));
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
        return view;
    }

    /**
     * Updates the history.
     */
    public void updateHistory()
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("listId", String.valueOf(view.getId()));
        params.put("viewId", String.valueOf(view.getId()));

        Session.getInstance().getEventBus().notifyObservers(
                new UpdateHistoryEvent(new CreateUrlRequest(params, true)));
    }
}
