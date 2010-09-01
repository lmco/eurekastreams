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
package org.eurekastreams.server.persistence.mappers.chained;

import java.util.ArrayList;
import java.util.List;

/**
 * Combines two collections.
 *
 * @param <Type>
 *            the type of list objects to combine
 */
public class CollectionCombiner<Type> implements ResultsCombinerStrategy<List<Type>>
{
    /**
     * Combine two lists.
     *
     * @param collection1
     *            the first collection.
     * @param collection2
     *            the second collection.
     * @return the combined collection.
     */
    @Override
    public List combine(final List<Type> collection1, final List<Type> collection2)
    {
        List allItems = new ArrayList<Type>();

        allItems.addAll(collection1);
        allItems.addAll(collection2);

        return allItems;
    }

}
