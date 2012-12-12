/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Identifiable;

/**
 * Represents an entity that can be an actor of an activity.
 */
public class StreamEntityDTO implements Serializable, Identifiable
{
    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    /** The type (person, group, etc.). */
    private EntityType type;

    /** The entity id. */
    private long id = 0;

    /** The avatar id. */
    private String avatarId;

    /** The unique string identifier, such as accountId. */
    private String uniqueIdentifier;

    /** The destination/recipient (group/person) entity's id. */
    private Long destinationEntityId;

    /** The UI-friendly display name. */
    private String displayName;

    /** If the stream entity is "active" (e.g. for people, they're not locked). */
    private boolean active = true;

    /**
     * Constructor.
     */
    public StreamEntityDTO()
    {
    }

    /**
     * Constructor.
     *
     * @param inType
     *            The type (person, group, etc.).
     * @param inUniqueIdentifier
     *            The unique string identifier, such as accountId.
     */
    public StreamEntityDTO(final EntityType inType, final String inUniqueIdentifier)
    {
        type = inType;
        uniqueIdentifier = inUniqueIdentifier;
    }

    /**
     * @return the type
     */
    public EntityType getType()
    {
        return type;
    }

    /**
     * @param inType
     *            the type to set
     */
    public void setType(final EntityType inType)
    {
        type = inType;
    }

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Set the StreamScope ID.
     *
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * Get the StreamScope ID.
     *
     * @return the avatarId
     */
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * @param inAvatarId
     *            the avatarId to set
     */
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
    }

    /**
     * @return the uniqueIdentifier
     */
    public String getUniqueIdentifier()
    {
        return uniqueIdentifier;
    }

    /**
     * @param inUniqueIdentifier
     *            the uniqueIdentifier to set
     */
    public void setUniqueIdentifier(final String inUniqueIdentifier)
    {
        uniqueIdentifier = inUniqueIdentifier;
    }

    /**
     * @return the displayName
     */
    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param inDisplayName
     *            the displayName to set
     */
    public void setDisplayName(final String inDisplayName)
    {
        displayName = inDisplayName;
    }

    /**
     * Get the destination/recipient entity's id.
     *
     * @return the destination/recipient entity's id.
     */
    public Long getDestinationEntityId()
    {
        return destinationEntityId;
    }

    /**
     * Set the destination/recipient entity's id.
     *
     * @param inDestinationEntityId
     *            the destination/recipient entity's id.
     */
    public void setDestinationEntityId(final Long inDestinationEntityId)
    {
        destinationEntityId = inDestinationEntityId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId()
    {
        return uniqueIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType()
    {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEntityId()
    {
        return destinationEntityId;
    }

    /**
     * @return If stream entity is active.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Set if stream entity is active.
     *
     * @param inActive
     *            If stream entity is active.
     */
    public void setActive(final boolean inActive)
    {
        active = inActive;
    }
}
