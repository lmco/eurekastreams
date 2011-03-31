/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;

/**
 * Utility methods for consistent handling of domain data.
 */
public final class DomainConversionUtility
{
    /**
     * Constructor to prevent instantiation.
     */
    private DomainConversionUtility()
    {
    }

    /**
     * Returns the appropriate entity type for a given scope type (in isolation).
     * 
     * @param scopeType
     *            Scope type.
     * @return Entity type.
     */
    public static EntityType convertToEntityType(final ScopeType scopeType)
    {
        switch (scopeType)
        {
        case PERSON:
            return EntityType.PERSON;
        case PERSONS_PARENT_ORGANIZATION:
        case ORGANIZATION:
            return EntityType.ORGANIZATION;
        case GROUP:
            return EntityType.GROUP;
        case RESOURCE:
            return EntityType.RESOURCE;
        default:
            return EntityType.NOTSET;
        }
    }
}
