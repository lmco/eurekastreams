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

import org.eurekastreams.server.action.request.profile.GetPendingGroupsRequest;
import org.eurekastreams.server.action.request.profile.ReviewPendingGroupRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.data.GotPendingGroupsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedReviewPendingGroupResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Pending Group model.
 *
 */
public class PendingGroupsModel extends BaseModel implements Fetchable<GetPendingGroupsRequest>,
        Updateable<ReviewPendingGroupRequest>
{

    /**
     * Singleton.
     */
    private static PendingGroupsModel model = new PendingGroupsModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PendingGroupsModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetPendingGroupsRequest request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getPendingGroupsAction", request, new OnSuccessCommand<PagedSet<DomainGroupModelView>>()
        {
            public void onSuccess(final PagedSet<DomainGroupModelView> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotPendingGroupsResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final ReviewPendingGroupRequest request)
    {
        super.callWriteAction("reviewPendingGroupsAction", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdatedReviewPendingGroupResponseEvent(request));
            }
        });
    }

}
