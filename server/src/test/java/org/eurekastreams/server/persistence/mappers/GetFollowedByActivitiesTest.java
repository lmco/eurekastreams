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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Test for get followed by activities.
 *
 */
public class GetFollowedByActivitiesTest extends CachedMapperTest
{
    /**
     * User id from dataset.xml.
     */
    private static final long USER_ID = 99L;

    /**
     * System under test.
     */
    @Autowired
    @Qualifier("getFollowedByActivities")
    private ChainedDomainMapper<Long, List<Long>> getFollowedByActivities;

    /**
     * Test getFollowedActivityIds method.
     */
    @Test
    public void testGetFollowedActivityIdsWithPersonGroupActivities()
    {
        List<Long> results = getFollowedByActivities.execute(USER_ID);

        //assert correct number of results.
        assertEquals(2, results.size());

        //assert list is sorted correctly.
        assertTrue(results.get(0) > results.get(1));
    }
}
