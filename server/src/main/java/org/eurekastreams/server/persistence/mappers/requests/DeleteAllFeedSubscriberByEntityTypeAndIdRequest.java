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
package org.eurekastreams.server.persistence.mappers.requests;

import org.eurekastreams.server.domain.EntityType;

/**
 * Request for DeleteAllFeedSubscriberByEntityTypeAndId mapper.
 * 
 */
public class DeleteAllFeedSubscriberByEntityTypeAndIdRequest
{
    /**
     * Entity id.
     */
    private Long entityId;

    /**
     * Entity type.
     */
    private EntityType entityType;

    /**
     * Constructor.
     * 
     * @param inEntityId
     *            Entity id.
     * @param inEntityType
     *            Entity type.
     */
    public DeleteAllFeedSubscriberByEntityTypeAndIdRequest(final Long inEntityId, final EntityType inEntityType)
    {
        entityId = inEntityId;
        entityType = inEntityType;
    }

    /**
     * @return the entityId
     */
    public Long getEntityId()
    {
        return entityId;
    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

}
