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

package org.eurekastreams.server.persistence.mappers.db;

import javax.persistence.Query;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.ChangeStreamActivitySubscriptionMapperRequest;

/**
 * Mapper to update a user's notification preference for new activities for a specific stream.
 */
public class ChangeStreamActivitySubscriptionDbMapper extends
        BaseArgDomainMapper<ChangeStreamActivitySubscriptionMapperRequest, Boolean>
{
    /** Name of entity to use in query. */
    private String entityName;

    /**
     * Constructor.
     *
     * @param entityType
     *            Entity type of streams to operate on.
     */
    public ChangeStreamActivitySubscriptionDbMapper(final EntityType entityType)
    {
        switch (entityType)
        {
        case PERSON:
            entityName = "Follower";
            break;
        case GROUP:
            entityName = "GroupFollower";
            break;
        default:
            throw new IllegalArgumentException("Entity type " + entityType + " not allowed.  Only PERSON and GROUP.");
        }
    }

    /**
     * Update the person's new activity notifications for a group that they are a member of.
     *
     * @param inRequest
     *            the ChangeGroupActivitySubscriptionMapperRequest containing the person id, group id, and notification
     *            setting
     * @return true
     */
    @Override
    public Boolean execute(final ChangeStreamActivitySubscriptionMapperRequest inRequest)
    {
        String q = "UPDATE " + entityName + " SET receiveNewActivityNotifications = :receiveNewActivityNotifications "
                + "WHERE pk.followerId = :personId AND pk.followingId = :streamEntityId";
        Query query = getEntityManager().createQuery(q)
                .setParameter("receiveNewActivityNotifications", inRequest.getReceiveNewActivityNotifications())
                .setParameter("personId", inRequest.getSubscriberPersonId())
                .setParameter("streamEntityId", inRequest.getStreamEntityId());
        query.executeUpdate();

        return new Boolean(true);
    }
}
