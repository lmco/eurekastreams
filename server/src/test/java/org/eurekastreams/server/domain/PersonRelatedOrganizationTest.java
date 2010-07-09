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
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.PersonRelatedOrganization.PersonRelatedOrganizationPk;
import org.junit.Test;

/**
 * Test fixture for PersonRelatedOrganization.
 */
public class PersonRelatedOrganizationTest
{
    /**
     * Test the properties.
     */
    @Test
    public void testConstructorAndProperties()
    {
        PersonRelatedOrganization sut = new PersonRelatedOrganization(1, 2);
        assertEquals(1, sut.getPersonId());
        assertEquals(2, sut.getOrganizationId());
    }

    /**
     * Test the PersonRelatedOrganizationPk properties.
     */
    @Test
    public void testPersonRelatedOrganizationPkProperties()
    {
        PersonRelatedOrganizationPk pk = new PersonRelatedOrganizationPk(1, 2);
        assertEquals(1, pk.getPersonId());
        assertEquals(2, pk.getOrganizationId());
    }

    /**
     * Test the PersonRelatedOrganizationPk hashcode and equals.
     */
    @Test
    public void testPersonRelatedOrganizationPkHashcodeAndEquals()
    {
        PersonRelatedOrganizationPk pk1 = new PersonRelatedOrganizationPk(1, 2);
        PersonRelatedOrganizationPk pk2 = new PersonRelatedOrganizationPk(1, 2);
        PersonRelatedOrganizationPk pk3 = new PersonRelatedOrganizationPk(3, 4);
        PersonRelatedOrganizationPk pk4 = new PersonRelatedOrganizationPk(1, 4);

        assertEquals(pk1.hashCode(), pk2.hashCode());
        assertTrue(pk1.equals(pk2));

        assertFalse(pk1.hashCode() == pk3.hashCode());
        assertFalse(pk1.equals(pk3));

        assertFalse(pk1.equals("hi"));

        assertFalse(pk1.equals(pk4));
    }

}
