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

import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.web.client.events.data.GotBulkEntityResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Get a bunch of people.
 *
 */
public class BulkEntityModel extends BaseModel implements Fetchable<ArrayList<StreamEntityDTO>>
{
    /**
     * Singleton.
     */
    private static BulkEntityModel model = new BulkEntityModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static BulkEntityModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final ArrayList<StreamEntityDTO> request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getBulkEntities", request, new OnSuccessCommand<ArrayList<Serializable>>()
        {
            public void onSuccess(final ArrayList<Serializable> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotBulkEntityResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
