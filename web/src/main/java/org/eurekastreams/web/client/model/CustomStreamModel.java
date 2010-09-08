/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
import java.util.List;

import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Custom stream model.
 *
 */
public class CustomStreamModel extends BaseModel implements Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static CustomStreamModel model = new CustomStreamModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static CustomStreamModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        List<StreamFilter> streams = new ArrayList<StreamFilter>();

        Stream everyone = new Stream();
        everyone.setRequest("{ query : {} }");
        everyone.setName("Everyone");
        everyone.setReadOnly(true);
        streams.add(everyone);

        Stream following = new Stream();
        following.setRequest("{ query : { followedBy: \"romanoa1\" } }");
        following.setName("Following");
        following.setReadOnly(true);
        streams.add(following);

        Session.getInstance().getEventBus().notifyObservers(
                new GotCurrentUserCustomStreamsResponseEvent(new GetCurrentUserStreamFiltersResponse(1, streams)));

    }
}
