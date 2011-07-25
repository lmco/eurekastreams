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

import org.eurekastreams.server.action.request.profile.GetRequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.model.GroupMembershipRequestModel;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRequestingGroupMembershipRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.TwoColumnPagedListRenderer;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Group membership request pager strategy.
 * 
 */
public class GroupMembershipRequestPagerUiStrategy implements PagerStrategy
{
    /**
     * Event.
     */
    private PagerResponseEvent responseEvent = new PagerResponseEvent();

    /**
     * Renders two column layout.
     */
    private TwoColumnPagedListRenderer listRenderer = new TwoColumnPagedListRenderer();

    /**
     * Group id.
     */
    private long groupId;

    /**
     * Group short name.
     */
    private String groupShortName;

    /**
     * Pager.
     */
    private BasicPager pager = new BasicPager();

    /**
     * Constructor.
     */
    public GroupMembershipRequestPagerUiStrategy()
    {
        responseEvent.setKey(getKey());

        EventBus.getInstance().addObserver(GotRequestForGroupMembershipResponseEvent.class,
                new Observer<GotRequestForGroupMembershipResponseEvent>()
                {
                    public void update(final GotRequestForGroupMembershipResponseEvent event)
                    {
                        pager.setMaxCount(event.getResponse().getTotal());

                        FlowPanel responsePanel = new FlowPanel();
                        listRenderer.render(responsePanel, new PersonRequestingGroupMembershipRenderer(event
                                .getGroupId(), event.getGroupShortName()), event.getResponse(),
                                "No membership requests");
                        responseEvent.setWidget(responsePanel);
                        EventBus.getInstance().notifyObservers(responseEvent);
                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        groupId = event.getResponse().getId();
                        groupShortName = event.getResponse().getShortName();
                        EventBus.getInstance().removeObserver(GotGroupModelViewInformationResponseEvent.class, this);
                    }
                });

    }

    /**
     * Initialize.
     */
    public void init()
    {
        GroupMembershipRequestModel.getInstance().fetch(
                new GetRequestForGroupMembershipRequest(groupId, groupShortName, pager.getStartItem(), pager
                        .getEndItem()), false);
    }

    /**
     * @return The key.
     */
    public String getKey()
    {
        return "groupMembershipReq";
    }

    /**
     * @return if has next page values.
     */
    public boolean hasNext()
    {
        return pager.isNextPageable();
    }

    /**
     * @return if has previous page values.
     */
    public boolean hasPrev()
    {
        return pager.isPreviousPageable();
    }

    /**
     * Next.
     */
    public void next()
    {
        pager.nextPage();
        GroupMembershipRequestModel.getInstance().fetch(
                new GetRequestForGroupMembershipRequest(groupId, groupShortName, pager.getStartItem(), pager
                        .getEndItem()), false);
    }

    /**
     * Previous.
     */
    public void prev()
    {
        pager.previousPage();
        GroupMembershipRequestModel.getInstance().fetch(
                new GetRequestForGroupMembershipRequest(groupId, groupShortName, pager.getStartItem(), pager
                        .getEndItem()), false);
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
