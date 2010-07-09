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

import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.web.client.events.data.DeletedPersonalEducationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalEducationResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPersonalEducationResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalEducationResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Represents the model for personal education.
 */
public class PersonalEducationModel extends BaseModel implements Fetchable<Long>, Deletable<Long>,
        Insertable<HashMap<String, Serializable>>, Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static PersonalEducationModel model = new PersonalEducationModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PersonalEducationModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Long request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getEducation", request, new OnSuccessCommand<LinkedList<Enrollment>>()
        {
            public void onSuccess(final LinkedList<Enrollment> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotPersonalEducationResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("persistEducation", request, new OnSuccessCommand<Enrollment>()
        {
            public void onSuccess(final Enrollment response)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new UpdatedPersonalEducationResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteEducation", request, new OnSuccessCommand<Boolean>()
                {
                    public void onSuccess(final Boolean response)
                    {
                        Session.getInstance().getEventBus()
                                .notifyObservers(new DeletedPersonalEducationResponseEvent(response));
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("persistEducation", request, new OnSuccessCommand<Enrollment>()
                {
                    public void onSuccess(final Enrollment response)
                    {
                        Session.getInstance().getEventBus()
                                .notifyObservers(new InsertedPersonalEducationResponseEvent(response));
                    }
                });
    }

}
