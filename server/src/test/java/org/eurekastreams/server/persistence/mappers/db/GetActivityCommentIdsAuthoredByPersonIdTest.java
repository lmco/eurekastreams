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

import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetActivityCommentIdsAuthoredByPersonId.
 */
public class GetActivityCommentIdsAuthoredByPersonIdTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetActivityCommentIdsAuthoredByPersonId sut = new GetActivityCommentIdsAuthoredByPersonId();

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test the execute() method, while expecting results.
     */
    @Test
    public void testExecuteExpectingResults()
    {
        final Long fordId = 42L;
        final Long burnsId = 99L;

        List<Long> fordComments = sut.execute(fordId);
        assertEquals(4, fordComments.size());
        assertEquals(new Long(4), fordComments.get(0));
        assertEquals(new Long(5), fordComments.get(1));
        assertEquals(new Long(7), fordComments.get(2));
        assertEquals(new Long(8), fordComments.get(3));

        List<Long> burnsComments = sut.execute(burnsId);
        assertEquals(5, burnsComments.size());

        Collections.sort(burnsComments);
        assertEquals(new Long(1), burnsComments.get(0));
        assertEquals(new Long(2), burnsComments.get(1));
        assertEquals(new Long(3), burnsComments.get(2));
        assertEquals(new Long(6), burnsComments.get(3));
    }

    /**
     * Test the execute() method, while expecting no results.
     */
    @Test
    public void testExecuteExpectingNoResults()
    {
        final Long fordp2Id = 142L;
        assertEquals(0, sut.execute(fordp2Id).size());
    }
}
