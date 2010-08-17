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

import java.util.Map;

/**
 * Simple mapper which retrieves values from an unchanging map. One use of this is to allow a class to get its data from
 * a dictionary now but be upgraded transparently to get its data from another source later.
 *
 * @param <TRequestType>
 *            Lookup key type.
 * @param <TReturnType>
 *            Data type.
 */
public class ImmutableMapLookupMapper<TRequestType, TReturnType> implements DomainMapper<TRequestType, TReturnType>
{
    /** The map. */
    private Map<TRequestType, TReturnType> map;

    /**
     * Constructor.
     *
     * @param inMap
     *            Map to use; access to the map is not synchronized by the mapper thus the map MUST NOT be altered.
     */
    public ImmutableMapLookupMapper(final Map<TRequestType, TReturnType> inMap)
    {
        map = inMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TReturnType execute(final TRequestType inRequest)
    {
        return map.get(inRequest);
    }
}
