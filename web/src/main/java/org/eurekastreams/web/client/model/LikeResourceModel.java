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
import java.util.HashMap;

import org.eurekastreams.server.action.request.stream.SetSharedResourceLikeRequest;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.data.GotResourceDTOResponseEvent;
import org.eurekastreams.web.client.events.data.ResourceLikeChangeEvent;

/**
 * The like resource mode.
 */
public class LikeResourceModel extends BaseModel implements Fetchable<Serializable>,
        Insertable<HashMap<String, Serializable>>
{

    /**
     * Singleton.
     */
    private static LikeResourceModel model = new LikeResourceModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static LikeResourceModel getInstance()
    {
        return model;
    }

    /**
     * Fetch the shared resource DTO.
     * 
     * @param request
     *            the request.
     * @param useClientCacheIfAvailable
     *            if cache should be used.
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getSharedResourceByKeyAction", request, new OnSuccessCommand<SharedResourceDTO>()
        {
            public void onSuccess(final SharedResourceDTO response)
            {
                EventBus.getInstance().notifyObservers(new GotResourceDTOResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * Insert a liker.
     * 
     * @param request
     *            the request.
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        final SetSharedResourceLikeRequest likeRequest = new SetSharedResourceLikeRequest((String) request
                .get("resourceurl"), (Boolean) request.get("liked"));

        super.callWriteAction("setSharedResourceLiked", likeRequest, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                EventBus.getInstance().notifyObservers(new ResourceLikeChangeEvent(likeRequest.getLikes()));
            }
        });
    }
}
