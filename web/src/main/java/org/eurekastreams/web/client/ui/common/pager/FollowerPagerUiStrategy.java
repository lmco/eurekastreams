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

import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupMembersResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowersResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.model.Fetchable;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.RemovableGroupMemberPersonRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.TwoColumnPagedListRenderer;

import org.eurekastreams.server.search.modelview.DomainGroupModelView;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Follower page strategy.
 */
public class FollowerPagerUiStrategy implements PagerStrategy
{
    /**
     * Model.
     */
    private Fetchable<GetFollowersFollowingRequest> model;

    /**
     * Event.
     */
    private PagerResponseEvent responseEvent = new PagerResponseEvent();

    /**
     * Pager.
     */
    private BasicPager pager = new BasicPager();

    /**
     * Renders two column layout.
     */
    private TwoColumnPagedListRenderer twoColListRenderer = new TwoColumnPagedListRenderer();

    /**
     * Renders a person.
     */
    private PersonRenderer personRenderer = new PersonRenderer(false);

    /**
     * Renders a person with a link so that an Admin or Group Coordinator can remove them.
     */
    private RemovableGroupMemberPersonRenderer removablePersonRenderer;

    /**
     * Entity type.
     */
    private EntityType entityType;

    /**
     * Event key.
     */
    private String entityKey;

    /**
     * Group.
     */
    private DomainGroupModelView group;

    /**
     * Constructor.
     */
    public FollowerPagerUiStrategy()
    {
        responseEvent.setKey(getKey());

        EventBus.getInstance().addObserver(GotPersonFollowersResponseEvent.class,
                new Observer<GotPersonFollowersResponseEvent>()
                {

                    public void update(final GotPersonFollowersResponseEvent event)
                    {
                        pager.setMaxCount(event.getResponse().getTotal());

                        FlowPanel responsePanel = new FlowPanel();
                        twoColListRenderer.render(responsePanel, personRenderer, event.getResponse(), "No Followers");
                        responseEvent.setWidget(responsePanel);
                        EventBus.getInstance().notifyObservers(responseEvent);
                    }
                });

        EventBus.getInstance().addObserver(GotGroupMembersResponseEvent.class,
                new Observer<GotGroupMembersResponseEvent>()
                {

                    public void update(final GotGroupMembersResponseEvent event)
                    {
                        pager.setMaxCount(event.getResponse().getTotal());

                        removablePersonRenderer = new RemovableGroupMemberPersonRenderer(entityKey, group);

                        FlowPanel responsePanel = new FlowPanel();
                        twoColListRenderer.render(responsePanel, removablePersonRenderer, event.getResponse(),
                                "No Followers");
                        responseEvent.setWidget(responsePanel);
                        EventBus.getInstance().notifyObservers(responseEvent);
                    }
                });

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        entityKey = event.getResponse().getAccountId();
                        entityType = EntityType.PERSON;
                        model = PersonFollowersModel.getInstance();

                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        entityKey = event.getResponse().getShortName();
                        entityType = EntityType.GROUP;
                        model = GroupMembersModel.getInstance();
                        group = event.getResponse();
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
     * Initialize..
     */
    public void init()
    {
        pager = new BasicPager();
        model.fetch(new GetFollowersFollowingRequest(entityType, entityKey, pager.getStartItem(), pager.getEndItem()),
                false);
    }

    /**
     * Next.
     */
    public void next()
    {
        pager.nextPage();
        model.fetch(new GetFollowersFollowingRequest(entityType, entityKey, pager.getStartItem(), pager.getEndItem()),
                false);

    }

    /**
     * Prev.
     */
    public void prev()
    {
        pager.previousPage();
        model.fetch(new GetFollowersFollowingRequest(entityType, entityKey, pager.getStartItem(), pager.getEndItem()),
                false);
    }

    /**
     * Get the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return "follower";
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
