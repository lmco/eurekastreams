/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.requests;

import java.io.Serializable;

import org.eurekastreams.server.domain.EntityType;

/**
 * Get feed subscriber request.
 *
 */
public class GetFeedSubscriberRequest implements Serializable
{
    /**
     * Feed id.
     */
    private long feedId;
    /**
     * Entity id.
     */
    private long entityId;

    /**
     * Entity type.
     */
    private EntityType entityType;

    /** Person ID of requestor. */
    private long requestorPersonId;

    /**
     * Default constructor.
     *
     * @param inFeedId
     *            feed id.
     * @param inEntityId
     *            entity id.
     * @param inEntityType
     *            type.
     * @param inRequestorPersonId
     *            Person ID of requestor.
     */
    public GetFeedSubscriberRequest(final long inFeedId, final long inEntityId, final EntityType inEntityType,
            final long inRequestorPersonId)
    {
        feedId = inFeedId;
        entityId = inEntityId;
        entityType = inEntityType;
        requestorPersonId = inRequestorPersonId;
    }

    /**
     * Gets the feed id.
     *
     * @return the feed id.
     */
    public long getFeedId()
    {
        return feedId;
    }

    /**
     * Gets the entity id.
     *
     * @return the entity id.
     */
    public long getEntityId()
    {
        return entityId;
    }

    /**
     * Gets the entity type.
     *
     * @return the entity type.
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @return the Person ID of requestor.
     */
    public long getRequestorPersonId()
    {
        return requestorPersonId;
    }

}
