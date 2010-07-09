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

import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.events.data.GotOrganizationModelViewResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for organization model views and fetchable by ID.
 */
// TODO: Danger here with caching - if changes are made via OrganizationModel this model will have stale data. Really
// the two should be merged.
public class OrganizationModelViewModel extends BaseModel implements Fetchable<Long>
{
    /**
     * Singleton.
     */
    private static OrganizationModelViewModel model = new OrganizationModelViewModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static OrganizationModelViewModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Long request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getOrganizationById", request, new OnSuccessCommand<OrganizationModelView>()
        {
            public void onSuccess(final OrganizationModelView response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotOrganizationModelViewResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
