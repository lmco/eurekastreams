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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests PropertyHashMap.
 */
public class PropertyHashMapTest
{
    /** Test data. */
    private static final String KEY = "key";

    /** SUT. */
    private PropertyHashMap<Object> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new PropertyHashMap<Object>();
    }

    /**
     * Tests set property.
     */
    @Test
    public void testPutActual()
    {
        Object o = new Object();
        sut.put(KEY, o);
        Property<Object> result = sut.get(KEY);
        assertEquals(o, result.getValue());
    }

    /**
     * Tests set property.
     */
    @Test
    public void testPutPlaceholder()
    {
        sut.put(KEY, Integer.class, "id");
        Property<Object> result = sut.get(KEY);
        assertEquals(Integer.class, result.getType());
        assertEquals("id", result.getIdentity());
    }

    /**
     * Tests create alias.
     */
    @Test
    public void testPutAlias()
    {
        Object o = new Object();
        sut.put(KEY, o);
        sut.putAlias("alias", KEY);
        Property<Object> result1 = sut.get(KEY);
        Property<Object> result2 = sut.get("alias");

        assertEquals(o, result2.getValue());

        result1.setValue(8L);
        assertEquals(8L, result2.getValue());
    }
}
