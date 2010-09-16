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
package org.eurekastreams.server.service.utility;

import java.util.Collection;


/**
 * Utility class used by Eureka Streams extensions to augment collections (lists/sets) without altering the core
 * configuration.
 */
public class CollectionAugmenter
{
    /**
     * Constructor.
     *
     * @param collectionToUpdate
     *            Collection (from the core configuration) to which to add the extension's item(s).
     * @param additionalItems
     *            Items to add.
     */
    public CollectionAugmenter(final Collection collectionToUpdate, final Collection additionalItems)
    {
        collectionToUpdate.addAll(additionalItems);
    }

    /**
     * Constructor.
     *
     * @param collectionToUpdate
     *            Collection (from the core configuration) to which to add the extension's item(s).
     * @param additionalItem
     *            Item to add.
     */
    public CollectionAugmenter(final Collection collectionToUpdate, final Object additionalItem)
    {
        collectionToUpdate.add(additionalItem);
    }
}
