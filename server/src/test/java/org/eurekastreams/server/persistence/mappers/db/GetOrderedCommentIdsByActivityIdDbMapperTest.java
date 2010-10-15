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
 * Test for GetOrderedCommentIdsByActivityIdDbMapper.
 * 
 */
public class GetOrderedCommentIdsByActivityIdDbMapperTest extends MapperTest
{
    /**
     * Activity id to get comments for (from dataset.xml).
     */
    private final long activityId = 6789L;

    /**
     * System under test.
     */
    @Autowired
    private GetOrderedCommentIdsByActivityIdDbMapper sut;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> results = sut.execute(activityId);

        // assert correct # of results and that it's sorted asc.
        assertEquals(3, results.size());
        assertEquals(1, results.get(0).longValue());
        assertEquals(2, results.get(1).longValue());
        assertEquals(3, results.get(2).longValue());
    }

}
