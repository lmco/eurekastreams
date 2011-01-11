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

import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.GotSearchResultsResponseEvent;
import org.eurekastreams.web.client.model.SearchResultsGroupModel;
import org.eurekastreams.web.client.model.SearchResultsModel;
import org.eurekastreams.web.client.model.SearchResultsOrgModel;
import org.eurekastreams.web.client.model.SearchResultsPeopleModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.SingleColumnPagedListRenderer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The general search results page.
 */
public class SearchContent extends FlowPanel
{
    /**
     * The paged list panel to display results.
     */
    private PagedListPanel searchResultsPanel = null;

    /**
     * The label for the query text.
     */
    private final InlineLabel queryText = new InlineLabel("");

    /**
     * The renderer.
     */
    private final SearchResultItemRenderer renderer = new SearchResultItemRenderer();

    /** Has the control been initialized. */
    private boolean initialized = false;

    /** Current boost value; used to avoid re-fetch when there is no change. */
    private String currentBoost;

    /** Current query string; used to avoid re-fetch when there is no change. */
    private String currentQuery;

    /**
     * Constructor.
     */
    public SearchContent()
    {
        // When the history changes, update the query and reset the pager, triggering a re-search.
        // IMPORTANT: Wire up URL change event first, so we see it before the PagedList does. This way we can change the
        // search term in the search requests before PagedList sees the event. Otherwise PagedList may see the change in
        // the page range and send out a search request using the old search term.
        Session.getInstance().getEventBus()
                .addObserver(UpdatedHistoryParametersEvent.class, new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        onHistoryParameterChange(event);
                    }
                });

        RootPanel.get().addStyleName("directory");

        searchResultsPanel = new PagedListPanel("searchResults", new SingleColumnPagedListRenderer());
        searchResultsPanel.addStyleName("search-results");
        FlowPanel contentPanel = new FlowPanel();

        Label header = new Label("Profile Search Results");
        header.addStyleName("directory-header");
        this.add(header);
        this.add(contentPanel);

        FlowPanel resultsHeaderPanel = new FlowPanel();
        resultsHeaderPanel.addStyleName("results-header");
        resultsHeaderPanel.add(new InlineLabel("Search Results for: "));
        resultsHeaderPanel.add(queryText);

        searchResultsPanel.insert(resultsHeaderPanel, 0);
        contentPanel.add(searchResultsPanel);

        // When the search results come back, render the results.
        Session.getInstance().getEventBus()
                .addObserver(GotSearchResultsResponseEvent.class, new Observer<GotSearchResultsResponseEvent>()
                {
                    public void update(final GotSearchResultsResponseEvent arg1)
                    {
                        searchResultsPanel.render(arg1.getResponse(), "No matches found");
                    }
                });
    }

    /**
     * When the history changes, update the query and reset the pager, triggering a re-search.
     *
     * @param event
     *            Event containing URL parameters.
     */
    private void onHistoryParameterChange(final UpdatedHistoryParametersEvent event)
    {
        String boost = event.getParameters().get("boost");
        if (boost == null)
        {
            boost = "";
        }
        String query = event.getParameters().get("query");
        if (query == null)
        {
            query = "";
        }
        queryText.setText(query);

        GetDirectorySearchResultsRequest request = new GetDirectorySearchResultsRequest(query, "", boost, 0, 0);

        if (!initialized)
        {
            searchResultsPanel.addSet("All", SearchResultsModel.getInstance(), renderer, request);
            searchResultsPanel.addSet("Employees", SearchResultsPeopleModel.getInstance(), renderer, request);
            searchResultsPanel.addSet("Groups", SearchResultsGroupModel.getInstance(), renderer, request);
            searchResultsPanel.addSet("Organizations", SearchResultsOrgModel.getInstance(), renderer, request);
            initialized = true;
        }
        else if (!boost.equals(currentBoost) || !query.equals(currentQuery))
        {
            searchResultsPanel.updateSetRequest("All", request);
            searchResultsPanel.updateSetRequest("Employees", request);
            searchResultsPanel.updateSetRequest("Groups", request);
            searchResultsPanel.updateSetRequest("Organizations", request);

            // Invalidate searchResultsPanel so that it will reload when it gets the event.
            // Ways NOT to implement this:
            // 1. Call searchResultsPanel.reload(). This leads to the search data being requested from the server twice
            // if both the search criteria and the page range / filter / sort changed. The reload causes a query but
            // does not save the filter/sort/paging state, so once searchResultsPanel gets the URL change event, it
            // detects
            // a change and performs the query again.
            // 2. Don't call anything. If none of the page range / filter / sort changed, then when searchResultsPanel
            // gets the event it won't think it needs to do anything, so no query ever gets made.
            // Ideally, we'd use a trickle-down approach where searchResultsPanel would never get the event from the
            // event bus directly, but rather would get it from this class. This class could then say
            // "here's the event, and update yourself with it unconditionally".
            searchResultsPanel.invalidateState();
        }
        currentBoost = boost;
        currentQuery = query;
    }
}
