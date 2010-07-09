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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetPersonIdsWithStarredActivity class.
 *
 */
public class GetPersonIdsWithStarredActivityTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetPersonIdsWithStarredActivity sut;

    /**
     * Test execute with bogus activityId.
     */
    @Test
    public void testBogusActivityId()
    {
        final long bogusId = -9879843;
        List<Long> results = sut.execute(bogusId);
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    /**
     * Test execute with activity Id that user has starred.
     */
    @Test
    public void testExecute()
    {
        final long activityId = 6789L;
        final long mrburnsId = 99;
        List<Long> results = sut.execute(activityId);
        assertNotNull(results);
        assertTrue(results.contains(mrburnsId));
    }

}
