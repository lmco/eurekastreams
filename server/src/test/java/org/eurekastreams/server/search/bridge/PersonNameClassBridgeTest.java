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
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.domain.Person;
import org.junit.Test;

/**
 * Test fixture for PersonNameClassBridge.
 */
public class PersonNameClassBridgeTest
{
    /**
     * System under test.
     */
    PersonNameClassBridge sut = new PersonNameClassBridge();

    /**
     * Test objectToString with null input.
     */
    @Test
    public void testObjectToStringNullObj()
    {
        assertNull(sut.objectToString(null));
    }

    /**
     * Test objectToString with null input.
     */
    @Test
    public void testObjectToStringInvalidType()
    {
        assertNull(sut.objectToString(1));
    }

    /**
     * Test objectToString with valid input.
     */
    @Test
    public void testObjectToStringValid()
    {
        Person person = new Person("foobar", "Jim", "Bob", "Smith", "JimmyBob");
        assertEquals("Jim Bob Smith JimmyBob", sut.objectToString(person));
    }

    /**
     * Test objectToString with null middle name.
     */
    @Test
    public void testObjectToStringNullMiddleName()
    {
        Person person = new Person("foobar", "Jim", null, "Smith", "JimmyBob");
        assertEquals("Jim  Smith JimmyBob", sut.objectToString(person));
    }
}
