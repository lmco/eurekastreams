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

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.data.GotPersonalBiographyResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalBiographyResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Represents the model for personal biography.
 */
public class PersonalBiographyModel extends BaseModel implements Fetchable<String>,
        Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static PersonalBiographyModel model = new PersonalBiographyModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static PersonalBiographyModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final String request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getPersonModelView", request, new OnSuccessCommand<PersonModelView>()
        {
            public void onSuccess(final PersonModelView response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotPersonalBiographyResponseEvent(response.getBiography()));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("updateBiography", request, new OnSuccessCommand<String>()
        {
            public void onSuccess(final String response)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new UpdatedPersonalBiographyResponseEvent(response));
            }
        });

        PersonalInformationModel.getInstance().clearCache();

    }

}
