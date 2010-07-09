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

import org.eurekastreams.web.client.events.InsertOptOutVideoResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * client model for the opt out videos.
 *
 */
public class OptOutVideosModel extends BaseModel implements Insertable<Long>
{
    /**
     * Singleton.
     */
    private static OptOutVideosModel model = new OptOutVideosModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static OptOutVideosModel getInstance()
    {
        return model;
    }

    /**
     * call the super class to go to the db via the action on insert.
     *
     * @param request
     *            the request to take in.
     */
    public void insert(final Long request)
    {
        super.callWriteAction("insertOptOutVideo", request, new OnSuccessCommand<Long>()
        {
            public void onSuccess(final Long response)
            {
                Session.getInstance().getEventBus().notifyObservers(new InsertOptOutVideoResponseEvent(response));
            }
        });
    }
}
