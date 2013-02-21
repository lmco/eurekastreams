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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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

    /** Test data. */
    private static final long PERSON_ID = 42L;

    /**
     * Test updating with new additional properties.
     */
    @Test
    public void test()
    {
        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        assertTrue(dbPerson.getAdditionalProperties() == null);

        Person p = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");
        HashMap<String, String> additional = new HashMap<String, String>();
        additional.put("additional", "additionalValue");
        p.setAdditionalProperties(additional);

        sut.execute(p);

        getEntityManager().flush();
        getEntityManager().clear();

        Person resultPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        assertTrue(dbPerson.getAdditionalProperties() != null);
        assertTrue(resultPerson.getAdditionalProperties().get("additional").equals("additionalValue"));
    }

    /**
     * Test where additional properties are specified but do not need to be updated.
     */
    @Test
    public void testNoUpdatedProperties()
    {
        HashMap<String, String> additional = new HashMap<String, String>();
        additional.put("additional", "additionalValue");

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();
        assertTrue(dbPerson.getDisplayNameSuffix().equals(""));

        dbPerson.setAdditionalProperties(additional);
        getEntityManager().flush();

        assertTrue(dbPerson.getAdditionalProperties() != null);

        Person p = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");
        assertTrue(p.getDisplayNameSuffix().equals(""));
        p.setAdditionalProperties(additional);
        
        UpdatePersonResponse response = sut.execute(p);        
        assertFalse(response.wasUserUpdated());
        assertFalse(response.wasDisplayNameUpdated());
    }

    /**
     * Test where additional properties are no longer specified on ldap person so db person needs to be updated.
     */
    @Test
    public void testRemoveProperties()
    {
        HashMap<String, String> additional = new HashMap<String, String>();
        additional.put("additional", "additionalValue");

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();
        dbPerson.setAdditionalProperties(additional);
        getEntityManager().flush();

        assertTrue(dbPerson.getAdditionalProperties() != null);

        Person p = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");

        UpdatePersonResponse response = sut.execute(p);

        assertTrue(response.wasUserUpdated());
        assertFalse(response.wasDisplayNameUpdated());
        assertTrue(dbPerson.getAdditionalProperties() == null);
    }

    /**
     * Test updating with new last name.
     */
    @Test
    public void testNewLastName()
    {
        final String newLastName = "NewLastName";

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        assertTrue(dbPerson.getLastName().equals("Prefect"));

        Person p = new Person("fordp", "Ford", "X", newLastName, "Volgon-Swatter");

        UpdatePersonResponse response = sut.execute(p);
        assertTrue(response.wasUserUpdated());
        assertTrue(response.wasDisplayNameUpdated());

        getEntityManager().flush();
        getEntityManager().clear();

        Person resultPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        assertTrue(resultPerson.getLastName().equals(newLastName));
    }

    /**
     * Test updating with new display name suffix.
     */
    @Test
    public void testNewDisplayNameSuffix()
    {
        final String newLastName = "NewLastName";

        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        assertTrue(dbPerson.getDisplayNameSuffix().equals(""));

        Person p = new Person("fordp", "Ford", "X", newLastName, "Volgon-Swatter");
        p.setDisplayNameSuffix(" FOO");

        UpdatePersonResponse response = sut.execute(p);
        assertTrue(response.wasUserUpdated());
        assertTrue(response.wasDisplayNameUpdated());

        getEntityManager().flush();
        getEntityManager().clear();

        Person resultPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        assertTrue(resultPerson.getDisplayNameSuffix().equals(" FOO"));
    }

    /**
     * Test updating with new, changed, unchanged, and deleted additional properties.
     */
    @Test
    public void testUpdatePropertyMix()
    {
        // set up DB person
        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();
        HashMap<String, String> dbProps = new HashMap<String, String>();
        dbProps.put("key1", "value1");
        dbProps.put("key2", "value2");
        dbProps.put("key3", "value3");
        dbProps.put("key4", "value4");
        dbProps.put("key5", "value5");
        dbPerson.setAdditionalProperties(dbProps);
        getEntityManager().flush();

        // set up LDAP person
        Person ldapPerson = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");
        HashMap<String, String> ldapProps = new HashMap<String, String>();
        ldapProps.put("key1", "value1");
        ldapProps.put("key3", "value3A");
        ldapProps.put("key5", "value5A");
        ldapProps.put("key7", "value7");
        ldapPerson.setAdditionalProperties(ldapProps);

        // execute
        UpdatePersonResponse result = sut.execute(ldapPerson);

        // verify
        assertTrue(result.wasUserUpdated());
        getEntityManager().flush();
        getEntityManager().clear();
        Person resultPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        HashMap<String, String> resultProps = resultPerson.getAdditionalProperties();
        assertNotNull(resultProps);
        assertEquals(4, resultProps.size());
        assertEquals("value1", resultProps.get("key1"));
        assertEquals("value3A", resultProps.get("key3"));
        assertEquals("value5A", resultProps.get("key5"));
        assertEquals("value7", resultProps.get("key7"));
    }

    /**
     * Test updating with unchanged and deleted additional properties.
     */
    @Test
    public void testUpdatePropertyDeleteSome()
    {
        // set up DB person
        Person dbPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();
        HashMap<String, String> dbProps = new HashMap<String, String>();
        dbProps.put("key1", "value1");
        dbProps.put("key2", "value2");
        dbPerson.setAdditionalProperties(dbProps);
        getEntityManager().flush();

        // set up LDAP person
        Person ldapPerson = new Person("fordp", "Ford", "X", "Prefect", "Volgon-Swatter");
        HashMap<String, String> ldapProps = new HashMap<String, String>();
        ldapProps.put("key1", "value1");
        ldapPerson.setAdditionalProperties(ldapProps);

        // execute
        UpdatePersonResponse result = sut.execute(ldapPerson);

        // verify
        assertTrue(result.wasUserUpdated());
        getEntityManager().flush();
        getEntityManager().clear();
        Person resultPerson = (Person) getEntityManager().createQuery("FROM Person WHERE id = :id")
                .setParameter("id", PERSON_ID).getSingleResult();

        HashMap<String, String> resultProps = resultPerson.getAdditionalProperties();
        assertNotNull(resultProps);
        assertEquals(1, resultProps.size());
        assertEquals("value1", resultProps.get("key1"));
    }

}
