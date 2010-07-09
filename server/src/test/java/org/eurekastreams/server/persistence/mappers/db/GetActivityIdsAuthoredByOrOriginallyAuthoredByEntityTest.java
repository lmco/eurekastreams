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
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetActivityIdsAuthoredByEntity.
 */
public class GetActivityIdsAuthoredByOrOriginallyAuthoredByEntityTest extends
        MapperTest
{
    /**
     * System under test.
     */
    private GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute gets back activities with a mix of actor and original actor
     * ids.
     */
    @Test
    public void testExecuteWithOriginalActorActivities()
    {
        final Long activityId1 = 6789L;
        final Long activityId2 = 6790L;
        final Long activityId3 = 6791L;
        final Long activityId4 = 6793L;

        List<Long> actual = sut.execute("smithers", EntityType.PERSON);
        Collections.sort(actual);

        List<Long> expected = new ArrayList<Long>();
        expected.add(activityId1);
        expected.add(activityId2);
        expected.add(activityId3);
        expected.add(activityId4);

        assertEquals(expected, actual);
    }

    /**
     * Test execute gets back activities with a mix of actor and original actor
     * ids.
     */
    @Test
    public void testExecuteWithPersonWithOnlyOriginalAuthor()
    {
        final Long activityId1 = 6792L;

        List<Long> actual = sut.execute("csagan", EntityType.PERSON);
        Collections.sort(actual);

        List<Long> expected = new ArrayList<Long>();
        expected.add(activityId1);

        assertEquals(expected, actual);
    }

    /**
     * Test execute() with no expected results.
     */
    @Test
    public void testExecuteWithNoResults()
    {
        List<Long> actual = sut.execute("sdfsdfdsf", EntityType.PERSON);

        assertEquals(0, actual.size());
    }
}
