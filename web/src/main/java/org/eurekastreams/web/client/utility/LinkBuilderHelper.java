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
package org.eurekastreams.web.client.utility;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;

/**
 * Utility class to build links.
 */
public class LinkBuilderHelper
{
    /**
     * Which page to use to show the profile for each type of entity.
     */
    private static Map<EntityType, Page> entityProfilePageMapping = new HashMap<EntityType, Page>();
    static
    {
        entityProfilePageMapping.put(EntityType.PERSON, Page.PEOPLE);
        entityProfilePageMapping.put(EntityType.GROUP, Page.GROUPS);
        entityProfilePageMapping.put(EntityType.ORGANIZATION, Page.ORGANIZATIONS);
    }

    /**
     * Gets the profile page for a given entity type.
     * 
     * @param type
     *            Entity type.
     * @return Page, or null if no mapping.
     */
    public Page getEntityProfilePage(final EntityType type)
    {
        return entityProfilePageMapping.get(type);
    }
}
