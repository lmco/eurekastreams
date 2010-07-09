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
package org.eurekastreams.web.client.model;

import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.data.DeletedPersonFollowersResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowersResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPersonFollowerResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * People followers model.
 *
 */
public class PersonFollowersModel extends BaseModel implements Fetchable<GetFollowersFollowingRequest>,
        Insertable<SetFollowingStatusRequest>, Deletable<SetFollowingStatusRequest>
{
    /**
     * Singleton.
     */
    private static PersonFollowersModel model = new PersonFollowersModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PersonFollowersModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetFollowersFollowingRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getPersonFollowers", request, new OnSuccessCommand<PagedSet<PersonModelView>>()
        {
            public void onSuccess(final PagedSet<PersonModelView> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotPersonFollowersResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final SetFollowingStatusRequest request)
    {
        super.callWriteAction("setFollowingStatusPersonTaskHandler", request, new OnSuccessCommand<Integer>()
        {
            public void onSuccess(final Integer response)
            {
                Session.getInstance().getEventBus().notifyObservers(new InsertedPersonFollowerResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final SetFollowingStatusRequest request)
    {
        super.callWriteAction("setFollowingStatusPersonTaskHandler", request, new OnSuccessCommand<Integer>()
        {
            public void onSuccess(final Integer response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedPersonFollowersResponseEvent(response));
            }
        });
    }

}
