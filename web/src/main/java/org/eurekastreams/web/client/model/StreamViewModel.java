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

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.web.client.events.StreamViewCreatedEvent;
import org.eurekastreams.web.client.events.StreamViewDeletedEvent;
import org.eurekastreams.web.client.events.StreamViewUpdatedEvent;
import org.eurekastreams.web.client.events.data.GotCompleteStreamViewResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Stream View Model.
 *
 */
public class StreamViewModel extends BaseModel implements Insertable<HashMap<String, Serializable>>, Fetchable<Long>,
        Updateable<HashMap<String, Serializable>>, Deletable<StreamView>
{

    /**
     * Singleton.
     */
    private static StreamViewModel model = new StreamViewModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static StreamViewModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("createStreamView", request, new OnSuccessCommand<StreamView>()
        {
            public void onSuccess(final StreamView response)
            {
                Session.getInstance().getEventBus().notifyObservers(new StreamViewCreatedEvent(response));
                StreamViewListModel.getInstance().clearCache();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("updateStreamView", request, new OnSuccessCommand<StreamView>()
        {
            public void onSuccess(final StreamView response)
            {
                Session.getInstance().getEventBus().notifyObservers(new StreamViewUpdatedEvent(response));
                StreamViewListModel.getInstance().clearCache();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final StreamView request)
    {
        super.callWriteAction("deleteStreamView", request.getId(), new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new StreamViewDeletedEvent(request));
                StreamViewListModel.getInstance().clearCache();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Long request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getCompleteStreamView", request, new OnSuccessCommand<StreamView>()
        {
            public void onSuccess(final StreamView response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotCompleteStreamViewResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
