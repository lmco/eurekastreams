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

import org.eurekastreams.web.client.events.data.DeletedGroupAvatarResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Group avatar model.
 *
 */
public class GroupAvatarModel extends BaseModel implements Deletable<Long>
{
    /**
     * Singleton.
     */
    private static GroupAvatarModel model = new GroupAvatarModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static GroupAvatarModel getInstance()
    {
        return model;
    }

    /**
     * Delete method.
     *
     * @param request
     *            the request.
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteGroupAvatar", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedGroupAvatarResponseEvent(response));
            }
        });
    }
}
