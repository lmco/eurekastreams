/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import java.io.Serializable;

/**
 * Two-part key for identifying an entity.
 */
public class EntityIdentifier implements Serializable
{
    /** Type of the entity. */
    private EntityType type;

    /** Numeric ID of the entity. */
    private long id;

    /**
     * Constructor for deserialization.
     */
    public EntityIdentifier()
    {
    }

    /**
     * Constructor.
     *
     * @param inType
     *            Type of the entity.
     * @param inId
     *            Numeric ID of the entity.
     */
    public EntityIdentifier(final EntityType inType, final long inId)
    {
        type = inType;
        id = inId;
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
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }
}
