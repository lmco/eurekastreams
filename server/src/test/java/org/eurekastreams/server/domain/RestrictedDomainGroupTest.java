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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Tests for RestrictedDomainGroup.
 */
public class RestrictedDomainGroupTest
{
    /**
     * Subject under test.
     */
    private RestrictedDomainGroup sut;

    /**
     * Test the getters and setters.
     */
    @Test
    public void testGettersAndSetters()
    {
        sut = new RestrictedDomainGroup();

        String bannerId = "123";
        sut.setBannerId(bannerId);
        assertEquals(bannerId, sut.getBannerId());

        String name = "name";
        sut.setName(name);
        assertEquals(name, sut.getName());

        sut.setName(null);
        assertEquals("", sut.getName());

        sut.setShortName(name);
        assertEquals(name, sut.getShortName());

        sut.setShortName(null);
        assertEquals("", sut.getShortName());

        Organization parentOrg = new Organization();
        sut.setParentOrganization(parentOrg);
        assertEquals(parentOrg, sut.getParentOrganization());

        long id = 7L;
        sut.setId(id);
        assertEquals(id, sut.getId());

        assertNull(sut.getCoordinators());
        assertFalse(sut.isCoordinator(""));
        assertFalse(sut.isPublicGroup());
    }

    /**
     * Tests whether the copy constructor grabs all the fields that it should.
     */
    @Test
    public void testCopyConstructor()
    {
        Organization parent = new Organization();
        Person testPerson = new Person();

        DomainGroup group = new DomainGroup("name", "shortName", testPerson);
        group.setBannerId("123");
        group.setParentOrganization(parent);

        RestrictedDomainGroup restrictedGroup = new RestrictedDomainGroup(group);

        assertEquals(group.getId(), restrictedGroup.getId());
        assertEquals(group.getBannerId(), restrictedGroup.getBannerId());
        assertEquals(group.getName(), restrictedGroup.getName());
        assertEquals(group.getParentOrganization(), restrictedGroup.getParentOrganization());
    }
}
