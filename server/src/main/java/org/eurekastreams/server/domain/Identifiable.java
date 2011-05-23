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

/**
 * Represents an "entity" in the system which can be identified.
 */
public interface Identifiable
{
    /**
     * @return The (numeric database) id of the entity
     */
    long getEntityId();

    /**
     * @return The string-based unique identifier of the identity.
     */
    String getUniqueId();

    /**
     * @return The type of the entity.
     */
    EntityType getEntityType();

    /**
     * @return The name to display for the entity.
     */
    String getDisplayName();
}
