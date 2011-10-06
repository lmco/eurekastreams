/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import javax.persistence.NoResultException;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for GetFieldFromTableByUniqueField.
 */
public class GetFieldFromTableByUniqueFieldTest extends MapperTest
{
    /**
     * Test successful execute.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithString()
    {
        GetFieldFromTableByUniqueField<String, String> sut = new GetFieldFromTableByUniqueField("Person", "accountId",
                "displayName");
        sut.setEntityManager(getEntityManager());

        assertEquals("Volgon-Swatter Prefect", sut.execute("fordp"));
    }

    /**
     * Test successful execute.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithLong()
    {
        final Long fordId = 42L;
        GetFieldFromTableByUniqueField<Long, String> sut = new GetFieldFromTableByUniqueField("Person", "id",
                "displayName");
        sut.setEntityManager(getEntityManager());

        assertEquals("Volgon-Swatter Prefect", sut.execute(fordId));
    }

    /**
     * Test execute when there's no results.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = NoResultException.class)
    public void testExecuteNoResult()
    {
        GetFieldFromTableByUniqueField<String, String> sut = new GetFieldFromTableByUniqueField("Person", "accountId",
                "displayName");
        sut.setEntityManager(getEntityManager());
        sut.execute("forsdfsdfdp");
    }

    /**
     * Test execute when there's no unique result.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = NoResultException.class)
    public void testExecuteNoUniqueResult()
    {
        GetFieldFromTableByUniqueField<Integer, String> sut = new GetFieldFromTableByUniqueField("Person",
                "followingCount", "displayName");
        sut.setEntityManager(getEntityManager());
        sut.execute(0);
    }

    /**
     * Test successful execute.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithStringAndCondition()
    {
        GetFieldFromTableByUniqueField<String, String> sut = new GetFieldFromTableByUniqueField("Person", "lastName",
                "accountId", "streamPostable = false");
        sut.setEntityManager(getEntityManager());

        assertEquals("fordp2", sut.execute("Prefect"));
    }
}
