/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.support;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestMoreEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.pagedlist.ItemRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Stream list view.
 */
public class StreamListPanel extends FlowPanel
{
    /**
     * Number of activities in list: This is needed because there are two types of ways activities get into the list.
     * The first is a a bulk request which already has it's total of activities. However when deleting or adding
     * individual activities we need to add or subtract to this total to know if it is empty.
     */
    private int howManyInList = 0;

    /**
     * The item panel.
     */
    FlowPanel itemPanel;

    /**
     * More button.
     */
    Label moreButton;

    /**
     * Label for no results.
     */
    Label noResults;

    /**
     * Waiting spinner.
     */
    FlowPanel waitSpinner = new FlowPanel();

    /**
     * Error label.
     */
    Label errorLabel = new Label();

    /**
     * The item renderer.
     */
    private ItemRenderer<ActivityDTO> renderer;

    /**
     * Are we adding more? Or is this a new stream.
     */
    private boolean addingMore = false;

    /**
     * Constructor.
     *
     * @param inRenderer
     *            the item renderer.
     */
    public StreamListPanel(final ItemRenderer<ActivityDTO> inRenderer)
    {
        renderer = inRenderer;
        itemPanel = new FlowPanel();
        moreButton = new Label("View More");

        errorLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formErrorBox());
        errorLabel.setVisible(false);

        noResults = new Label("No activity has been posted.");
        noResults.addStyleName(StaticResourceBundle.INSTANCE.coreCss().noResults());
        noResults.setVisible(false);

        moreButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().moreButton());
        moreButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
        moreButton.setVisible(false);

        waitSpinner.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitSpinner());

        this.add(itemPanel);
        this.add(errorLabel);
        this.add(waitSpinner);
        this.add(noResults);
        this.add(moreButton);

        final EventBus eventBus = Session.getInstance().getEventBus();

        eventBus.addObserver(MessageStreamUpdateEvent.class, new Observer<MessageStreamUpdateEvent>()
        {
            public void update(final MessageStreamUpdateEvent event)
            {
                if (!addingMore)
                {
                    itemPanel.clear();
                }

                addingMore = false;
                waitSpinner.setVisible(false);
                moreButton.setVisible(event.isMoreResults());
                errorLabel.setVisible(false);

                if (!event.getMessages().getPagedSet().isEmpty())
                {
                    howManyInList = event.getMessages().getPagedSet().size();

                    for (ActivityDTO item : event.getMessages().getPagedSet())
                    {
                        itemPanel.add(renderer.render(item));
                    }
                }
                else
                {
                    noResults.setText("This stream does not contain any activity.");
                    noResults.setVisible(true);
                    howManyInList = 0;
                }

            }
        });

        eventBus.addObserver(DeletedActivityResponseEvent.class, new Observer<DeletedActivityResponseEvent>()
        {
            public void update(final DeletedActivityResponseEvent event)
            {
                howManyInList--;

                if (howManyInList == 0)
                {
                    noResults.setText("No activity has been posted.");
                    noResults.setVisible(true);
                }
            }
        });

        eventBus.addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent event)
            {
                howManyInList++;
            }
        });

        moreButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                waitSpinner.setVisible(true);
                addingMore = true;
                eventBus.notifyObservers(new StreamRequestMoreEvent());
            }
        });
    }

    /**
     * Reinitializes the model.
     */
    public void reinitialize()
    {
        noResults.setVisible(false);
        itemPanel.clear();
        errorLabel.setVisible(false);
        waitSpinner.setVisible(true);
        moreButton.setVisible(false);
        noResults.setVisible(false);
    }

}
