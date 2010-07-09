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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for getting unique commentors for a given activity id.
 */
public class GetCommentorIdsByActivityIdTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetCommentorIdsByActivityId sut;

    /**
     * Test execute for one commentor.
     */
    @Test
    public void testExecuteOrg5()
    {
        final Long activityId = 6789L;
        final Long commentorId = 99L;

        List<Long> commentorIds = sut.execute(activityId);
        assertEquals(1, commentorIds.size());
        assertTrue(commentorIds.contains(commentorId));
    }

    /**
     * Test execute for multiple commentors.
     */
    @Test
    public void testExecuteOrg6()
    {
        final Long activityId = 6790L;
        final Long commentorId1 = 42L;
        final Long commentorId2 = 99L;

        List<Long> commentorIds = sut.execute(activityId);
        assertEquals(2, commentorIds.size());
        assertTrue(commentorIds.contains(commentorId1));
        assertTrue(commentorIds.contains(commentorId2));
    }
}
