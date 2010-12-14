/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.UpdatePersonResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for UpdatePersonMapper.
 */
public class UpdatePersonMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private UpdatePersonMapper sut;

    /**
     * Test updating with new additional properties.
     */
    @Test
    public void test()
    {
        final long personId = 42L;

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id").setParameter("id",
                personId).getSingleResult();

        assertTrue(dbPerson.getAdditionalProperties() == null);

        Person p = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");
        HashMap<String, String> additional = new HashMap<String, String>();
        additional.put("additional", "additionalValue");
        p.setAdditionalProperties(additional);

        sut.execute(p);

        getEntityManager().flush();
        getEntityManager().clear();

        Person resultPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id").setParameter("id",
                personId).getSingleResult();

        assertTrue(dbPerson.getAdditionalProperties() != null);
        assertTrue(resultPerson.getAdditionalProperties().get("additional").equals("additionalValue"));
    }

    /**
     * Test where additional properties are specified but do not need to be updated.
     */
    @Test
    public void testNoUpdatedProperties()
    {
        final long personId = 42L;
        HashMap<String, String> additional = new HashMap<String, String>();
        additional.put("additional", "additionalValue");

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id").setParameter("id",
                personId).getSingleResult();

        dbPerson.setAdditionalProperties(additional);
        getEntityManager().flush();
        
        assertTrue(dbPerson.getAdditionalProperties() != null);

        Person p = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");
        p.setAdditionalProperties(additional);

        UpdatePersonResponse response = sut.execute(p);

        assertFalse(response.wasUserUpdated());
    }

    /**
     * Test where additional properties are no longer specified on ldap person so db person needs to be updated.
     */
    @Test
    public void testRemoveProperties()
    {
        final long personId = 42L;
        HashMap<String, String> additional = new HashMap<String, String>();
        additional.put("additional", "additionalValue");

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id").setParameter("id",
                personId).getSingleResult();
        dbPerson.setAdditionalProperties(additional);
        getEntityManager().flush();

        assertTrue(dbPerson.getAdditionalProperties() != null);

        Person p = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");

        UpdatePersonResponse response = sut.execute(p);

        assertTrue(response.wasUserUpdated());
        assertTrue(dbPerson.getAdditionalProperties() == null);
    }

    /**
     * Test updating with new last name.
     */
    @Test
    public void testNewLastName()
    {
        final long personId = 42L;
        final String newLastName = "NewLastName";

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id").setParameter("id",
                personId).getSingleResult();

        assertTrue(dbPerson.getLastName().equals("Prefect"));

        Person p = new Person("fordp", "Ford", "X", newLastName, "Volgon-Swatter");

        sut.execute(p);

        getEntityManager().flush();
        getEntityManager().clear();

        Person resultPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id").setParameter("id",
                personId).getSingleResult();

        assertTrue(resultPerson.getLastName().equals(newLastName));
    }
}
