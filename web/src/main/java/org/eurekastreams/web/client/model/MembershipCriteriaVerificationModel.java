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

import java.util.ArrayList;

import org.eurekastreams.server.action.request.GroupLookupRequest;
import org.eurekastreams.server.action.request.PersonLookupRequest;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MembershipCriteriaAddedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationFailureEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationNoUsersEvent;
import org.eurekastreams.web.client.model.requests.MembershipCriteriaVerificationRequest;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for verifying looking up people.
 */
public class MembershipCriteriaVerificationModel extends BaseModel implements
        Fetchable<MembershipCriteriaVerificationRequest>
{
    /**
     * The maximum number of results to return from a lookup attempt. (We should keep it low for performance since we
     * just need to know the criterion is valid, however the way the LDAP code is working, this must be as big as a page
     * size otherwise it will throw an exception about there being too many records.)
     */
    private static final int MAX_RESULTS = 100;

    /** Singleton. */
    private static MembershipCriteriaVerificationModel model = new MembershipCriteriaVerificationModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static MembershipCriteriaVerificationModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final MembershipCriteriaVerificationRequest inRequest, final boolean inUseClientCacheIfAvailable)
    {
        final EventBus eventBus = Session.getInstance().getEventBus();
        final String criterion = inRequest.getCriteria();

        if (inRequest.isGroup())
        {
            super.callReadAction("groupLookup", new GroupLookupRequest(criterion),
                    new OnSuccessCommand<Boolean>()
                    {
                        public void onSuccess(final Boolean wasFound)
                        {
                    sendSuccessEvent(criterion, wasFound);
                        }
                    }, new OnFailureCommand()
                    {
                        public void onFailure(final Throwable inEx)
                        {
                            eventBus.notifyObservers(new MembershipCriteriaVerificationFailureEvent());
                        }
                    }, inUseClientCacheIfAvailable);
        }
        else
        {
            super.callReadAction("personLookupOrg", new PersonLookupRequest(criterion, MAX_RESULTS),
                    new OnSuccessCommand<ArrayList<PersonModelView>>()
                    {
                        public void onSuccess(final ArrayList<PersonModelView> people)
                        {
                            sendSuccessEvent(criterion, !people.isEmpty());
                        }
                    }, new OnFailureCommand()
                    {
                        public void onFailure(final Throwable inEx)
                        {
                            eventBus.notifyObservers(new MembershipCriteriaVerificationFailureEvent());
                        }
                    }, inUseClientCacheIfAvailable);
        }
    }

    /**
     * Sends event on successful response.
     *
     * @param criterion
     *            Requested criterion.
     * @param found
     *            If any query results found.
     */
    private void sendSuccessEvent(final String criterion, final boolean found)
    {
        final EventBus eventBus = Session.getInstance().getEventBus();
        if (found)
        {
            eventBus.notifyObservers(new MembershipCriteriaAddedEvent(new MembershipCriteria(criterion), true));
        }
        else
        {
            eventBus.notifyObservers(new MembershipCriteriaVerificationNoUsersEvent());
        }
    }
}
