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

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.server.action.request.feed.DeleteFeedSubscriptionRequest;
import org.eurekastreams.server.action.response.feed.PluginAndFeedSubscriptionsResponse;
import org.eurekastreams.web.client.events.data.DeletedStreamPluginSubscriptionResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamPluginSubscriptionsResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedStreamPluginSubscriptionResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedStreamPluginSubscriptionResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * GroupStreamPluginSubscriptionModel.
 */
public class GroupStreamPluginSubscriptionModel  extends BaseModel implements
	Fetchable<String>, 
	Insertable<HashMap<String, Serializable>>, 
	Deletable<DeleteFeedSubscriptionRequest>, 
	Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static GroupStreamPluginSubscriptionModel model = new GroupStreamPluginSubscriptionModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static GroupStreamPluginSubscriptionModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final String request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction(
        		"getPluginsAndSubscriptionsForGroup", 
        		request, 
        		new OnSuccessCommand<PluginAndFeedSubscriptionsResponse>()
        {
            public void onSuccess(final PluginAndFeedSubscriptionsResponse response)
            {
                Session.getInstance().getEventBus().
                	notifyObservers(new GotStreamPluginSubscriptionsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("persistPluginForGroup", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().
                	notifyObservers(new InsertedStreamPluginSubscriptionResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final DeleteFeedSubscriptionRequest request)
    {
        super.callWriteAction("deletePluginForGroup", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().
                	notifyObservers(new DeletedStreamPluginSubscriptionResponseEvent(response));
            }
        });
    }

    /**
     * Rename a tab.
     *
     * @param request
     *            the request.
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("persistPluginForGroup", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdatedStreamPluginSubscriptionResponseEvent(response));
            }
        });
    }

}
