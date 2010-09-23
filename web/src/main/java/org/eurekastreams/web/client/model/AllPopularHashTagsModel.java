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
import java.util.HashSet;

import org.eurekastreams.web.client.events.data.GotAllPopularHashTagsResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Get all the popular hashtags.
 *
 */
public class AllPopularHashTagsModel extends BaseModel implements Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static AllPopularHashTagsModel model = new AllPopularHashTagsModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static AllPopularHashTagsModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getAllPopularHashTags", null, new OnSuccessCommand<HashSet<String>>()
        {
            public void onSuccess(final HashSet<String> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotAllPopularHashTagsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
