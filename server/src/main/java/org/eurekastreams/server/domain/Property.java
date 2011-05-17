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

/**
 * A data property (attribute).
 *
 * @param <T>
 *            Data type to store in the property.
 */
public class Property<T>
{
    /** The type of the property. */
    private Class type;

    /** The identity of the property. */
    private Serializable identity;

    /** The value of the property. */
    private T value;

    /**
     * Constructor.
     *
     * @param inType
     *            The type of the property.
     * @param inIdentity
     *            The identity of the property.
     */
    public Property(final Class inType, final Serializable inIdentity)
    {
        type = inType;
        identity = inIdentity;
    }

    /**
     * Constructor.
     *
     * @param inValue
     *            The value of the property.
     */
    public Property(final T inValue)
    {
        value = inValue;
    }

    /**
     * @return the type
     */
    public Class getType()
    {
        return type;
    }

    /**
     * @return the identity
     */
    public Serializable getIdentity()
    {
        return identity;
    }

    /**
     * @return the value
     */
    public T getValue()
    {
        return value;
    }

    /**
     * @param inValue
     *            the value to set
     */
    public void setValue(final T inValue)
    {
        value = inValue;
    }
}