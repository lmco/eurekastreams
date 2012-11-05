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
package org.eurekastreams.web.client.ui.common.pager;

import java.util.HashSet;

import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupCoordinatorsResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.model.GroupCoordinatorsModel;
import org.eurekastreams.web.client.model.requests.GetGroupCoordinatorsRequest;
import org.eurekastreams.web.client.ui.common.pagedlist.ModelViewRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.TwoColumnPagedListRenderer;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Following pager.
 */
public class CoordinatorsPagerUiStrategy implements PagerStrategy
{
    /**
     * The model.
     */
    private GroupCoordinatorsModel model = GroupCoordinatorsModel.getInstance();

    /**
     * The event.
     */
    private PagerResponseEvent responseEvent = new PagerResponseEvent();

    /**
     * Coordinators.
     */
    private HashSet<PersonModelView> coordinators;

    /**
     * Basic pager.
     */
    private BasicPager pager = new BasicPager();

    /**
     * Renders two column layout.
     */
    private TwoColumnPagedListRenderer twoColListRenderer = new TwoColumnPagedListRenderer();

    /**
     * Item renderer.
     */
    private ModelViewRenderer itemRenderer = new ModelViewRenderer();

    /**
     * Event key.
     */
    private String entityKey;

    /**
     * Constructor.
     */
    public CoordinatorsPagerUiStrategy()
    {
        responseEvent.setKey(getKey());

        EventBus.getInstance().addObserver(GotGroupCoordinatorsResponseEvent.class,
                new Observer<GotGroupCoordinatorsResponseEvent>()
                {

                    public void update(final GotGroupCoordinatorsResponseEvent event)
                    {
                        pager.setMaxCount(event.getResponse().getTotal());

                        FlowPanel responsePanel = new FlowPanel();
                        twoColListRenderer.render(responsePanel, itemRenderer, event.getResponse(), "No Coordinators");
                        responseEvent.setWidget(responsePanel);
                        EventBus.getInstance().notifyObservers(responseEvent);
                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        entityKey = event.getResponse().getShortName();
                        coordinators = new HashSet<PersonModelView>(event.getResponse().getCoordinators());
                    }
                });

    }

    /**
     * If the pager has next.
     * 
     * @return has next.
     */
    public boolean hasNext()
    {
        return pager.isNextPageable();
    }

    /**
     * If the page has prev.
     * 
     * @return has prev.
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
        pager = new BasicPager();
        GetGroupCoordinatorsRequest req = new GetGroupCoordinatorsRequest(entityKey, coordinators);
        req.setEndIndex(pager.getEndItem());
        req.setStartIndex(pager.getStartItem());
        model.fetch(req, false);
    }

    /**
     * Next.
     */
    public void next()
    {
        pager.nextPage();
        GetGroupCoordinatorsRequest req = new GetGroupCoordinatorsRequest(entityKey, coordinators);
        req.setEndIndex(pager.getEndItem());
        req.setStartIndex(pager.getStartItem());
        model.fetch(req, false);
    }

    /**
     * Prev.
     */
    public void prev()
    {
        pager.previousPage();
        GetGroupCoordinatorsRequest req = new GetGroupCoordinatorsRequest(entityKey, coordinators);
        req.setEndIndex(pager.getEndItem());
        req.setStartIndex(pager.getStartItem());
        model.fetch(req, false);
    }

    /**
     * Get the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return "coordinators";
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
