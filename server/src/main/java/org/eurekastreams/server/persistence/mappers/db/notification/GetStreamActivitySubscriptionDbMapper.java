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

package org.eurekastreams.server.persistence.mappers.db.notification;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetStreamActivitySubscriptionMapperRequest;

/**
 * Mapper to update a user's notification preference for new activities for a specific stream.
 */
public class GetStreamActivitySubscriptionDbMapper extends
        BaseArgDomainMapper<GetStreamActivitySubscriptionMapperRequest, Boolean>
{
    /** Name of entity to use in query. */
    private String entityName;

    /**
     * Constructor.
     *
     * @param entityType
     *            Entity type of streams to operate on.
     */
    public GetStreamActivitySubscriptionDbMapper(final EntityType entityType)
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
     * Get the person's notification preference for a stream that they are following.
     *
     * @param inRequest
     *            Identifies the user and the stream entity to query.
     * @return If subscribed.
     */
    @Override
    public Boolean execute(final GetStreamActivitySubscriptionMapperRequest inRequest)
    {
        String q = "SELECT receiveNewActivityNotifications FROM " + entityName
                + " WHERE pk.followingId = :streamEntityId AND pk.followerId = :personId";

        Query query = getEntityManager().createQuery(q).setParameter("personId", inRequest.getSubscriberPersonId())
                .setParameter("streamEntityId", inRequest.getStreamEntityId());
        List<Boolean> results = query.getResultList();
        return results.size() == 1 ? results.get(0) : false;
    }
}
