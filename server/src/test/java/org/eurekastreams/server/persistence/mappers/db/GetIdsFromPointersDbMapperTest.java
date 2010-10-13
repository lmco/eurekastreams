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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        List<String> accountIds = new ArrayList<String>();
        accountIds.add("fordp");
        accountIds.add("fordp2");

        Map<String, Long> results = sut.execute(accountIds);
        assertEquals(2, results.size());
        assertEquals(fordId, results.get("fordp"));
        assertEquals(ford2Id, results.get("fordp2"));
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

        Map<String, Long> results = sut.execute(accountIds);
        assertEquals(0, results.size());
    }

    /**
     * Test getting people ids by account id with no inputs.
     */
    @Test
    public void testExecuteForPeopleAccountIdsWithNoInputs()
    {
        GetIdsFromPointersDbMapper<String> sut = new GetIdsFromPointersDbMapper<String>("accountId", Person.class);
        sut.setEntityManager(getEntityManager());
        Map<String, Long> results = sut.execute(new ArrayList<String>());
        assertEquals(0, results.size());
    }
}
