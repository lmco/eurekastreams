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
package org.eurekastreams.server.persistence.mappers;

/**
 * Mapper to look up a single item by a key that's translated to the item's id.
 *
 * @param <PointerType>
 *            the type of data to use to look up the item's id
 * @param <ValueType>
 *            the item type to return
 */
public class GetItemByPointerIdMapper<PointerType, ValueType> implements DomainMapper<PointerType, ValueType>
{
    /**
     * Mapper to look up the id by the pointer id.
     */
    private final DomainMapper<PointerType, Long> pointerToIdMapper;

    /**
     * Mapper to look up the item by id.
     */
    private final DomainMapper<Long, ValueType> itemByIdMapper;

    /**
     * Constructor.
     *
     * @param inPointerToIdMapper
     *            Mapper to look up the id by the pointer id.
     * @param inItemByIdMapper
     *            Mapper to look up the item by id.
     */
    public GetItemByPointerIdMapper(final DomainMapper<PointerType, Long> inPointerToIdMapper,
            final DomainMapper<Long, ValueType> inItemByIdMapper)
    {
        pointerToIdMapper = inPointerToIdMapper;
        itemByIdMapper = inItemByIdMapper;
    }

    /**
     * Lookup the item by its look-up field.
     *
     * @param inPointerId
     *            the pointer to use to look up the item
     * @return the item referenced by the input pointer
     */
    @Override
    public ValueType execute(final PointerType inPointerId)
    {
        Long id = pointerToIdMapper.execute(inPointerId);
        return itemByIdMapper.execute(id);
    }
}
