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
import java.util.LinkedList;

import org.eurekastreams.server.domain.Job;
import org.eurekastreams.web.client.events.data.DeletedPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Represents the model for personal Employment.
 */
public class PersonalEmploymentModel extends BaseModel implements Fetchable<Long>, Deletable<Long>,
        Insertable<HashMap<String, Serializable>>, Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static PersonalEmploymentModel model = new PersonalEmploymentModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PersonalEmploymentModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Long request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getEmployment", request, new OnSuccessCommand<LinkedList<Job>>()
        {
            public void onSuccess(final LinkedList<Job> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotPersonalEmploymentResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("persistEmployment", request, new OnSuccessCommand<Job>()
        {
            public void onSuccess(final Job response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdatedPersonalEmploymentResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteEmployment", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new DeletedPersonalEmploymentResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {

        super.callWriteAction("persistEmployment", request, new OnSuccessCommand<Job>()
        {
            public void onSuccess(final Job response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new InsertedPersonalEmploymentResponseEvent(response));
            }
        });
    }
}
