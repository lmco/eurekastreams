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

import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest;
import org.eurekastreams.web.client.events.ActivityLikedChangeEvent;
import org.eurekastreams.web.client.events.EventBus;

/**
 * Model used to like and unlike an activity.
 *
 */
public class ActivityLikeModel extends BaseModel implements Updateable<SetActivityLikeRequest>
{
    /**
     * Singleton.
     */
    private static ActivityLikeModel model = new ActivityLikeModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static ActivityLikeModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void update(final SetActivityLikeRequest request)
    {
        super.callWriteAction("setActivityLiked", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                EventBus.getInstance().notifyObservers(
                        new ActivityLikedChangeEvent(request.getLikeActionType(), request.getActivityId()));
            }
        });
    }

}
