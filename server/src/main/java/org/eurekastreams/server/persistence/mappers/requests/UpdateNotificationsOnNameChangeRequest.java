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
 * Request for updating notifications on name change.
 * 
 */
public class UpdateNotificationsOnNameChangeRequest
{
    /**
     * {@link EntityType}.
     */
    private EntityType type;

    /**
     * Key.
     */
    private String uniqueKey;

    /**
     * Name value.
     */
    private String name;

    /**
     * Constructor.
     * 
     * @param inType
     *            Entity type.
     * @param inUniqueKey
     *            key.
     * @param inName
     *            Name value.
     */
    public UpdateNotificationsOnNameChangeRequest(final EntityType inType, // \n
            final String inUniqueKey, final String inName)
    {
        type = inType;
        uniqueKey = inUniqueKey;
        name = inName;
    }

    /**
     * @return the type
     */
    public EntityType getType()
    {
        return type;
    }

    /**
     * @return the uniqueKey
     */
    public String getUniqueKey()
    {
        return uniqueKey;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

}
