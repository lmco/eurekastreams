/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetPersonIdsByLockedStatus.
 */
public class GetPersonIdsByLockedStatusTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetPersonIdsByLockedStatus sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetPersonIdsByLockedStatus();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        long total = (Long) getEntityManager().createQuery("SELECT COUNT(id) FROM Person").getSingleResult();

        assertTrue(total > 0);

        getEntityManager().createQuery("UPDATE Person SET accountLocked = false").executeUpdate();

        assertEquals(total, sut.execute(false).size());
        assertEquals(0, sut.execute(true).size());

        getEntityManager().createQuery("UPDATE Person SET accountLocked = true").executeUpdate();

        assertEquals(total, sut.execute(true).size());
        assertEquals(0, sut.execute(false).size());
    }
}
