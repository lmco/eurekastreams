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
import java.util.Collection;

/**
 * Combines two collections.
 */
@SuppressWarnings("unchecked")
public class CollectionCombiner implements ResultsCombinerStrategy<Collection>
{
    /**
     * Generics purposely left off.
     * @param collection1 the first collection.
     * @param collection2 the second collection.
     * @return the combined collection.
     */
    @Override
    public Collection combine(final Collection collection1, final Collection collection2)
    {
        Collection allItems = new ArrayList();
        
        allItems.addAll(collection1);
        allItems.addAll(collection2);
        
        return allItems;
    }

}
