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

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotActivityResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Activity Model.
 *
 */
public class ActivityModel extends BaseModel implements Fetchable<Long>, Deletable<Long>
{
    /**
     * Singleton.
     */
    private static ActivityModel model = new ActivityModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static ActivityModel getInstance()
    {
        return model;
    }

    /**
     * Retrieves a list of activities for the org.
     * 
     * @param inRequest
     *            Request.
     * @param inUseClientCacheIfAvailable
     *            If ok to return cached results.
     */
    public void fetch(final Long inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getActivityById", inRequest,
                new OnSuccessCommand<ActivityDTO>()
                {
                    public void onSuccess(final ActivityDTO response)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new GotActivityResponseEvent(response));
                    }
                }, inUseClientCacheIfAvailable);
    }

    /**
     * Deletes an activity.
     * 
     * @param request
     *            Activity id.
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteActivityAction", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedActivityResponseEvent(request));
            }
        });
    }
}
