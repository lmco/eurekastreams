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
package org.eurekastreams.server.domain.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eurekastreams.server.domain.stream.ActivityHashTag.ActivityHashTagPk;
import org.junit.Test;

/**
 * Test fixture for ActivityHashTag.
 */
public class ActivityHashTagTest
{
    /**
     * Test the loaded constructor.
     */
    @Test
    public void testLoadedConstructor()
    {
        final long activityId = 3828L;
        final long hashTagId = 88L;
        ActivityHashTag sut = new ActivityHashTag(activityId, hashTagId);

        assertEquals(activityId, sut.getActivityId());
        assertEquals(hashTagId, sut.getHashTagId());
    }

    /**
     * Test the properties and empty constructor.
     */
    @Test
    public void testActivityHashTagPk()
    {
        final long activityId = 3828L;
        final long hashTagId = 88L;
        ActivityHashTagPk sut = new ActivityHashTagPk(activityId, hashTagId);

        assertEquals(activityId, sut.getActivityId());
        assertEquals(hashTagId, sut.getHashTagId());
    }

    /**
     * Test the properties and empty constructor.
     */
    @Test
    public void testActivityHashTagPkEquals()
    {
        final long activityId = 3828L;
        final long hashTagId = 88L;
        ActivityHashTagPk sut1 = new ActivityHashTagPk(activityId, hashTagId);
        ActivityHashTagPk sut2 = new ActivityHashTagPk(activityId, hashTagId);

        assertEquals(sut1.hashCode(), sut2.hashCode());
        assertEquals(sut1, sut2);
    }

    /**
     * Test the properties and empty constructor.
     */
    @Test
    public void testActivityHashTagPkEquals2()
    {
        final long activityId = 3828L;
        final long hashTagId = 88L;
        ActivityHashTagPk sut1 = new ActivityHashTagPk(activityId, hashTagId);

        final long activityId2 = 888L;
        final long hashTagId2 = 8888L;
        ActivityHashTagPk sut2 = new ActivityHashTagPk(activityId2, hashTagId2);

        assertFalse(sut1.hashCode() == sut2.hashCode());
        assertFalse(sut1.equals(sut2.hashCode()));
    }
}
