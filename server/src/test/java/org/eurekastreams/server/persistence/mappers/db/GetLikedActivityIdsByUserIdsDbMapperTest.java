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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests getting liked activity ids for a given user.
 */
public class GetLikedActivityIdsByUserIdsDbMapperTest extends CachedMapperTest
{
    /**
     * The user to find liked activities for.
     */
    private static final long USER_ID = 99;

    /**
     * The id to be found.
     */
    private static final long ACTIVITY_ID = 6789;

    /**
     * System under test.
     */
    private GetLikedActivityIdsByUserIdsDbMapper mapper = new GetLikedActivityIdsByUserIdsDbMapper();
    
    /**
     * Setup fixtures.
     */
    @Before
    public void setup()
    {
        mapper.setCache(getCache());
        mapper.setEntityManager(getEntityManager());
    }

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        Collection<Collection<Long>> results = mapper.execute(Arrays.asList(USER_ID));
        assertEquals(1, results.size());
        assertEquals(1, results.toArray().length);
        assertEquals(new Long(ACTIVITY_ID), results.toArray()[0]);
    }
}
