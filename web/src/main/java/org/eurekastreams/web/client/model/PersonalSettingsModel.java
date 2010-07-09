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
import java.util.HashMap;

import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;
import org.eurekastreams.web.client.events.data.GotPersonalSettingsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalSettingsResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Personal Settings model.
 */
public class PersonalSettingsModel extends BaseModel implements Updateable<HashMap<String, Serializable>>,
        Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static PersonalSettingsModel model = new PersonalSettingsModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PersonalSettingsModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("updatePersonalSettings", request, new OnSuccessCommand<Serializable>()
        {
            public void onSuccess(final Serializable response)
            {
                Session.getInstance().getEventBus().notifyObservers(new UpdatedPersonalSettingsResponseEvent());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getPersonalSettings", request, new OnSuccessCommand<RetrieveSettingsResponse>()
        {
            public void onSuccess(final RetrieveSettingsResponse response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotPersonalSettingsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
