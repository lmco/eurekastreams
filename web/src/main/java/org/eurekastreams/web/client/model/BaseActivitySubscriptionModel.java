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

import org.eurekastreams.server.action.request.stream.ChangeStreamActivitySubscriptionRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.data.StreamActivitySubscriptionChangedEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for un/subscribing a user to stream notifications for a followed stream.
 */
public class BaseActivitySubscriptionModel extends BaseModel implements Insertable<String>, Deletable<String>
{
    /** Action for updating preferences. */
    private final String updateAction;

    /** Entity type of stream. */
    private final EntityType entityType;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            Entity type of stream.
     * @param inUpdateAction
     *            Action for updating preferences.
     */
    protected BaseActivitySubscriptionModel(final EntityType inEntityType, final String inUpdateAction)
    {
        entityType = inEntityType;
        updateAction = inUpdateAction;
    }

    /**
     * Updates the preference on the server.
     *
     * @param uniqueId
     *            Unique ID of the stream.
     * @param subscribe
     *            Whether to subscribe or not.
     */
    private void update(final String uniqueId, final boolean subscribe)
    {
        final ChangeStreamActivitySubscriptionRequest request = new ChangeStreamActivitySubscriptionRequest(
                entityType, uniqueId, subscribe);
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
     * @param inGroupShortName
     *            the short name of the group to subsribe notifications from
     */
    public void insert(final String inGroupShortName)
    {
        update(inGroupShortName, true);
    }

    /**
     * Unsubscribe to new activity notifications for the group with the input short name.
     *
     * @param inGroupShortName
     *            the short name of the group to unsubsribe notifications from
     */
    public void delete(final String inGroupShortName)
    {
        update(inGroupShortName, false);
    }
}
