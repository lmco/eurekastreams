/*
 * Copyright (c) 2012 Lockheed Martin Corporation
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
package org.eurekastreams.commons.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wrapper which allows any Collection to be used as a List.
 * 
 * @param <T>
 *            Collection element type.
 */
public class CollectionListAdapter<T> implements List<T>
{
    /** Wrapped collection. */
    private final Collection<T> wrapped;

    /**
     * Constructor.
     * @param inCollection Collection to wrap.
     */
    public CollectionListAdapter(final Collection<T> inCollection)
    {
        wrapped = inCollection;
    }

    @Override
    public boolean add(final T e)
    {
        return wrapped.add(e);
    }

    @Override
    public void add(final int index, final T element)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public boolean addAll(final Collection< ? extends T> c)
    {
        return wrapped.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection< ? extends T> c)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public void clear()
    {
        wrapped.clear();
    }

    @Override
    public boolean contains(final Object o)
    {
        return wrapped.contains(o);
    }

    @Override
    public boolean containsAll(final Collection< ? > c)
    {
        return wrapped.containsAll(c);
    }

    @Override
    public T get(final int index)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public int indexOf(final Object o)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public boolean isEmpty()
    {
        return wrapped.isEmpty();
    }

    @Override
    public Iterator<T> iterator()
    {
        return wrapped.iterator();
    }

    @Override
    public int lastIndexOf(final Object o)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public ListIterator<T> listIterator()
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public ListIterator<T> listIterator(final int index)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public boolean remove(final Object o)
    {
        return wrapped.remove(o);
    }

    @Override
    public T remove(final int index)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public boolean removeAll(final Collection< ? > c)
    {
        return wrapped.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection< ? > c)
    {
        return wrapped.retainAll(c);
    }

    @Override
    public T set(final int index, final T element)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public int size()
    {
        return wrapped.size();
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex)
    {
        throw new UnsupportedOperationException("Underlying collection may not support ordering.");
    }

    @Override
    public Object[] toArray()
    {
        return wrapped.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a)
    {
        return wrapped.toArray(a);
    }
}
