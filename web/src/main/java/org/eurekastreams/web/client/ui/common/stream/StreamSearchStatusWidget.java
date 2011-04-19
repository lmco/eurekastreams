/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.stream.filters.list.CustomStreamDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget displaying a bar with the current search term and options to cancel or save the search.
 */
public class StreamSearchStatusWidget extends Composite
{
    /**
     * Search term label.
     */
    Label searchTermLabel = new Label();

    /**
     * Search description.
     */
    FlowPanel mainPanel = new FlowPanel();

    /**
     * Save search button.
     */
    Label saveSearch = new Label("+ Save Stream");

    /**
     * Close button.
     */
    Label closeButton = new Label("close");

    /**
     * Last request.
     */
    private String lastRequest = "";

    /**
     * Constructor.
     */
    public StreamSearchStatusWidget()
    {
        mainPanel.setVisible(false);
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchDescription());

        closeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().close());
        mainPanel.add(closeButton);

        saveSearch.addStyleName(StaticResourceBundle.INSTANCE.coreCss().saveSearch());
        mainPanel.add(saveSearch);

        Label searchResultsFor = new Label("Results for: ");
        searchResultsFor.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchResultsFor());

        mainPanel.add(searchResultsFor);
        searchTermLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchTerm());
        mainPanel.add(searchTermLabel);

        initWidget(mainPanel);

        saveSearch.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                Stream newStream = new Stream();
                newStream.setRequest(lastRequest);

                Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
                Dialog.showCentered(new CustomStreamDialogContent(newStream));
            }
        });

        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                EventBus.getInstance().notifyObservers(new StreamSearchBeginEvent(null));
                onSearchCanceled();
            }
        });

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                lastRequest = event.getJsonRequest();
            }
        });
    }

    /**
     * Update search widget.
     *
     * @param inSearchTerm
     *            the search term.
     */
    public void setSearchTerm(final String inSearchTerm)
    {
        mainPanel.setVisible(true);
        searchTermLabel.setText(inSearchTerm);
    }

    /**
     * Called when a search is canceled.
     */
    public void onSearchCanceled()
    {
        searchTermLabel.setText("");
        mainPanel.setVisible(false);
    }

    /**
     * Sets if the search can be changed.
     *
     * @param canChange
     *            if the search can be changed.
     */
    public void setCanChange(final boolean canChange)
    {
        closeButton.setVisible(canChange);
        saveSearch.setVisible(canChange);
    }
}
