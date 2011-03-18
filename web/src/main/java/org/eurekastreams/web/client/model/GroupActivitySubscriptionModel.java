/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.ArrayList;

import org.eurekastreams.server.action.request.stream.ChangeGroupActivitySubscriptionRequest;
import org.eurekastreams.web.client.events.data.GotGroupActivitySubscriptionsResponseEvent;
import org.eurekastreams.web.client.events.data.GroupActivitySubscriptionChangedEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for un/subscribing a user to group notifications for a group s/he's a member of.
 */
public class GroupActivitySubscriptionModel extends BaseModel implements Fetchable<Serializable>, Insertable<String>,
        Deletable<String>
{
    /**
     * Singleton.
     */
    private static GroupActivitySubscriptionModel model = new GroupActivitySubscriptionModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static GroupActivitySubscriptionModel getInstance()
    {
        return model;
    }

    /**
     * Get a list of the ids of the groups this user is subscribed to new activity notifications for. The user would
     * have to belong to these groups.
     * 
     * @param ignoredParameter
     *            ignored
     * @param useClientCacheIfAvailable
     *            whether to use the client cache
     */
    public void fetch(final Serializable ignoredParameter, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getUserGroupActivitySubscriptions", "", new OnSuccessCommand<ArrayList<String>>()
        {
            public void onSuccess(final ArrayList<String> groupShortNames)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new GotGroupActivitySubscriptionsResponseEvent(groupShortNames));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * Subscribe to new activity notifications for the group with the input short name.
     * 
     * @param inGroupShortName
     *            the short name of the group to subsribe notifications from
     */
    public void insert(final String inGroupShortName)
    {
        final ChangeGroupActivitySubscriptionRequest request = new ChangeGroupActivitySubscriptionRequest(
                inGroupShortName, true);
        super.callWriteAction("changeGroupNewActivityNotificationPreference", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                GroupActivitySubscriptionModel.getInstance().clearCache();
                Session.getInstance().getEventBus().notifyObservers(new GroupActivitySubscriptionChangedEvent(request));
            }
        });
    }

    /**
     * Unsubscribe to new activity notifications for the group with the input short name.
     * 
     * @param inGroupShortName
     *            the short name of the group to unsubsribe notifications from
     */
    public void delete(final String inGroupShortName)
    {
        final ChangeGroupActivitySubscriptionRequest request = new ChangeGroupActivitySubscriptionRequest(
                inGroupShortName, false);

        super.callWriteAction("changeGroupNewActivityNotificationPreference", request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                GroupActivitySubscriptionModel.getInstance().clearCache();
                Session.getInstance().getEventBus().notifyObservers(new GroupActivitySubscriptionChangedEvent(request));
            }
        });
    }

}
