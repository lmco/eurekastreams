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

import java.io.Serializable;
import java.util.HashMap;

/**
 * A property map implemented as a hash map.
 *
 * @param <T>
 *            Type of properties to store.
 */
public class PropertyHashMap<T> extends HashMap<String, Property<T>> implements PropertyMap<T>
{
    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String key, final T value)
    {
        put(key, new Property<T>(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String key, final Class type, final Serializable identity)
    {
        put(key, new Property<T>(type, identity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAlias(final String aliasKey, final String originalKey)
    {
        Property<T> property = get(originalKey);
        if (property != null)
        {
            put(aliasKey, property);
        }
    }
}
