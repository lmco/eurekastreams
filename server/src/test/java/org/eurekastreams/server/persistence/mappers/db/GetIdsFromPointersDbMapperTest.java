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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for GetIdsFromPointersDbMapper.
 */
public class GetIdsFromPointersDbMapperTest extends MapperTest
{
    /**
     * Test getting people ids by account id.
     */
    @Test
    public void testExecuteForPeopleAccountIds()
    {
        GetIdsFromPointersDbMapper<String> sut = new GetIdsFromPointersDbMapper<String>("accountId", Person.class);
        sut.setEntityManager(getEntityManager());
        final Long fordId = 42L;
        final Long ford2Id = 142L;
        final Long csaganId = 4507L;
        final Long smithersId = 98L;
        final Long mrburnsId = 99L;

        List<String> accountIds = new ArrayList<String>();
        accountIds.add("fordp");
        accountIds.add("fordp2");
        accountIds.add("DOESNOTEXIST1");
        accountIds.add("mrburns");
        accountIds.add("smithers");
        accountIds.add("DOESNOTEXIST2");
        accountIds.add("csagan");

        List<Long> results = sut.execute(accountIds);
        System.out.println(results);
        assertEquals(7, results.size());
        assertEquals(fordId, results.get(0));
        assertEquals(ford2Id, results.get(1));
        assertNull(results.get(2));
        assertEquals(mrburnsId, results.get(3));
        assertEquals(smithersId, results.get(4));
        assertNull(results.get(5));
        assertEquals(csaganId, results.get(6));
    }

    /**
     * Test getting people ids by account id with no matches.
     */
    @Test
    public void testExecuteForPeopleAccountIdsWithNoMatches()
    {
        GetIdsFromPointersDbMapper<String> sut = new GetIdsFromPointersDbMapper<String>("accountId", Person.class);
        sut.setEntityManager(getEntityManager());

        List<String> accountIds = new ArrayList<String>();
        accountIds.add("xxx");
        accountIds.add("yyy");

        List<Long> results = sut.execute(accountIds);
        assertEquals(2, results.size());
        assertNull(results.get(0));
        assertNull(results.get(1));
    }

    /**
     * Test getting people ids by account id with no inputs.
     */
    @Test
    public void testExecuteForPeopleAccountIdsWithNoInputs()
    {
        GetIdsFromPointersDbMapper<String> sut = new GetIdsFromPointersDbMapper<String>("accountId", Person.class);
        sut.setEntityManager(getEntityManager());

        assertEquals(0, sut.execute(new ArrayList<String>()).size());
    }
}
