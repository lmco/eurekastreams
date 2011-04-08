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
import java.util.LinkedList;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.data.GotResourceActorsEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.renderers.ResourceCountWidget.CountType;

/**
 * Resource actors model.
 */
public class GetResourceActorsModel extends BaseModel implements Fetchable<Serializable>
{

    /**
     * Singleton.
     */
    private static GetResourceActorsModel model = new GetResourceActorsModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static GetResourceActorsModel getInstance()
    {
        return model;
    }

    /**
     * Fetch the resource actors.
     * 
     * @param request
     *            the request.
     * @param useClientCacheIfAvailable
     *            if cache should be used.
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        HashMap<String, Serializable> reqMap = (HashMap<String, Serializable>) request;

        String actionKey = (reqMap.get("type") == CountType.LIKES) ? "getPeopleThatLikedSharedResourceAction"
                : "getPeopleThatSharedSharedResourceAction";

        super.callReadAction(actionKey, new SharedResourceRequest((String) reqMap.get("resourceUrl"), Session
                .getInstance().getCurrentPerson().getEntityId()), new OnSuccessCommand<LinkedList<PersonModelView>>()
        {
            public void onSuccess(final LinkedList<PersonModelView> response)
            {
                EventBus.getInstance().notifyObservers(new GotResourceActorsEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
