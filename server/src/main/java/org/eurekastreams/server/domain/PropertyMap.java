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
import java.util.Map;

/**
 * Map of properties.
 * 
 * @param <T>
 *            Type of properties to store.
 */
public interface PropertyMap<T> extends Map<String, Property<T>>
{
    /**
     * Sets a property with a value.
     *
     * @param key
     *            Name of property.
     * @param value
     *            Value of property.
     */
    void put(String key, T value);

    /**
     * Sets a property with a placeholder value.
     *
     * @param key
     *            Name of property.
     * @param type
     *            The type of the property.
     * @param identity
     *            The identity of the property.
     */
    void put(String key, final Class type, final Serializable identity);

    /**
     * Causes two keys to point to the same property (so both will hold the same value and lazy-loading will load both);
     * creates a new key which will be in sync with an existing key.
     *
     * @param aliasKey
     *            New key.
     * @param originalKey
     *            Existing key.
     */
    void putAlias(final String aliasKey, final String originalKey);
}
