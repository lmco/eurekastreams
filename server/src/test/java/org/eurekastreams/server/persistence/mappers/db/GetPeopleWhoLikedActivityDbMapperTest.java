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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for the get people who liked activity.
 *
 */
public class GetPeopleWhoLikedActivityDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetPeopleWhoLikedActivityDbMapper sut;

    /**
     * Test execute on 5 alone.
     */
    @Test
    public void testExecuteActivityWithResults()
    {
        final Long activityid = 6790L;
        final Long personId1 = 42L;
        final Long personId2 = 142L;

        List<List<Long>> values = sut.execute(Arrays.asList(activityid));

        final List<Long> peopleIds = values.iterator().next();

        assertEquals(2, peopleIds.size());
        assertTrue(peopleIds.contains(personId1));
        assertTrue(peopleIds.contains(personId2));
    }

    /**
     * Test execute on 6 alone.
     */
    @Test
    public void testExecuteActivityWithOutResults()
    {
        List<List<Long>> values = sut.execute(Arrays.asList(1L));

        final List<Long> peopleIds = values.iterator().next();

        assertEquals(0, peopleIds.size());
    }
}
