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
package org.eurekastreams.web.client.ui.pages.search;

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.GotSearchResultsResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Global search composite. TODO break this out for testability.
 */
public class GlobalSearchComposite extends FlowPanel
{
    /**
     * The search term.
     */
    private final LabeledTextBox searchTerm;

    /**
     * Constructor.
     * 
     * @param label
     *            the label for the uninitialized textbox.
     */
    public GlobalSearchComposite(final String label)
    {
        searchTerm = new LabeledTextBox(label);

        Label searchButton = new Label("Search");
        searchButton.addStyleName("search-list-button");
        addStyleName("search-list");
        add(searchTerm);
        add(searchButton);

        final EventBus eventBus = Session.getInstance().getEventBus();

        searchButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (searchTerm.getText().length() > 0)
                {
                    eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.SEARCH,
                            generateParams(searchTerm.getText()), false)));
                }
            }
        });

        searchTerm.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown()
                        && searchTerm.getText().length() > 0)
                {
                    eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.SEARCH,
                            generateParams(searchTerm.getText()), false)));
                }
            }
        });

        eventBus.addObserver(SwitchedHistoryViewEvent.class, new Observer<SwitchedHistoryViewEvent>()
        {
            public void update(final SwitchedHistoryViewEvent event)
            {
                if (event.getPage() != Page.SEARCH)
                {
                    searchTerm.reset();
                }
            }
        });

        // clear search box on successful search
        eventBus.addObserver(GotSearchResultsResponseEvent.class, new Observer<GotSearchResultsResponseEvent>()
        {
            public void update(final GotSearchResultsResponseEvent event)
            {
                if (event.getResponse().getTotal() > 0)
                {
                    searchTerm.reset();
                }
            }
        });
    }

    /**
     * Creates a hashmap for the history parameters to pass to the search page.
     * 
     * @param query
     *            the search string.
     * @return the hashmap of all necessary initial search parameters.
     */
    private HashMap<String, String> generateParams(final String query)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("query", query);
        params.put("startIndex", "0");
        params.put("endIndex", "9");
        return params;
    }
}
