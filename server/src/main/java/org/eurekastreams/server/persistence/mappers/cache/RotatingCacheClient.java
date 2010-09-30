/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.server.Rotator;

/**
 * Cache client that rotates through configured Cache clients.
 * 
 */
public class RotatingCacheClient implements Cache
{
    /**
     * Cache rotator for read operations.
     */
    private Rotator<Cache> readCacheRotator;

    /**
     * Cache rotator for write operations.
     */
    private Rotator<Cache> writeCacheRotator;

    /**
     * Constructor.
     * 
     * @param inReadCacheRotator
     *            Cache rotator for read operations.
     * @param inWriteCacheRotator
     *            Cache rotator for write operations.
     */
    public RotatingCacheClient(final Rotator<Cache> inReadCacheRotator, final Rotator<Cache> inWriteCacheRotator)
    {
        readCacheRotator = inReadCacheRotator;
        writeCacheRotator = inWriteCacheRotator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> addToSet(final String inKey, final Long inValue)
    {
        return writeCacheRotator.getNext().addToSet(inKey, inValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToTopOfList(final String inKey, final List<Long> inValues)
    {
        writeCacheRotator.getNext().addToTopOfList(inKey, inValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToTopOfList(final String inKey, final Long inValue)
    {
        writeCacheRotator.getNext().addToTopOfList(inKey, inValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        readCacheRotator.getNext().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final String inKey)
    {
        writeCacheRotator.getNext().delete(inKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteList(final String inKey)
    {
        writeCacheRotator.getNext().deleteList(inKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final String inKey)
    {
        return readCacheRotator.getNext().get(inKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Long> getList(final String inKey, final int inMaximumEntries)
    {
        return readCacheRotator.getNext().getList(inKey, inMaximumEntries);
    }

    @Override
    public ArrayList<Long> getList(final String inKey)
    {
        return readCacheRotator.getNext().getList(inKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> multiGet(final Collection<String> inKeys)
    {
        return readCacheRotator.getNext().multiGet(inKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ArrayList<Long>> multiGetList(final Collection<String> inKeys)
    {
        return readCacheRotator.getNext().multiGetList(inKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromList(final String inKey, final Long inValue)
    {
        writeCacheRotator.getNext().removeFromList(inKey, inValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromLists(final List<String> inKeys, final List<Long> inValues)
    {
        writeCacheRotator.getNext().removeFromLists(inKeys, inValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromSet(final String inKey, final Long inValue)
    {
        writeCacheRotator.getNext().removeFromSet(inKey, inValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(final String inKey, final Object inValue)
    {
        writeCacheRotator.getNext().set(inKey, inValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setList(final String inKey, final List<Long> inValue)
    {
        writeCacheRotator.getNext().setList(inKey, inValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Long> setListCAS(final String inKey, final List<Long> inValue)
    {
        return writeCacheRotator.getNext().setListCAS(inKey, inValue);
    }

}
