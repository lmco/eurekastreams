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

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.junit.Test;

/**
 * Test fixture for OrganizationToShortNameBridge.
 */
public class OrganizationToShortNameFieldBridgeTest
{
    /**
     * System under test.
     */
    private OrganizationToShortNameFieldBridge sut = new OrganizationToShortNameFieldBridge();

    /**
     * Test object to string when passed in an Organization.
     */
    @Test
    public void testObjectToString()
    {
        String shortName = "greenones";
        Organization o = new Organization("Looking for Spaceships", shortName);
        assertEquals(shortName, sut.objectToString(o));
    }

    /**
     * Test object to string when passed in a null Organization.
     */
    @Test
    public void testObjectToStringWithNullOrganization()
    {
        assertNull(sut.objectToString(null));
    }

    /**
     * Test object to string when passed a non-Organization.
     */
    @Test
    public void testObjectToStringWithInvalidInput()
    {
        assertNull(sut.objectToString(new Person()));
    }

    /**
     * Test stringToObject().
     */
    @Test
    public void testStringToObject()
    {
        assertEquals("FOO", sut.stringToObject("FOO"));
    }
}
