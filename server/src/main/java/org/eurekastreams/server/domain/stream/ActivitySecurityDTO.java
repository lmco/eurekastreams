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
package org.eurekastreams.server.domain.stream;

import java.io.Serializable;

/**
 * Stores security information about an Activity. Used to determine who can see the activity.
 */
public class ActivitySecurityDTO implements Serializable
{
    /**
     * Version.
     */
    private static final long serialVersionUID = -1982680235906203165L;

    /**
     * If the destination stream is public.
     */
    private boolean isDestinationStreamPublic;

    /**
     * The activity ID.
     */
    private Long id;

    /**
     * Destination Entity ID.
     */
    private Long destinationEntityId;

    /**
     * Constructor.
     *
     * @param inId
     *            the activity ID.
     * @param inDestinationEntityId
     *            the destination stream's entity ID.
     * @param inIsDestinationStreamPublic
     *            if the destination stream is public.
     */
    public ActivitySecurityDTO(final Long inId, final Long inDestinationEntityId,
            final Boolean inIsDestinationStreamPublic)
    {
        id = inId;
        destinationEntityId = inDestinationEntityId;
        isDestinationStreamPublic = inIsDestinationStreamPublic;
    }

    /**
     * Set if the destination stream is public.
     *
     * @param inIsDestinationStreamPublic
     *            the if destination stream is public.
     */
    public void setDestinationStreamPublic(final boolean inIsDestinationStreamPublic)
    {
        this.isDestinationStreamPublic = inIsDestinationStreamPublic;
    }

    /**
     * @return true if the destination stream is public, false if not.
     */
    public boolean isDestinationStreamPublic()
    {
        return isDestinationStreamPublic;
    }

    /**
     * Set the destination entity ID.
     *
     * @param inDestinationEntityId
     *            the destination entity ID.
     */
    public void setDestinationEntityId(final Long inDestinationEntityId)
    {
        this.destinationEntityId = inDestinationEntityId;
    }

    /**
     * @return the destination entity ID.
     */
    public Long getDestinationEntityId()
    {
        return destinationEntityId;
    }

    /**
     * Set the activity ID.
     *
     * @param inId
     *            the id.
     */
    public void setId(final Long inId)
    {
        this.id = inId;
    }

    /**
     * @return the activity id.
     */
    public Long getId()
    {
        return id;
    }
}
