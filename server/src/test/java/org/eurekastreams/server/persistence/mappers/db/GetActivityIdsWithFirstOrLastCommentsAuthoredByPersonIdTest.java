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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId.
 */
public class GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdTest extends
        MapperTest
{
    /**
     * System under test.
     */
    private GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId();
        sut.setEntityManager(getEntityManager());
    }
    
    /**
     * Test execute.
     */
    @Test
    public void testExecute1()
    {
        final Long fordId = 42L;
        final Long activity1 = 6790L;
        final Long activity2 = 6791L;
        final Long activity3 = 6792L;

        List<Long> expected = new ArrayList<Long>();
        expected.add(activity1);
        expected.add(activity2);
        expected.add(activity3);

        List<Long> actual = sut.execute(fordId);
        Collections.sort(actual);
        assertEquals(expected, actual);
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute2()
    {
        final Long fordId = 99L;
        final Long activity1 = 6789L;
        final Long activity2 = 6793L;

        List<Long> expected = new ArrayList<Long>();
        expected.add(activity1);
        expected.add(activity2);

        List<Long> actual = sut.execute(fordId);
        Collections.sort(actual);
        assertEquals(expected, actual);
    }
}
