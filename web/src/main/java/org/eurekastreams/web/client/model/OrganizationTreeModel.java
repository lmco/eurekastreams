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

import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.web.client.events.data.GotOrganizationTreeResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model to retrieve a summary of the entire org tree.
 */
public class OrganizationTreeModel extends BaseModel implements Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static OrganizationTreeModel model = new OrganizationTreeModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static OrganizationTreeModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getOrganizationTree", null, new OnSuccessCommand<OrganizationTreeDTO>()
        {
            public void onSuccess(final OrganizationTreeDTO response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotOrganizationTreeResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
