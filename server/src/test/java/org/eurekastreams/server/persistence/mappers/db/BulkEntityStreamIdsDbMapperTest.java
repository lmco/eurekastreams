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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Bulk entity stream id DB mapper test.
 */
public class BulkEntityStreamIdsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private BulkEntityStreamIdsDbMapper sut = null;

    /**
     * Setup test fixtures.
     */
    @Before
    public void before()
    {
        sut = new BulkEntityStreamIdsDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Tests mapping with a person.
     */
    @Test
    public void testExecuteForPerson()
    {
        final Long personId = 42L;
        final Long expectedEntityStreamId = 5L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(personId, EntityType.PERSON);

        List<Long> results = sut.execute(request);

        assertEquals(1, results.size());
        assertEquals(expectedEntityStreamId, results.get(0));
    }

    /**
     * Tests mapping person with no results.
     */
    @Test(expected = NoResultException.class)
    public void testExecuteForPersonNoResults()
    {
        final Long personId = 0L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(personId, EntityType.PERSON);

        sut.execute(request);
    }

    /**
     * Tests mapping with a group.
     */
    @Test
    public void testExecuteForGroup()
    {
        final Long groupId = 1L;
        final Long expectedEntityStreamId = 9L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(groupId, EntityType.GROUP);

        List<Long> results = sut.execute(request);

        assertEquals(1, results.size());
        assertEquals(expectedEntityStreamId, results.get(0));
    }

    /**
     * Tests mapping for a group with no results.
     */
    @Test(expected = NoResultException.class)
    public void testExecuteForGroupNoResults()
    {
        final Long groupId = 0L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(groupId, EntityType.GROUP);

        sut.execute(request);
    }

    /**
     * Tests mapping an unhandled type.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteForUnhandledType()
    {
        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();
        request.put(0L, EntityType.NOTSET);

        List<Long> results = sut.execute(request);
    }
}
