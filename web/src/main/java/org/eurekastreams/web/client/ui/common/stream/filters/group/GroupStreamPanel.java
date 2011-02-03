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
package org.eurekastreams.web.client.ui.common.stream.filters.group;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.GroupStreamDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.web.client.events.ChangeShowStreamRecipientEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.SwitchedToActivityDetailViewEvent;
import org.eurekastreams.web.client.events.SwitchedToGroupStreamEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * Group stream panel.
 * 
 */
public class GroupStreamPanel extends Composite implements FilterPanel
{
    /**
     * The view associated with it.
     */
    private GroupStreamDTO group;

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
     * Renderer.
     */
    private GroupStreamRenderer renderer;

    /**
     * Switch hangled.
     */
    private Boolean switchHandled = true;

    /**
     * Default constructor.
     * 
     * @param inGroup
     *            the group to render.
     * @param inRenderer
     *            the renderer for this panel
     */
    public GroupStreamPanel(final GroupStreamDTO inGroup, final GroupStreamRenderer inRenderer)
    {
        renderer = inRenderer;
        FocusPanel container = new FocusPanel();
        container.addStyleName("filter");

        FlowPanel panel = new FlowPanel();

        labelContainer = new FlowPanel();
        labelContainer.addStyleName("filter-label");
        label = new Label(inGroup.getName());
        labelContainer.add(label);

        group = inGroup;

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
                if (switchHandled)
                {
                    unActivate();
                }

                switchHandled = true;
            }
        });

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
        group = (GroupStreamDTO) inView;
        label.setText(group.getName());
    }

    /**
     * Get item id.
     * 
     * @return the item id.
     */
    public Long getItemId()
    {
        return group.getId();
    }

    /**
     * Get the move handle.
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
        switchHandled = false;

        this.addStyleName("active");

        String jsonRequest = StreamJsonRequestFactory.addRecipient(EntityType.GROUP, group.getShortName(),
                StreamJsonRequestFactory.getEmptyRequest()).toString();

        Session.getInstance().getEventBus().notifyObservers(new StreamRequestEvent(group.getName(), jsonRequest));
        Session.getInstance().getEventBus().notifyObservers(new ChangeShowStreamRecipientEvent(false));
        Session.getInstance().getEventBus().notifyObservers(new SwitchedToGroupStreamEvent(group));

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
        return group;
    }

    /**
     * Update the history.
     */
    public void updateHistory()
    {
        Session.getInstance().getEventBus().notifyObservers(
                new UpdateHistoryEvent(new CreateUrlRequest(renderer.getFilterHistoryToken(), String.valueOf(group
                        .getId()), true)));
    }
}
