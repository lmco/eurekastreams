/*
 * Copyright (c) 2012-2012 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.stream.Activity;
import org.junit.Test;

/**
 * Tests IsPersonVisibleInSearchClassBridge.
 */
public class IsPersonVisibleInSearchClassBridgeTest
{
    /**
     * System under test.
     */
    private final IsPersonVisibleInSearchClassBridge sut = new IsPersonVisibleInSearchClassBridge();

    /**
     * Test objectToString when passed null input.
     */
    @Test
    public void testObjectToStringNullInput()
    {
        assertNull(sut.objectToString(null));
    }

    /**
     * Test objectToString when passed an invalid type.
     */
    @Test
    public void testObjectToStringWrongType()
    {
        assertNull(sut.objectToString(new Activity()));
    }

    /**
     * Test objectToString for locked user.
     */
    @Test
    public void testObjectToStringLocked()
    {
        Person person = new Person();
        person.setAccountLocked(true);
        person.setAccountDeactivated(false);
        assertEquals("f", sut.objectToString(person));
    }

    /**
     * Test objectToString for deactivated user.
     */
    @Test
    public void testObjectToStringDeactivated()
    {
        Person person = new Person();
        person.setAccountLocked(false);
        person.setAccountDeactivated(true);
        assertEquals("f", sut.objectToString(person));
    }

    /**
     * Test objectToString for locked and deactivated user.
     */
    @Test
    public void testObjectToStringLockedAndDeactivated()
    {
        Person person = new Person();
        person.setAccountLocked(true);
        person.setAccountDeactivated(true);
        assertEquals("f", sut.objectToString(person));
    }

    /**
     * Test objectToString for normal user.
     */
    @Test
    public void testObjectToStringNormal()
    {
        Person person = new Person();
        person.setAccountLocked(false);
        person.setAccountDeactivated(false);
        assertEquals("t", sut.objectToString(person));
    }
}
