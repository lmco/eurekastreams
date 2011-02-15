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

import java.util.ArrayList;

import org.eurekastreams.server.action.request.PersonLookupRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.data.GotPersonLookupResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for looking up people.
 */
public class PersonLookupModel extends BaseModel implements Fetchable<PersonLookupRequest>
{
    /** Singleton. */
    private static PersonLookupModel model = new PersonLookupModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PersonLookupModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final PersonLookupRequest inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("personLookup", inRequest, new OnSuccessCommand<ArrayList<PersonModelView>>()
        {
            public void onSuccess(final ArrayList<PersonModelView> inResponse)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotPersonLookupResponseEvent(inResponse));
            }
        }, inUseClientCacheIfAvailable);
    }
}
