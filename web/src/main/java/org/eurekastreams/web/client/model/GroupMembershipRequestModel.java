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

import org.eurekastreams.server.action.request.DomainGroupShortNameRequest;
import org.eurekastreams.server.action.request.profile.GetRequestForGroupMembershipRequest;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.data.DeletedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.events.data.GotRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model handing group membership requests.
 */
public class GroupMembershipRequestModel extends BaseModel implements Fetchable<GetRequestForGroupMembershipRequest>,
        Deletable<RequestForGroupMembershipRequest>, Insertable<String>
{
    /**
     * Singleton.
     */
    private static GroupMembershipRequestModel model = new GroupMembershipRequestModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static GroupMembershipRequestModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final GetRequestForGroupMembershipRequest inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getRequestsForGroupMembershipByGroupAction", inRequest,
                new OnSuccessCommand<PagedSet<PersonModelView>>()
                {
                    public void onSuccess(final PagedSet<PersonModelView> response)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new GotRequestForGroupMembershipResponseEvent(inRequest.getGroupId(), inRequest
                                        .getGroupShortName(), response));
                    }
                }, inUseClientCacheIfAvailable);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final RequestForGroupMembershipRequest inRequest)
    {
        super.callWriteAction("deleteRequestForGroupMembershipAction", inRequest, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new DeletedRequestForGroupMembershipResponseEvent(inRequest, response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final String inRequest)
    {
        super.callWriteAction("sendGroupAccessRequestAction", new DomainGroupShortNameRequest(inRequest),
                new OnSuccessCommand<Boolean>()
                {
                    public void onSuccess(final Boolean response)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new InsertedRequestForGroupMembershipResponseEvent(inRequest, response));
                    }
                });
    }
}
