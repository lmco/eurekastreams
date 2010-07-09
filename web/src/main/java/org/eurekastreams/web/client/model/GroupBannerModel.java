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

import org.eurekastreams.server.domain.Bannerable;
import org.eurekastreams.web.client.events.data.DeleteGroupBannerResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * client model for GroupBannerModel.
 *
 */
public class GroupBannerModel extends BaseModel implements Deletable<Long>
{
    /**
     * Singleton.
     */
    private static GroupBannerModel model = new GroupBannerModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static GroupBannerModel getInstance()
    {
        return model;
    }

    /**
     * Delete.
     * @param request  the request.
     */
    public void delete(final Long request)
    {
        super.callWriteAction("deleteDomainGroupBanner", request, new OnSuccessCommand<Bannerable>()
        {
            public void onSuccess(final Bannerable response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeleteGroupBannerResponseEvent(response));
            }
        });
    }
}
