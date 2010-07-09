/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui;

import java.util.Set;

/**
 * Generated support class for property binding.
 * 
 */
public abstract class HashedBindable
{
    /**
     * Populates the hash with the Bindable objects properties.
     * 
     * @param unHashedObject
     *            the bindable object to read.
     */
    public abstract void populateHash(Bindable unHashedObject);

    /**
     * Gets the object associated with a property.
     * 
     * @param key
     *            the property.
     * @return the associated object.
     */
    public abstract Object get(String key);

    /**
     * Sets the value of a property.
     * 
     * @param key
     *            the property.
     * @param w
     *            the object to set the property to.
     */
    public abstract void set(String key, Object w);

    /**
     * Returns a set of the properties as strings.
     * 
     * @return the properties.
     */
    public abstract Set<String> getFields();
}
