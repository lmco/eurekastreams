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
package org.eurekastreams.web.client.ui.pages.activity;

import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotPersonFollowersResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowingResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonJoinedGroupsResponseEvent;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.model.PersonFollowingModel;
import org.eurekastreams.web.client.model.PersonJoinedGroupsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.pagedlist.GroupRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;

/**
 * Get a person's following tab.
 */
public final class GetPersonFollowingTab
{
    /**
     * Constructor.
     */
    private GetPersonFollowingTab()
    {
    };

    /**
     * Creates and sets up the connections tab content.
     * 
     * @param inPerson
     *            Person whose profile is being displayed.
     * @return Tab content.
     */
    public static PagedListPanel getFollowingTab(final PersonModelView inPerson)
    {
        final PagedListPanel connectionTabContent = new PagedListPanel("connections");

        Session.getInstance().getEventBus().addObserver(GotPersonFollowersResponseEvent.class,
                new Observer<GotPersonFollowersResponseEvent>()
                {
                    public void update(final GotPersonFollowersResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "No one is following this person");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotPersonFollowingResponseEvent.class,
                new Observer<GotPersonFollowingResponseEvent>()
                {
                    public void update(final GotPersonFollowingResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "This person is not following anyone");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotPersonJoinedGroupsResponseEvent.class,
                new Observer<GotPersonJoinedGroupsResponseEvent>()
                {
                    public void update(final GotPersonJoinedGroupsResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "This person is not following any groups");
                    }
                });

        connectionTabContent.addSet("Followers", PersonFollowersModel.getInstance(), new PersonRenderer(false),
                new GetFollowersFollowingRequest(EntityType.PERSON, inPerson.getAccountId(), 0, 0));

        connectionTabContent.addSet("Following", PersonFollowingModel.getInstance(), new PersonRenderer(false),
                new GetFollowersFollowingRequest(EntityType.PERSON, inPerson.getAccountId(), 0, 0));

        connectionTabContent.addSet("Groups", PersonJoinedGroupsModel.getInstance(), new GroupRenderer(),
                new GetFollowersFollowingRequest(EntityType.GROUP, inPerson.getAccountId(), 0, 0));

        return connectionTabContent;
    }
}
