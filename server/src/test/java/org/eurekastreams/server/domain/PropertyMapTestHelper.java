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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Assert;

/**
 * Helpers for testing classes that use property maps.
 */
public final class PropertyMapTestHelper
{
    /**
     * Forbid instantiation.
     */
    private PropertyMapTestHelper()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Asserts that a placeholder property exists as described.
     * 
     * @param props
     *            Property map to check.
     * @param key
     *            Key in map.
     * @param type
     *            Expected type.
     * @param identity
     *            Expected identity.
     */
    public static void assertPlaceholder(final PropertyMap<Object> props, final String key, final Class type,
            final Serializable identity)
    {
        Property<Object> prop = props.get(key);
        assertNotNull(prop);
        assertEquals(type, prop.getType());
        assertEquals(identity, prop.getIdentity());
    }

    /**
     * Asserts that an actual value property exists as described.
     *
     * @param props
     *            Property map to check.
     * @param key
     *            Key in map.
     * @param value
     *            Expected value.
     */
    public static void assertValue(final PropertyMap<Object> props, final String key, final Object value)
    {
        Property<Object> prop = props.get(key);
        assertNotNull(prop);
        assertEquals(value, prop.getValue());
    }

    /**
     * Asserts that one key is an alias of the other - that both keys point to the same property.
     *
     * @param props
     *            Property map to check.
     * @param key1
     *            Key.
     * @param key2
     *            Key.
     */
    public static void assertAlias(final PropertyMap<Object> props, final String key1, final String key2)
    {
        Property<Object> prop1 = props.get(key1);
        Property<Object> prop2 = props.get(key1);
        assertNotNull(prop1);
        assertNotNull(prop2);
        Assert.assertSame(prop1, prop2);
    }

}
