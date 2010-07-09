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

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests getting composite stream ids for a given user.
 */
public class UserCompositeStreamSearchIdsMapperTest extends CachedMapperTest
{
    /**
     * The user to find stream search ids for.
     */
    private static final long USER_ID = 42;

    /**
     * System under test.
     */
    @Autowired
    private UserCompositeStreamSearchIdsMapper mapper;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> results = mapper.execute(USER_ID);
        assertEquals(2, results.size());

        results = mapper.execute(USER_ID);
        assertEquals(2, results.size());
    }
}
