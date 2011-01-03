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
package org.eurekastreams.web.client.ui.common.pagedlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.action.request.PageableRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerUpdatedEvent;
import org.eurekastreams.web.client.events.SwitchToFilterOnPagedFilterPanelEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.Fetchable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.Pager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * This is a fairly complex control. Basically, it supports a series of "filters" (which can be though of as data sets.
 * These's data sets can be sorted and/or paged. Add sets by feeding it Fetchable models and a renderer for the
 * individual items, and this control should take care of all the logic.
 *
 */
public class PagedListPanel extends FlowPanel
{
    /** URL parameter for the list ID. */
    public static final String URL_PARAM_LIST_ID = "listId";

    /** URL parameter for the filter name. */
    public static final String URL_PARAM_FILTER = "listFilter";

    /** URL parameter for the sort. */
    public static final String URL_PARAM_SORT = "listSort";

    /** List id (so we can have >1 on a page). */
    private String listId = "list";

    /** The current filter. */
    private String currentFilter = "";

    /** The current sort. */
    private String currentSortKey = "";

    /** Current pager start index. */
    private Integer currentStartIndex = null;

    /** Current pager end index. */
    private Integer currentEndIndex = null;

    /** An initial state is being stored awaiting the tabs to be added. */
    private boolean storingInitialState = false;

    /** The initial filter. */
    private String initialFilter;

    /** The initial sort. */
    private String initialSortKey;

    /** initial pager start index. */
    private String initialStartIndex;

    /** The renderers keyed by filter. */
    @SuppressWarnings("unchecked")
    private final Map<String, ItemRenderer> renderers = new HashMap<String, ItemRenderer>();

    /** Requests keyed by filter. */
    private final Map<String, HashMap<String, PageableRequest>> requests = // \n
    new HashMap<String, HashMap<String, PageableRequest>>();

    /** Links keyed by filter. */
    private final Map<String, Anchor> filterLinks = new HashMap<String, Anchor>();

    /** Fetchers keyed by filter. */
    @SuppressWarnings("unchecked")
    private final Map<String, Fetchable> fetchers = new HashMap<String, Fetchable>();

    /** Sorters keyed by filter. */
    private final Map<String, FlowPanel> sortPanels = new HashMap<String, FlowPanel>();

    /** Sort Links keyed by filter and sort. */
    private final Map<String, HashMap<String, Anchor>> sortLinks = new HashMap<String, HashMap<String, Anchor>>();

    /** Collection of filters that have been loaded and the available sorts for each filter. */
    private final Map<String, List<String>> loadedFilters = new HashMap<String, List<String>>();

    /** Used to lay out the page; default is two columns. */
    private PagedListRenderer pageRenderer = new TwoColumnPagedListRenderer();

    /** The bottom pager. Pass in true to show the buttons. */
    private final Pager bottomPager;

    /** Navigation panel. */
    private final FlowPanel navPanel;

    /** Contains the items. */
    private final FlowPanel renderContainer = new FlowPanel();

    /** Contains the filter switchers. */
    private final FlowPanel filterContainer = new FlowPanel();

    /** Contains the sort switchers. */
    private final FlowPanel sortContainer = new FlowPanel();

    /** Waiting spinner. */
    FlowPanel waitSpinner = new FlowPanel();


    /**
     * Default constructor.
     *
     * @param inListId
     *            the list id.
     */
    public PagedListPanel(final String inListId)
    {
        this(inListId, null, null);
    }

    /**
     * Constructor.
     *
     * @param inListId
     *            the list id.
     * @param inContextParam
     *            Parameter to look for in the URL to determine if URL change events apply to this list.
     * @param inContextParamValue
     *            Value of parameter to look for in the URL to determine if URL change events apply to this list.
     */
    public PagedListPanel(final String inListId, final String inContextParam, final String inContextParamValue)
    {
        listId = inListId;
        bottomPager = new Pager("filteredPager" + listId, true);

        waitSpinner.addStyleName("wait-spinner");

        this.addStyleName("connection-master");
        filterContainer.add(new Label("View:"));

        navPanel = new FlowPanel();
        navPanel.addStyleName("navpanel");
        navPanel.add(filterContainer);
        navPanel.add(sortContainer);
        this.add(navPanel);

        filterContainer.addStyleName("options");
        filterContainer.addStyleName("views");
        sortContainer.addStyleName("options");
        bottomPager.addStyleName("bottom-pager");

        this.add(waitSpinner);
        this.add(renderContainer);

        Session.getInstance().getEventBus().addObserver(PagerUpdatedEvent.class, new Observer<PagerUpdatedEvent>()
        {
            public void update(final PagerUpdatedEvent event)
            {
                if (event.getPager().getPagerId().equals("filteredPager" + listId)
                        && event.getPager().getStartIndex() != currentStartIndex)
                {
                    currentStartIndex = event.getPager().getStartIndex();
                    currentEndIndex = event.getPager().getEndIndex();
                    reload();
                }
            }
        });

        Session.getInstance()
                .getEventBus()
                .addObserver(SwitchToFilterOnPagedFilterPanelEvent.class,
                        new Observer<SwitchToFilterOnPagedFilterPanelEvent>()
                        {
                            public void update(final SwitchToFilterOnPagedFilterPanelEvent event)
                            {
                                // ignore events for other lists
                                if (!listId.equals(event.getListId()))
                                {
                                    return;
                                }

                                updateStateIfChanged(event.getFilterName(), event.getSortKey(), null, null,
                                        !event.isFromUrlChange());
                            }
                        });

        Session.getInstance().getEventBus()
                .addObserver(UpdatedHistoryParametersEvent.class, new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        // determine if the history update pertains to this list
                        // If there's a list id, and it not ours, bail out. If there's no list id, then check for the
                        // "context parameter" (basically snoop on the URL to see if the tab we're on is showing); if
                        // present and it's not ours, bail out. Otherwise, we can't be sure if the update pertains to us
                        // or not, so we have to try to process it.
                        HashMap<String, String> parms = event.getParameters();
                        String eventListId = parms.get(URL_PARAM_LIST_ID);
                        if (eventListId == null)
                        {
                            if (inContextParam != null && inContextParamValue != null
                                    && !inContextParamValue.equals(parms.get(inContextParam)))
                            {
                                return;
                            }
                        }
                        else if (!listId.equals(eventListId))
                        {
                            return;
                        }

                        // store state if tabs not added yet
                        if (loadedFilters.isEmpty())
                        {
                            storingInitialState = true;
                            initialFilter = parms.get(URL_PARAM_FILTER);
                            initialSortKey = parms.get(URL_PARAM_SORT);
                            initialStartIndex = parms.get(Pager.URL_PARAM_START_INDEX);
                        }
                        // only handle event if initialized
                        else
                        {
                            updateStateIfChanged(parms.get(URL_PARAM_FILTER), parms.get(URL_PARAM_SORT),
                                    parms.get(Pager.URL_PARAM_START_INDEX), parms.get(Pager.URL_PARAM_END_INDEX),
                                    false);
                        }
                    }
                }, true);

        this.add(bottomPager);

        FlowPanel clear = new FlowPanel();
        clear.addStyleName("clear");
        this.add(clear);
    }

    /**
     * Constructor.
     *
     * @param inListId
     *            the list id.
     * @param inPageRenderer
     *            page layout renderer.
     */
    public PagedListPanel(final String inListId, final PagedListRenderer inPageRenderer)
    {
        this(inListId, null, null);
        pageRenderer = inPageRenderer;
    }

    /**
     * Constructor.
     *
     * @param inListId
     *            the list id.
     * @param inPageRenderer
     *            page layout renderer.
     * @param inContextParam
     *            Parameter to look for in the URL to determine if URL change events apply to this list.
     * @param inContextParamValue
     *            Value of parameter to look for in the URL to determine if URL change events apply to this list.
     */
    public PagedListPanel(final String inListId, final PagedListRenderer inPageRenderer, final String inContextParam,
            final String inContextParamValue)
    {
        this(inListId, inContextParam, inContextParamValue);
        pageRenderer = inPageRenderer;
    }

    /**
     * Updates the state of the widget (loading data, etc.) if the current state does not match the desired state.
     *
     * @param inFilter
     *            Requested filter name.
     * @param inSort
     *            Requested sort key.
     * @param inStartIndex
     *            Requested start index.
     * @param inEndIndex
     *            Requested end index.
     * @param mayUpdateUrl
     *            If the URL is allowed to be updated (e.g. don't update URL in response to a URL change, etc.).
     * @return Whether the state was updated.
     */
    private boolean updateStateIfChanged(final String inFilter, final String inSort, final String inStartIndex,
            final String inEndIndex, final boolean mayUpdateUrl)
    {
        String filter = inFilter;
        String sort = inSort;
        Integer start = normalizeIndex(inStartIndex);

        // For missing parameters, replace with the default values
        if (filter == null)
        {
            filter = (String) loadedFilters.keySet().toArray()[0];
            // treat sort as a child of filter, so if filter is missing, ignore any sort specified
            sort = null;
        }
        // reject unknown filters (maybe extraneous URL parameters or we caught an event that wasn't for us)
        else if (!loadedFilters.containsKey(filter))
        {
            return false;
        }
        if (sort == null)
        {
            sort = loadedFilters.get(filter).get(0);
        }
        // reject unknown sorts (maybe extraneous URL parameters or we caught an event that wasn't for us)
        else if (!loadedFilters.get(filter).contains(sort))
        {
            return false;
        }
        if (start == null)
        {
            start = 0;
        }

        // determine if state is different
        if (!currentFilter.equals(filter))
        {
            currentFilter = filter;
            currentSortKey = sort;
            currentStartIndex = start;
        }
        else if (!currentSortKey.equals(sort))
        {
            currentSortKey = sort;
            currentStartIndex = start;
        }
        else if (!currentStartIndex.equals(start))
        {
            currentStartIndex = start;
        }
        else
        {
            return false;
        }

        currentStartIndex = start;
        currentEndIndex = start + bottomPager.getPageSize() - 1;
        bottomPager.setStartIndex(currentStartIndex);
        bottomPager.setEndIndex(currentEndIndex);
        Session.getInstance().getEventBus().notifyObservers(new PagerUpdatedEvent(bottomPager));
        reload();

        // update the URL when allowed (if from user action on a link, but not if from a history/URL change (otherwise
        // we'd be re-updating the URL which really messes up the back button).
        if (mayUpdateUrl)
        {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(URL_PARAM_LIST_ID, listId);
            params.put(URL_PARAM_FILTER, currentFilter);
            params.put(URL_PARAM_SORT, currentSortKey);
            params.put(Pager.URL_PARAM_START_INDEX, currentStartIndex.toString());
            params.put(Pager.URL_PARAM_END_INDEX, currentEndIndex.toString());
            Session.getInstance().getEventBus()
                    .notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(params, false)));
        }

        return true;
    }

    /**
     * Normalizes a start/end index value.
     *
     * @param value
     *            String form.
     * @return Numeric form (null if not present/valid).
     */
    private Integer normalizeIndex(final String value)
    {
        if (value == null || value.isEmpty())
        {
            return null;
        }
        try
        {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }

    /**
     * Reload the panel to a default filter.
     *
     * @param filter
     *            The default filter to use after reset.
     */
    public void reload(final String filter)
    {
        currentFilter = filter;
        reload();
    }

    /**
     * Reload the panel.
     */
    public void reload()
    {

        for (Anchor filterLink : filterLinks.values())
        {
            filterLink.removeStyleName("active");
        }

        if (sortLinks.get(currentFilter) != null)
        {
            for (Anchor sortLink : sortLinks.get(currentFilter).values())
            {
                sortLink.removeStyleName("active");
            }

            if (sortLinks.get(currentFilter).get(currentSortKey) != null)
            {
                sortLinks.get(currentFilter).get(currentSortKey).addStyleName("active");
            }
        }

        if (sortPanels.get(currentFilter) != null)
        {
            sortContainer.clear();
            sortContainer.add(sortPanels.get(currentFilter));
        }
        else
        {
            sortContainer.clear();
        }

        filterLinks.get(currentFilter).addStyleName("active");

        refreshData();
    }

    /**
     * Causes the data for the current filter and sort to be refreshed (via fetching from the model).
     */
    @SuppressWarnings("unchecked")
    public void refreshData()
    {
        waitSpinner.setVisible(true);
        PageableRequest request = requests.get(currentFilter).get(currentSortKey);
        request.setStartIndex(currentStartIndex);
        request.setEndIndex(currentEndIndex);
        renderContainer.addStyleName("hidden");
        fetchers.get(currentFilter).fetch(request, false);
    }

    /**
     * Add a filter w/o a sort.
     *
     * @param name
     *            name of the filter.
     * @param fetchable
     *            the fetchable model.
     * @param renderer
     *            the renderer.
     * @param request
     *            the request.
     */
    @SuppressWarnings("unchecked")
    public void addSet(final String name, final Fetchable fetchable, final ItemRenderer renderer,
            final PageableRequest request)
    {
        addSet(name, fetchable, renderer, request, "");
    }

    /**
     * Add a filter w/o a sort.
     *
     * @param name
     *            name of the filter.
     * @param fetchable
     *            the fetchable model.
     * @param renderer
     *            the renderer.
     * @param request
     *            the request.
     * @param sortKey
     *            the sort key.
     */
    @SuppressWarnings("unchecked")
    public void addSet(final String name, final Fetchable fetchable, final ItemRenderer renderer,
            final PageableRequest request, final String sortKey)
    {
        if (requests.get(name) == null)
        {
            requests.put(name, new HashMap<String, PageableRequest>());

            renderers.put(name, renderer);
            fetchers.put(name, fetchable);

            Anchor filterLink = new Anchor(name);
            filterLink.addStyleName("connection-filter-button");

            filterLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    updateStateIfChanged(name, sortKey, null, null, true);
                }
            });

            filterLinks.put(name, filterLink);
            filterContainer.add(filterLink);
            sortLinks.put(name, new HashMap<String, Anchor>());
        }

        requests.get(name).put(sortKey, request);

        if (!sortKey.equals(""))
        {
            if (sortPanels.get(name) == null)
            {
                FlowPanel sortPanel = new FlowPanel();
                sortPanel.add(new Label("Sort: "));
                sortPanels.put(name, sortPanel);
            }

            Anchor sortLink = new Anchor(sortKey);
            sortLink.addStyleName("connection-filter-button");
            sortLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    updateStateIfChanged(name, sortKey, null, null, true);
                }
            });
            sortPanels.get(name).add(sortLink);
            sortLinks.get(name).put(sortKey, sortLink);
        }

        if (loadedFilters.containsKey(name))
        {
            loadedFilters.get(name).add(sortKey);
        }
        else
        {
            List sorts = new ArrayList();
            sorts.add(sortKey);
            loadedFilters.put(name, sorts);
        }

        // attempt to apply the initial state
        // This tab may be the one that satisfies the stored initial state. So we try to apply the state; if it doesn't
        // fit, then nothing happens and the initial state will still be stored for the next tab added. If it worked,
        // then we clear the initial state.
        if (storingInitialState)
        {
            if (updateStateIfChanged(initialFilter, initialSortKey, initialStartIndex, null, false))
            {
                storingInitialState = false;
            }
        }
    }

    /**
     * Updates the request for a given set that has no sort key.
     *
     * @param name
     *            The name of the set to update.
     * @param request
     *            The updated request.
     */
    public void updateSetRequest(final String name, final PageableRequest request)
    {
        updateSetRequest(name, request, "");
    }

    /**
     * Updates the request for a given set.
     *
     * @param name
     *            The name of the set to update.
     * @param request
     *            The updated request.
     * @param sortKey
     *            The sort key of the set to update.
     */
    public void updateSetRequest(final String name, final PageableRequest request, final String sortKey)
    {
        if (requests.get(name) != null)
        {
            requests.get(name).remove(sortKey);
            requests.get(name).put(sortKey, request);
        }
    }

    /**
     * Render the panel.
     *
     * @param <T>
     *            the type of item.
     * @param items
     *            the items.
     * @param noItemsMessage
     *            the message to display when nothing is there.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> void render(final PagedSet<T> items, final String noItemsMessage)
    {
        ItemRenderer render = renderers.get(currentFilter);
        renderContainer.clear();

        if (items.getTotal() > 0)
        {
            this.removeStyleName("empty-list");
        }
        else
        {

            this.addStyleName("empty-list");
        }
        pageRenderer.render(renderContainer, render, items, noItemsMessage);
        renderContainer.removeStyleName("hidden");

        bottomPager.setTotal(items.getTotal());
        waitSpinner.setVisible(false);
    }

    /**
     * @return The current filter being displayed.
     */
    public String getCurrentFilter()
    {
        return currentFilter;
    }

    /**
     * Sets the text displayed on the filter link.
     *
     * @param name
     *            The name of the filter.
     * @param title
     *            The text to display.
     */
    public void setFilterTitle(final String name, final String title)
    {
        filterLinks.get(name).setText(title);
    }

}
