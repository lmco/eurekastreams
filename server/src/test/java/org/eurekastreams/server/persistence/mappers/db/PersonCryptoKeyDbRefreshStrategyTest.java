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
package org.eurekastreams.server.persistence.mappers.db;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eurekastreams.server.domain.PersonCryptoKey;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests PersonCryptoKeyDbRefreshStrategy.
 */
public class PersonCryptoKeyDbRefreshStrategyTest extends MapperTest
{
    /** SUT. */
    private PersonCryptoKeyDbRefreshStrategy sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new PersonCryptoKeyDbRefreshStrategy();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Tests nominal case.
     */
    @Test
    public void testExecute()
    {
        final long userid = 98L;

        Query query = getEntityManager().createQuery("FROM PersonCryptoKey WHERE personId=" + userid);
        assertTrue(query.getResultList().isEmpty());

        byte[] key = "Some Data".getBytes();
        sut.refresh(userid, key);

        List list = query.getResultList();
        assertEquals(1, list.size());
        PersonCryptoKey entity = (PersonCryptoKey) list.get(0);
        assertEquals(userid, entity.getPersonId());
        Assert.assertArrayEquals(key, entity.getKey());
    }

    /**
     * Tests nominal case.
     */
    @Test(expected = PersistenceException.class)
    public void testExecuteAlreadyPresent()
    {
        final long userid = 4507L;

        Query query = getEntityManager().createQuery("FROM PersonCryptoKey WHERE personId=" + userid);
        assertEquals(1, query.getResultList().size());

        byte[] key = "Some Data".getBytes();
        sut.refresh(userid, key);
    }
}
