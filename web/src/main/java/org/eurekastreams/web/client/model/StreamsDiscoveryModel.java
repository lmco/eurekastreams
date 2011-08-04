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
package org.eurekastreams.web.client.model;

import java.io.Serializable;

import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.web.client.events.data.GotStreamDiscoverListsDTOResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for fetching the lists for the Stream Discovery page. Note: you might notice that fetch is being called once no
 * matter what page is current - but only for admins, because admins need to have the featured list because of the
 * feature/unfeature button on each stream. In other words - it's a performance trade-off for admins - not a concern.
 */
public class StreamsDiscoveryModel extends BaseModel implements Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static StreamsDiscoveryModel model = new StreamsDiscoveryModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static StreamsDiscoveryModel getInstance()
    {
        return model;
    }

    /**
     * Fetch a StreamDiscoverListsDTO response from the server, fire a GotStreamDiscoverListsDTOResponseEvent.
     *
     * @param inRequest
     *            the request - ignored
     * @param inUseClientCacheIfAvailable
     *            whether to use cache
     */
    public void fetch(final Serializable inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getStreamDiscoverListsDTOAction", null, new OnSuccessCommand<StreamDiscoverListsDTO>()
        {
            public void onSuccess(final StreamDiscoverListsDTO response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotStreamDiscoverListsDTOResponseEvent(response));
            }
        }, inUseClientCacheIfAvailable);
    }
}
