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

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.events.data.GotPersonalStreamSettingsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalStreamSettingsResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Represents the model for personal stream settings, used to drive personal stream settings tab.
 */
public class PersonalStreamSettingsModel extends BaseModel implements Fetchable<String>,
        Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static PersonalStreamSettingsModel model = new PersonalStreamSettingsModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static PersonalStreamSettingsModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final String request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getPerson", request, new OnSuccessCommand<Person>()
        {
            public void onSuccess(final Person response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotPersonalStreamSettingsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("updatePerson", request, new OnSuccessCommand<Person>()
        {
            public void onSuccess(final Person response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdatedPersonalStreamSettingsResponseEvent(response));
            }
        });

        CustomStreamModel.getInstance().clearCache();
    }

}
