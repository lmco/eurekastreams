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
import java.util.HashMap;

import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;
import org.eurekastreams.web.client.events.CustomStreamCreatedEvent;
import org.eurekastreams.web.client.events.CustomStreamDeletedEvent;
import org.eurekastreams.web.client.events.CustomStreamUpdatedEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Custom stream model.
 * 
 */
public class CustomStreamModel extends BaseModel implements Fetchable<Serializable>,
        Insertable<HashMap<String, Serializable>>, Updateable<HashMap<String, Serializable>>, Deletable<Stream>
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
        super.callReadAction("getCurrentUsersStreams", request,
                new OnSuccessCommand<GetCurrentUserStreamFiltersResponse>()
                {
                    public void onSuccess(final GetCurrentUserStreamFiltersResponse response)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new GotCurrentUserCustomStreamsResponseEvent(response));
                    }
                }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        JSONObject json = JSONParser.parse((String) request.get("stream")).isObject();

        if (!"".equals(request.get(StreamJsonRequestFactory.SEARCH_KEY)))
        {
            json = StreamJsonRequestFactory.setSearchTerm((String) request.get(StreamJsonRequestFactory.SEARCH_KEY),
                    json);
        }

        Stream stream = new Stream();
        stream.setRequest(json.toString());
        stream.setName((String) request.get("name"));
        stream.setId(0L);

        super.callWriteAction("modifyStreamForCurrentUser", stream, new OnSuccessCommand<Stream>()
        {
            public void onSuccess(final Stream response)
            {
                Session.getInstance().getEventBus().notifyObservers(new CustomStreamCreatedEvent(response));
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        JSONObject json = JSONParser.parse((String) request.get("stream")).isObject();

        if (!"".equals(request.get(StreamJsonRequestFactory.SEARCH_KEY)))
        {
            json = StreamJsonRequestFactory.setSearchTerm((String) request.get(StreamJsonRequestFactory.SEARCH_KEY),
                    json);
        }

        Stream stream = new Stream();
        stream.setRequest(json.toString());
        stream.setName((String) request.get("name"));
        stream.setId((Long) request.get("id"));

        super.callWriteAction("modifyStreamForCurrentUser", stream, new OnSuccessCommand<Stream>()
        {
            public void onSuccess(final Stream response)
            {
                Session.getInstance().getEventBus().notifyObservers(new CustomStreamUpdatedEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Stream request)
    {
        super.callWriteAction("deleteStreamForCurrentUser", request.getId(), new OnSuccessCommand<Long>()
        {
            public void onSuccess(final Long response)
            {
                Session.getInstance().getEventBus().notifyObservers(new CustomStreamDeletedEvent(request));
            }
        });
    }
}
