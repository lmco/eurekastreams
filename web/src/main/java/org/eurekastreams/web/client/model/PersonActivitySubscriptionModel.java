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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.data.GotGroupActivitySubscriptionsResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for un/subscribing a user to group notifications for a group s/he's a member of.
 */
public class PersonActivitySubscriptionModel extends BaseActivitySubscriptionModel implements Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static PersonActivitySubscriptionModel model = new PersonActivitySubscriptionModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static PersonActivitySubscriptionModel getInstance()
    {
        return model;
    }

    /**
     * Constructor.
     */
    public PersonActivitySubscriptionModel()
    {
        super(EntityType.PERSON, "changePersonNewActivityNotificationPreference");
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
}
