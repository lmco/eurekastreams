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

import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.web.client.events.ChangeShowStreamRecipientEvent;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.SwitchedToActivityDetailViewEvent;
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
public class CustomStreamPanel extends Composite implements FilterPanel
{

    /**
     * The view associated with it.
     */
    private Stream stream;

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
     * @param inStream
     *            the view.
     */
    public CustomStreamPanel(final Stream inStream)
    {
        FocusPanel container = new FocusPanel();
        container.addStyleName("filter");

        FlowPanel panel = new FlowPanel();

        labelContainer = new FlowPanel();
        labelContainer.addStyleName("filter-label");
        label = new Label(inStream.getName());
        labelContainer.add(label);
        stream = inStream;

        panel.addStyleName("stream-list-item");
        readOnly = stream.getReadOnly();

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
                    CustomStreamDialogContent dialogContent = new CustomStreamDialogContent(stream);
                    Dialog dialog = new Dialog(dialogContent);
                    dialog.setBgVisible(true);
                    dialog.center();
                }
            });
            editButton.addStyleName("edit-button");
            panel.add(editButton);

        }

        Session.getInstance().getEventBus().addObserver(SwitchedToActivityDetailViewEvent.class,
                new Observer<SwitchedToActivityDetailViewEvent>()
                {
                    public void update(final SwitchedToActivityDetailViewEvent arg1)
                    {
                        unActivate();
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent arg1)
            {
                unActivate();
            }
        });

        container.add(panel);
        initWidget(container);
    }

    /**
     * Set view.
     *
     * @param inStream
     *            the view.
     */
    public void setFilter(final StreamFilter inStream)
    {
        stream = (Stream) inStream;
        label.setText(stream.getName());
    }

    /**
     * Get item id.
     *
     * @return the item id.
     */
    public Long getItemId()
    {
        return stream.getId();
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
        Session.getInstance().getEventBus().notifyObservers(
                new StreamRequestEvent(stream.getName(), stream.getId(), stream.getRequest()));

        this.addStyleName("active");

        Session.getInstance().getEventBus().notifyObservers(new ChangeShowStreamRecipientEvent(true));
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
        return stream;
    }

    /**
     * Updates the history.
     */
    public void updateHistory()
    {
        Session.getInstance().getEventBus().notifyObservers(
                new UpdateHistoryEvent(new CreateUrlRequest("streamId", String.valueOf(stream.getId()), true)));
    }
}
