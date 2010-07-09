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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;

import org.eurekastreams.server.domain.EntityType;

/**
 * Get group profile authorization status for a person.
 */
public class GetCurrentUserFollowingStatusRequest implements Serializable
{
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -3061652215426787181L;

    /**
     * The entity identifier.
     */
    private String followedEntityId;

    /**
     * The entity type.
     */
    private EntityType entityType;

    /**
     * Constructor, returning the most recent page of data.
     * 
     * @param inFollowedEntityId
     *            the follower's entity id.
     * 
     * @param inEntityType
     *            the entity type of the follower.
     */
    public GetCurrentUserFollowingStatusRequest(final String inFollowedEntityId, final EntityType inEntityType)
    {
        followedEntityId = inFollowedEntityId;
        entityType = inEntityType;
    }

    /**
     * Used for Serialization.
     */
    @SuppressWarnings("unused")
    private GetCurrentUserFollowingStatusRequest()
    {
    }

    /**
     * @return the followed entity id.
     */
    public String getFollowedEntityId()
    {
        return followedEntityId;
    }

    /**
     * @param inFollowedEntityId
     *            the followed entity id.
     */
    public void setCompositeStreamId(final String inFollowedEntityId)
    {
        followedEntityId = inFollowedEntityId;
    }

    /**
     * @return the entity type.
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @param inEntityType
     *            the entity type.
     */
    public void setEntityType(final EntityType inEntityType)
    {
        entityType = inEntityType;
    }
}
