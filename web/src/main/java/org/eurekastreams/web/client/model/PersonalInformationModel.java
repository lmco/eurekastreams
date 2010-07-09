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
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalInformationResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Represents the model for personal information, used to drive things like the profile page and personal settings.
 * TODO: refactor to change to PersonModelView for response.
 *
 */
public class PersonalInformationModel extends BaseModel implements Fetchable<String>,
        Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static PersonalInformationModel model = new PersonalInformationModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PersonalInformationModel getInstance()
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
                Session.getInstance().getEventBus().notifyObservers(new GotPersonalInformationResponseEvent(response));
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
                                new UpdatedPersonalInformationResponseEvent(response));
                    }
                });

        StreamSearchListModel.getInstance().clearCache();
        StreamViewListModel.getInstance().clearCache();
    }

}
