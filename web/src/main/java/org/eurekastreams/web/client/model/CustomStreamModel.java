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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;
import org.eurekastreams.web.client.events.CustomStreamCreatedEvent;
import org.eurekastreams.web.client.events.CustomStreamDeletedEvent;
import org.eurekastreams.web.client.events.CustomStreamUpdatedEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

/**
 * Custom stream model.
 * 
 */
public class CustomStreamModel extends BaseModel implements Fetchable<Serializable>,
        Insertable<HashMap<String, Serializable>>, Updateable<HashMap<String, Serializable>>, Deletable<Stream>,
        Reorderable<SetStreamFilterOrderRequest>
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
        super.callReadAction("getCurrentUsersStreams", request, new OnSuccessCommand<ArrayList<StreamFilter>>()
        {
            public void onSuccess(final ArrayList<StreamFilter> streams)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotCurrentUserCustomStreamsResponseEvent(
                                new GetCurrentUserStreamFiltersResponse(1, streams)));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        JSONObject json = getJSONFromHashMap(request);
        Window.alert(json.toString());
        Stream everyone = new Stream();
        everyone.setRequest(json.toString());
        everyone.setName((String) request.get("name"));
        everyone.setReadOnly(false);
        everyone.setId(3L);

        Session.getInstance().getEventBus().notifyObservers(new CustomStreamCreatedEvent(everyone));

    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        JSONObject json = getJSONFromHashMap(request);
        Window.alert(json.toString());
        Stream everyone = new Stream();
        everyone.setRequest(json.toString());
        everyone.setName((String) request.get("name"));
        everyone.setReadOnly(false);
        everyone.setId(3L);

        Session.getInstance().getEventBus().notifyObservers(new CustomStreamUpdatedEvent(everyone));
    }

    /**
     * Converts the form hashmap to the JSON request object.
     * @param request the form request.
     * @return the JSON request.
     */
    private JSONObject getJSONFromHashMap(final HashMap<String, Serializable> request)
    {
        JSONObject jsonObject = StreamJsonRequestFactory.getEmptyRequest();

        if (!"".equals(request.get("keywords")))
        {
            jsonObject = StreamJsonRequestFactory.setSearchTerm((String) request.get("keywords"), jsonObject);
        }

        if (request.get("stream") instanceof String)
        {
            if (request.get("stream").equals("Following"))
            {
                jsonObject = StreamJsonRequestFactory.setSourceAsFollowing(jsonObject);
            }
            else if (request.get("stream").equals("Saved"))
            {
                jsonObject = StreamJsonRequestFactory.setSourceAsSaved(jsonObject);
            }
            else if (request.get("stream").equals("parentOrg"))
            {
                jsonObject = StreamJsonRequestFactory.setSourceAsParentOrg(jsonObject);
            }
        }
        else
        {
            List<StreamScope> scopes = (LinkedList<StreamScope>) request.get("stream");

            for (StreamScope scope : scopes)
            {
                jsonObject = StreamJsonRequestFactory.addRecipient(EntityType.valueOf(scope.getScopeType().toString()),
                        scope.getUniqueKey(), jsonObject);
            }
        }

        return jsonObject;
    }
    /**
     * {@inheritDoc}
     */
    public void delete(final Stream request)
    {
        Session.getInstance().getEventBus().notifyObservers(new CustomStreamDeletedEvent(request));
    }

    /**
     * {@inheritDoc}
     */
    public void reorder(final SetStreamFilterOrderRequest request)
    {
        super.callWriteAction("setStreamViewOrder", request, null);
    }
}
