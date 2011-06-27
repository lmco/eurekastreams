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
package org.eurekastreams.server.domain.dto;

import java.io.Serializable;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.FollowerStatusable;

/**
 * Interface for an entity that can have its display info updated.
 */
public interface DisplayInfoSettable extends Serializable, FollowerStatusable
{
    /**
     * Get the stream's unique key.
     * 
     * @return the stream's unique key
     */
    String getStreamUniqueKey();

    /**
     * Set the avatar id.
     * 
     * @param inAvatarId
     *            the avatard id to set
     */
    void setAvatarId(String inAvatarId);

    /**
     * Set the display name.
     * 
     * @param inDisplayName
     *            the display name to set
     */
    void setDisplayName(String inDisplayName);

    /**
     * Get the entity type.
     * 
     * @return the entity type
     */
    EntityType getEntityType();
}
