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

import java.util.ArrayList;

import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.GotStreamPopularHashTagsEvent;

/**
 * Popular hash tags model.
 */
public class PopularHashTagsModel extends BaseModel implements Fetchable<StreamPopularHashTagsRequest>
{
    /**
     * Singleton.
     */
    private static PopularHashTagsModel model = new PopularHashTagsModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static PopularHashTagsModel getInstance()
    {
        return model;
    }

    /**
     * Fetch the popular hash tags.
     * 
     * @param request
     *            the request.
     * @param useClientCacheIfAvailable
     *            if cache should be used.
     */
    public void fetch(final StreamPopularHashTagsRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getPopularHashTagsForStream", request, new OnSuccessCommand<ArrayList<String>>()
        {
            public void onSuccess(final ArrayList<String> response)
            {
                EventBus.getInstance().notifyObservers(new GotStreamPopularHashTagsEvent(response));
            }
        }, useClientCacheIfAvailable);

    }
}
