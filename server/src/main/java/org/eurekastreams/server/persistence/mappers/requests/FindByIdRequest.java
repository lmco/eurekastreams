/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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


/**
 * MapperRequest for finding a DomainEntity by Id.
 *
 */
public class FindByIdRequest
{
    /**
     * entity name.
     */
    private String entityName;
    
    /**
     * entity entity Id.
     */
    private long entityId;
    
    /**
     * Constructor.
     * @param inEntityName for the name of the entity to find.
     * @param inEntityId for the id of the entity to find.
     */
    public FindByIdRequest(final String inEntityName, final long inEntityId)
    {
        entityName = inEntityName;
        entityId = inEntityId;
    }
    
    /**
     * Getter for entity name.
     * @return The entity name.
     */
    public String getEntityName()
    {
        return entityName;
    }
    
    /**
     * Getter for entity id.
     * @return The entity id.
     */
    public long getEntityId()
    {
        return entityId;
    }
}
