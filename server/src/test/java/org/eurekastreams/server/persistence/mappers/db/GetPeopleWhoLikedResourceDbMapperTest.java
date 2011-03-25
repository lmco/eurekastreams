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

import java.util.List;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetPeopleWhoLikedResourceDbMapper.
 */
public class GetPeopleWhoLikedResourceDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetPeopleWhoLikedResourceDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetPeopleWhoLikedResourceDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Text execute, expecting results.
     */
    @Test
    public void testExecuteWithResults()
    {
        final Long personId1 = 42L;
        final Long personId2 = 99L;

        List<Long> peopleIds = sut.execute(new SharedResourceRequest("http://foo.com/foo.html"));
        assertEquals(2, peopleIds.size());
        assertTrue(peopleIds.contains(personId1));
        assertTrue(peopleIds.contains(personId2));
    }

    /**
     * Text execute, expecting no results.
     */
    @Test
    public void testExecuteWithNoResults()
    {
        List<Long> peopleIds = sut.execute(new SharedResourceRequest("http://foo.foo.com/foo.html"));
        assertEquals(0, peopleIds.size());
    }

    /**
     * Text execute with a non existent resource.
     */
    @Test
    public void testExecuteWithBadResource()
    {
        List<Long> peopleIds = sut.execute(new SharedResourceRequest("http://blah.com"));
        assertEquals(0, peopleIds.size());
    }
}
