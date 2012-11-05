/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.model;

import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.data.DeletedGroupMemberResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupMembersResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowerStatusResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGroupMemberResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Group Members Model.
 *
 */
public class GroupMembersModel extends BaseModel implements Fetchable<GetFollowersFollowingRequest>,
        Insertable<SetFollowingStatusRequest>, Deletable<SetFollowingStatusRequest>
{
    /**
     * Singleton.
     */
    private static GroupMembersModel model = new GroupMembersModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static GroupMembersModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetFollowersFollowingRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getGroupFollowers", request, new OnSuccessCommand<PagedSet<PersonModelView>>()
        {
            public void onSuccess(final PagedSet<PersonModelView> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotGroupMembersResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final SetFollowingStatusRequest request)
    {
        super.callWriteAction("setFollowingStatusGroupTaskHandler", request, new OnSuccessCommand<Integer>()
        {
            public void onSuccess(final Integer response)
            {
                // the user added could have been one who made a membership request, so clear the request model
                GroupMembershipRequestModel.getInstance().clearCache();
                StreamsDiscoveryModel.getInstance().clearCache();

                // clear following model
                CurrentUserPersonFollowingStatusModel.getInstance().clearCache();

                EventBus eventBus = Session.getInstance().getEventBus();
                eventBus.notifyObservers(new InsertedGroupMemberResponseEvent(request, response));

                // simulate a status request for recipients that don't listen for changes. Event type has person in the
                // name, but that is the event used when querying groups
                eventBus.notifyObservers(new GotPersonFollowerStatusResponseEvent(FollowerStatus.FOLLOWING));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final SetFollowingStatusRequest request)
    {
        super.callWriteAction("setFollowingStatusGroupTaskHandler", request, new OnSuccessCommand<Integer>()
        {
            public void onSuccess(final Integer response)
            {
                // clear following model
                CurrentUserPersonFollowingStatusModel.getInstance().clearCache();
                StreamsDiscoveryModel.getInstance().clearCache();

                EventBus eventBus = Session.getInstance().getEventBus();
                eventBus.notifyObservers(new DeletedGroupMemberResponseEvent(request, response));

                // simulate a status request for recipients that don't listen for changes. Event type has person in the
                // name, but that is the event used when querying groups
                eventBus.notifyObservers(new GotPersonFollowerStatusResponseEvent(FollowerStatus.NOTFOLLOWING));
                
                // In the event that a User is removed as a Follower of a Group,
                // then it's necessary to update the UI to see that
                // the removed User is no longer is a member of this Group, and that
                // the Followers Count decrements by 1.
                if (!request.getFollowerUniqueId().equals(
                        Session.getInstance().getCurrentPerson().getAccountId()))
                {   
                    // Update "Followers" tab so that the removed
                    // follower no longer appears.
                    BasicPager pager = new BasicPager();
                    fetch(new GetFollowersFollowingRequest(EntityType.GROUP, 
                        request.getGroupShortName(), pager.getStartItem(), pager.getEndItem()), false);
                    
                    // Update the Group 'Followers' #
                    GroupModel.getInstance().fetch(request.getGroupShortName(), false);
                }
            }
        });
    }
}
