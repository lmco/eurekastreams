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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.data.GotCurrentUserStreamBookmarks;

/**
 * Custom stream model.
 * 
 */
public class StreamBookmarksModel extends BaseModel implements Fetchable<Serializable>,
        Insertable<HashMap<String, Serializable>>, Updateable<HashMap<String, Serializable>>, Deletable<StreamScope>
{
    /**
     * Singleton.
     */
    private static StreamBookmarksModel model = new StreamBookmarksModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static StreamBookmarksModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getCurrentUsersBookmarks", request, new OnSuccessCommand<ArrayList<StreamFilter>>()
        {
            public void onSuccess(final ArrayList<StreamFilter> response)
            {
                EventBus.getInstance().notifyObservers(new GotCurrentUserStreamBookmarks(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("insertStreamBookmark", request.get("bookmark"), new OnSuccessCommand<StreamScope>()
        {
            public void onSuccess(final StreamScope response)
            {
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {

    }

    /**
     * {@inheritDoc}
     */
    public void delete(final StreamScope request)
    {

    }
}
