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
package org.eurekastreams.server.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.server.domain.Property;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * A property map which allows lazy loading using mappers.
 * 
 * @param <T>
 *            Type of properties to store.
 */
public class LazyLoadPropertiesMap<T> implements Map<String, T>
{
    /** The underlying map. */
    private final Map<String, Property<T>> map;

    /** The mappers to use to fetch lazy-loaded properties. */
    private final Map<Class, DomainMapper<Serializable, T>> mappers;

    /**
     * Constructor.
     * 
     * @param inMappers
     *            Mappers used to fetch lazy-loaded data, by type of data.
     */
    public LazyLoadPropertiesMap(final Map<Class, DomainMapper<Serializable, T>> inMappers)
    {
        map = new HashMap<String, Property<T>>();
        mappers = inMappers;
    }

    /**
     * Constructor.
     * 
     * @param inMap
     *            Map of data to wrap.
     * @param inMappers
     *            Mappers used to fetch lazy-loaded data, by type of data.
     */
    public LazyLoadPropertiesMap(final Map<String, Property<T>> inMap,
            final Map<Class, DomainMapper<Serializable, T>> inMappers)
    {
        map = inMap;
        mappers = inMappers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return map.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(final Object inKey)
    {
        return map.containsKey(inKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(final Object inValue)
    {
        // operation would require loading all lazy-loaded values, so we don't support it
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(final Object inKey)
    {
        T value = null;
        Property<T> property = map.get(inKey);
        if (property != null)
        {
            value = property.getValue();
            if (value == null && property.getType() != null)
            {
                DomainMapper<Serializable, T> mapper = mappers.get(property.getType());
                if (mapper != null)
                {
                    value = mapper.execute(property.getIdentity());
                    property.setValue(value);
                }
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T put(final String inKey, final T inValue)
    {
        T value = null;
        Property<T> property = map.get(inKey);
        if (property != null)
        {
            value = property.getValue();
            property.setValue(inValue);
        }
        else
        {
            property = new Property<T>(inValue);
            map.put(inKey, property);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T remove(final Object inKey)
    {
        // we don't need this; we'll code it if we ever do
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(final Map< ? extends String, ? extends T> inM)
    {
        for (java.util.Map.Entry< ? extends String, ? extends T> entry : inM.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        map.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> keySet()
    {
        return map.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> values()
    {
        // operation would require loading all lazy-loaded values, so we don't support it
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<java.util.Map.Entry<String, T>> entrySet()
    {
        // we don't need this; we'll code it if we ever do
        throw new UnsupportedOperationException();
    }
}
