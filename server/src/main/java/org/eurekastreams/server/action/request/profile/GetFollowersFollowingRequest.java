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

import org.eurekastreams.server.action.request.PageableRequest;
import org.eurekastreams.server.domain.EntityType;

/**
 * Get the followers or following list for a person.
 */
public class GetFollowersFollowingRequest implements Serializable, PageableRequest
{
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -3061652812426787181L;

    /**
     * The entity type.
     */
    private EntityType entityType;

    /**
     * The unique entity id.
     */
    private String entityId;

    /**
     * Start value.
     */
    private Integer startValue;

    /**
     * End value.
     */
    private Integer endValue;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            the entity type.
     *
     * @param inEntityId
     *            the entity id.
     *
     * @param inStartValue
     *            the start value.
     *
     * @param inEndValue
     *            the end value.
     *
     */
    public GetFollowersFollowingRequest(final EntityType inEntityType, final String inEntityId,
            final Integer inStartValue, final Integer inEndValue)
    {
        setEntityType(inEntityType);
        setEntityId(inEntityId);
        setStartIndex(inStartValue);
        setEndIndex(inEndValue);
    }

    /**
     * Used for Serialization.
     */
    @SuppressWarnings("unused")
    public GetFollowersFollowingRequest()
    {
    }

    /**
     * @param inEntityType
     *            the entityType to set
     */
    public void setEntityType(final EntityType inEntityType)
    {
        this.entityType = inEntityType;
    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @param inEntityId
     *            the entityId to set
     */
    public void setEntityId(final String inEntityId)
    {
        this.entityId = inEntityId;
    }

    /**
     * @return the entityId
     */
    public String getEntityId()
    {
        return entityId;
    }

    /**
     * @param inStartValue
     *            the startValue to set
     */
    public void setStartIndex(final Integer inStartValue)
    {
        this.startValue = inStartValue;
    }

    /**
     * @return the startValue
     */
    public Integer getStartIndex()
    {
        return startValue;
    }

    /**
     * @param inEndValue
     *            the endValue to set
     */
    public void setEndIndex(final Integer inEndValue)
    {
        this.endValue = inEndValue;
    }

    /**
     * @return the endValue
     */
    public Integer getEndIndex()
    {
        return endValue;
    }

}
