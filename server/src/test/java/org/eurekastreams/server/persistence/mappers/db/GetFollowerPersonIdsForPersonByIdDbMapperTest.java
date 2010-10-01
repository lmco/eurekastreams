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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetFollowerPersonIdsForPersonByIdDbMapper.
 * 
 */
public class GetFollowerPersonIdsForPersonByIdDbMapperTest extends MapperTest
{
    /**
     * The user to find followers for.
     */
    private static final long USER_ID = 99;

    /**
     * The follower.
     */
    private static final long FOLLOWER_ID = 98;

    /**
     * System under test.
     */
    @Autowired
    private GetFollowerPersonIdsForPersonByIdDbMapper mapper;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> results = mapper.execute(USER_ID);
        assertEquals(2, results.size());
        assertEquals(new Long(FOLLOWER_ID), results.get(0));
    }

}
