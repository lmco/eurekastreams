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
package org.eurekastreams.server.persistence.mappers.stream;

/**
 * Simple object to hold the pointerId and itemId for cached data that needs to be indexed by more than just the entity
 * id. For instance, a person object can be found in the cache by key Per:123 or PerAcct:jhstephe. The latter is an
 * example of an item pointer. The contents of that key in the cache just point to the actual entityId-based key
 * Per:123. This means that two cache reads are required to get the actual object when using item pointers.
 * 
 */
public class CacheItemPointer
{
    /**
     * The pointer id.
     */
    private String pointerId;

    /**
     * The item id.
     */
    private long itemId;

    /**
     * Gets the pointerId.
     * 
     * @return the pointer Id.
     */
    public String getPointerId()
    {
        return pointerId;
    }

    /**
     * Sets the pointer Id.
     * 
     * @param inPointerId
     *            the pointerId to set.
     */
    public void setPointerId(final String inPointerId)
    {
        pointerId = inPointerId;
    }

    /**
     * Gets the itemId.
     * 
     * @return the item Id.
     */
    public long getItemId()
    {
        return itemId;
    }

    /**
     * Sets the item Id.
     * 
     * @param inItemId
     *            the itemId to set.
     */
    public void setItemId(final long inItemId)
    {
        itemId = inItemId;
    }
}
