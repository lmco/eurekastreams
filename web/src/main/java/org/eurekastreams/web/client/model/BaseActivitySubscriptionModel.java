/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

import org.eurekastreams.server.action.request.stream.ChangeStreamActivitySubscriptionRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.data.GotStreamActivitySubscriptionResponseEvent;
import org.eurekastreams.web.client.events.data.StreamActivitySubscriptionChangedEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for un/subscribing a user to stream notifications for a followed stream.
 */
public class BaseActivitySubscriptionModel extends BaseModel implements Fetchable<String>, Insertable<String>,
        Deletable<String>
{
    /** Entity type of stream. */
    private final EntityType entityType;

    /** Action to query a single preference. */
    private final String queryAction;

    /** Action for updating preferences. */
    private final String updateAction;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            Entity type of stream.
     * @param inQueryAction
     *            Action to query a single preference.
     * @param inUpdateAction
     *            Action for updating preferences.
     */
    protected BaseActivitySubscriptionModel(final EntityType inEntityType, final String inQueryAction,
            final String inUpdateAction)
    {
        entityType = inEntityType;
        queryAction = inQueryAction;
        updateAction = inUpdateAction;
    }

    /**
     * Get whether this user is subscribed to new activity notifications for the given stream. The user would have to
     * follow this stream.
     *
     * @param uniqueId
     *            Unique ID of stream.
     * @param useClientCacheIfAvailable
     *            whether to use the client cache
     */
    public void fetch(final String uniqueId, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction(queryAction, uniqueId, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean isSubscribed)
            {
                Session.getInstance()
                        .getEventBus()
                        .notifyObservers(
new GotStreamActivitySubscriptionResponseEvent(entityType, uniqueId, // \n
                                isSubscribed));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * Updates the preference on the server.
     *
     * @param uniqueId
     *            Unique ID of the stream.
     * @param subscribe
     *            Whether to subscribe or not.
     * @param coordOnly
     *            Subscribe for coordinator posts only.
     */
    public void update(final String uniqueId, final boolean subscribe, final boolean coordOnly)
    {
        final ChangeStreamActivitySubscriptionRequest request = new ChangeStreamActivitySubscriptionRequest(entityType,
                uniqueId, subscribe, coordOnly);
        super.callWriteAction(updateAction, request, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                clearCache();
                Session.getInstance().getEventBus()
                        .notifyObservers(new StreamActivitySubscriptionChangedEvent(request));
            }
        });
    }

    /**
     * Subscribe to new activity notifications for the group with the input short name.
     *
     * @param uniqueId
     *            Unique ID of the stream to subscribe notifications from.
     */
    public void insert(final String uniqueId)
    {
        update(uniqueId, true, false);
    }

    /**
     * Unsubscribe to new activity notifications for the group with the input short name.
     *
     * @param uniqueId
     *            Unique ID of the stream to unsubscribe notifications from.
     */
    public void delete(final String uniqueId)
    {
        update(uniqueId, false, false);
    }
}
