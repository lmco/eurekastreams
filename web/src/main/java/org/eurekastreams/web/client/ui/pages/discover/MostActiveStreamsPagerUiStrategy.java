/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.discover;

import org.eurekastreams.server.action.request.stream.GetMostActiveStreamsPageRequest;
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.data.GotMostActiveStreamsPageResponseEvent;
import org.eurekastreams.web.client.model.MostActiveStreamsModel;
import org.eurekastreams.web.client.ui.common.pagedlist.ThreeColumnPagedListRenderer;
import org.eurekastreams.web.client.ui.common.pager.PagerStrategy;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Pager strategy for Most Active streams on the Discover page.
 */
public class MostActiveStreamsPagerUiStrategy implements PagerStrategy
{
    /**
     * the model.
     */
    private final MostActiveStreamsModel model = MostActiveStreamsModel.getInstance();

    /**
     * The event.
     */
    private final PagerResponseEvent responseEvent = new PagerResponseEvent();

    /**
     * Basic pager.
     */
    private final BasicPager pager = new BasicPager();

    /**
     * Renders three column layout.
     */
    private final ThreeColumnPagedListRenderer threeColListRenderer = new ThreeColumnPagedListRenderer();

    /**
     * Renderer for Most Active Streams StreamDTOs.
     */
    private final ActiveStreamsItemRenderer itemRenderer = new ActiveStreamsItemRenderer();

    /**
     * The page size.
     */
    private final int pageSize;

    /**
     * Constructor.
     *
     * @param inPageSize
     *            the page size
     */
    public MostActiveStreamsPagerUiStrategy(final int inPageSize)
    {
        pageSize = inPageSize;
        responseEvent.setKey(getKey());
    }

    /**
     * Listen for GotMostActiveStreamsPageResponseEvent events.
     */
    private void listen()
    {
        EventBus.getInstance().addObserver(GotMostActiveStreamsPageResponseEvent.class,
                new Observer<GotMostActiveStreamsPageResponseEvent>()
                {
                    public void update(final GotMostActiveStreamsPageResponseEvent inEvent)
                    {
                        EventBus.getInstance().removeObserver(GotMostActiveStreamsPageResponseEvent.class, this);

                        // tell the pager not to try to page past the number of locally-cached items
                        pager.setMaxCount(inEvent.getTotalNumberOfAccessibleStreams());

                        // set the start and end indexes
                        pager.setStartItem(inEvent.getResponse().getFromIndex());
                        pager.setEndItem(inEvent.getResponse().getToIndex());
                        pager.setPageSize(pageSize);

                        FlowPanel responsePanel = new FlowPanel();
                        threeColListRenderer.render(responsePanel, itemRenderer, inEvent.getResponse(),
                                "Activity stream data is not available");

                        responseEvent.setWidget(responsePanel);
                        EventBus.getInstance().notifyObservers(responseEvent);
                    }
                });
    }

    /**
     * Get the key for this response - allows for UI to distinguish which PageerResponseEvent was fired.
     *
     * @return the key for this PagerRequest
     */
    public String getKey()
    {
        return "mostActiveStreams";
    }

    /**
     * Whether there's a next page.
     *
     * @return whether there's more data
     */
    public boolean hasNext()
    {
        return pager.isNextPageable();
    }

    /**
     * Whether there's a previous page.
     *
     * @return whether there's a previous page
     */
    public boolean hasPrev()
    {
        return pager.isPreviousPageable();
    }

    /**
     * Initialize.
     */
    public void init()
    {
        listen();
        model.fetch(new GetMostActiveStreamsPageRequest(0, pageSize - 1), true);
    }

    /**
     * Go forward a page.
     */
    public void next()
    {
        listen();
        pager.nextPage();
        model.fetch(new GetMostActiveStreamsPageRequest(pager.getStartItem(), pager.getEndItem()), true);
    }

    /**
     * Go back a page.
     */
    public void prev()
    {
        listen();
        pager.previousPage();
        model.fetch(new GetMostActiveStreamsPageRequest(pager.getStartItem(), pager.getEndItem()), true);
    }
    
    /**
     * @return the start index from the pager.
     */
	public int getStartIndex() 
	{
		return pager.getStartItem();
	}

    /**
     * @return the end item fro the pager.
     */
	public int getEndIndex()
	{
		return pager.getEndItem();
	}
	
    /**
     * @return the total from the pager.
     */
	public int getTotal() 
	{
		return pager.getMaxCount();
	}
}
