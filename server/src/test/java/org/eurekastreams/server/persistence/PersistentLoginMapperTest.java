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
package org.eurekastreams.server.persistence;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.PersistentLogin;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for JpaPersistentLoginMapper.
 * 
 */
public class PersistentLoginMapperTest extends DomainEntityMapperTest
{
    /**
     * id of persistentLogin entry in DBUnit.
     */
    private final int idInDb = 696;

    /**
     * Expiration date value used for creation of PersistentLogin objects.
     */
    private final long expiryDate = 999999L;

    /**
     * JpaPersistentLoginMapper - system under test.
     */
    @Autowired
    private PersistentLoginMapper jpaPersistentLoginMapper;

    /**
     * Autowired QueryOptimizer.
     */
    @Autowired
    private QueryOptimizer queryOptimizer;

    /**
     * Make sure we can find a PersistentLogin when we ask for the account id.
     */
    @Test
    public void testFindByAccountId()
    {
        assertEquals(idInDb, jpaPersistentLoginMapper.findByAccountId("mrburns").getId());
    }

    /**
     * Test Delete by username.
     */
    @Test
    public void testDeletePersistentLoginByUserName()
    {
        jpaPersistentLoginMapper.deletePersistentLogin("mrburns");
        PersistentLogin foo = jpaPersistentLoginMapper.findByAccountId("mrburns");
        assertNull("PersistentLogin still present after delete", foo);
    }

    /**
     * test createOrUpdate create token expiration.
     */
    @Test
    public void testCreateOrUpdateCreateTokenExpirationDate()
    {
        PersistentLogin login = new PersistentLogin("homer", "doh", expiryDate);
        jpaPersistentLoginMapper.createOrUpdate(login);

        PersistentLogin sutResult = jpaPersistentLoginMapper.findByAccountId("homer");
        assertTrue("PersistentLogin id is zero, create didn't happen", 0L != sutResult.getId());

        assertEquals("PersistentLogin tokenExpirationDate not created correctly", expiryDate, sutResult
                .getTokenExpirationDate());
    }

    /**
     * test createOrUpdate update token expiration date.
     */
    @Test
    public void testCreateOrUpdateUpdateTokenExpirationDate()
    {
        // this user is already in db so update should happen.
        PersistentLogin login = new PersistentLogin("mrburns", "excellent1", expiryDate);
        jpaPersistentLoginMapper.createOrUpdate(login);

        PersistentLogin sutResult = jpaPersistentLoginMapper.findByAccountId("mrburns");
        assertEquals("PersistentLogin id is not same as original, update didn't happen", idInDb, sutResult.getId());

        assertEquals("PersistentLogin tokenExpirationDate not updated correctly", expiryDate, sutResult
                .getTokenExpirationDate());
    }

    /**
     * test createOrUpdate create token value.
     */
    @Test
    public void testCreateOrUpdateCreateTokenValue()
    {
        PersistentLogin login = new PersistentLogin("flanders", "diddly", expiryDate);
        jpaPersistentLoginMapper.createOrUpdate(login);

        PersistentLogin sutResult = jpaPersistentLoginMapper.findByAccountId("flanders");
        assertTrue("PersistentLogin id is zero, create didn't happen", 0L != sutResult.getId());

        assertEquals("PersistentLogin tokenValue not created correctly", "diddly", sutResult.getTokenValue());
    }

    /**
     * Test createOrUpdate update token value.
     */
    @Test
    public void testCreateOrUpdateUpdateTokenValue()
    {
        // this user is already in db so update should happen.
        PersistentLogin login = new PersistentLogin("mrburns", "excellent", expiryDate);
        jpaPersistentLoginMapper.createOrUpdate(login);

        PersistentLogin sutResult = jpaPersistentLoginMapper.findByAccountId("mrburns");
        assertEquals("PersistentLogin id is not same as original, update didn't happen", idInDb, sutResult.getId());

        assertEquals("PersistentLogin tokenValue not updated", "excellent", sutResult.getTokenValue());
    }

    /**
     * Basic test to ensure the setTheme works properly.
     */
    @Test
    public void testGetDomainEntityName()
    {
        PersistentLoginMapperSubClassSupport testSubClass = new PersistentLoginMapperSubClassSupport(queryOptimizer);

        assertEquals("getDomainEntityName() doesn't return PersistentLogin", "PersistentLogin", testSubClass
                .getDomainEntityName());
    }
}
