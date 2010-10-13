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
package org.eurekastreams.server.persistence.mappers;

import java.util.List;

/**
 * Mapper to look up items by a key that's translated to the item's id.
 *
 * @param <PointerType>
 *            the type of data to use to look up the item's id
 * @param <ValueType>
 *            the item type to return
 */
public class GetItemsByPointerIdsMapper<PointerType, ValueType> implements
        DomainMapper<List<PointerType>, List<ValueType>>
{
    /**
     * Mapper to look up the ids by the pointer ids.
     */
    private DomainMapper<List<PointerType>, List<Long>> pointersToIdMapper;

    /**
     * Mapper to look up the item by id.
     */
    private DomainMapper<List<Long>, List<ValueType>> itemsByIdMapper;

    /**
     * Constructor.
     *
     * @param inPointersToIdMapper
     *            Mapper to look up the ids by the pointer ids.
     * @param inItemsByIdMapper
     *            Mapper to look up the item by id.
     */
    public GetItemsByPointerIdsMapper(final DomainMapper<List<PointerType>, List<Long>> inPointersToIdMapper,
            final DomainMapper<List<Long>, List<ValueType>> inItemsByIdMapper)
    {
        pointersToIdMapper = inPointersToIdMapper;
        itemsByIdMapper = inItemsByIdMapper;
    }

    /**
     * Lookup the items by their look-up fields.
     *
     * @param inPointerIds
     *            the pointers to use to lookup the item
     * @return the items referenced by the input pointers
     */
    @Override
    public List<ValueType> execute(final List<PointerType> inPointerIds)
    {
        List<Long> pointersToIds = pointersToIdMapper.execute(inPointerIds);
        return itemsByIdMapper.execute(pointersToIds);
    }
}
