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

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedSystemSettingsResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * System Settings Model.
 * 
 */
public class SystemSettingsModel extends BaseModel implements Fetchable<Serializable>,
        Updateable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static SystemSettingsModel model = new SystemSettingsModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static SystemSettingsModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void update(final HashMap<String, Serializable> request)
    {
        super.callWriteAction("updateSystemSettings", request, new OnSuccessCommand<SystemSettings>()
        {
            public void onSuccess(final SystemSettings response)
            {
                Session.getInstance().getEventBus().notifyObservers(new UpdatedSystemSettingsResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable inShouldLoadAdmins, final boolean useClientCacheIfAvailable)
    {
        Boolean shouldLoadAdmins = (Boolean) inShouldLoadAdmins;
        super.callReadAction("getSystemSettings", shouldLoadAdmins, new OnSuccessCommand<SystemSettings>()
        {
            public void onSuccess(final SystemSettings response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotSystemSettingsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }
}
