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
package org.eurekastreams.commons.server;

import java.util.List;


/**
 * Round robin through collection of items.
 * 
 * @param <T>
 *            Type of items.
 */
public class RoundRobinRotator<T> implements Rotator<T>
{
    /**
     * List of item to rotate through.
     */
    private List<T> items;

    /**
     * Size of list.
     */
    private int size;

    /**
     * Running index.
     */
    private int index;

    /**
     * Sync lock object.
     */
    private Object lock = new Object();

    /**
     * Constructor.
     * 
     * @param inItems
     *            List of item to rotate through.
     */
    public RoundRobinRotator(final List<T> inItems)
    {
        items = inItems;
        size = items.size();
        index = 0;
    }

    /**
     * Round robin through collection of items.
     * 
     * @return Next item.
     */
    @Override
    public T getNext()
    {
        synchronized (lock)
        {
            T result = items.get(index);
            index = ++index == size ? 0 : index;
            return result;
        }
    }

}
