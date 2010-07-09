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

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.web.client.events.data.InsertedRootOrganizationResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Root Organization Model.
 *
 */
public class RootOrganizationModel extends BaseModel implements Insertable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static RootOrganizationModel model = new RootOrganizationModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static RootOrganizationModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("createRootOrganization", request, new OnSuccessCommand<Organization>()
        {
            public void onSuccess(final Organization response)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new InsertedRootOrganizationResponseEvent(response));
            }
        });
    }

}
