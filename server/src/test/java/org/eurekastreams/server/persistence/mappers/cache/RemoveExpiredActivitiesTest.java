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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests mapper to delete expired activity ids.
 */
public class RemoveExpiredActivitiesTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private RemoveExpiredActivities sut;

    /**
     * Test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        final int commentCount = 10;
        final long activity1 = 6791L;
        final long activity2 = 6792L;
        final long activity3 = 6793L;

        final long comment4 = 4;
        final long comment8 = 8;
        final long comment9 = 9;
        final long comment10 = 10;

        getCache().setList(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activity1, Arrays.asList(comment8));
        getCache().setList(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activity2, Arrays.asList(comment4));
        getCache().setList(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activity3, Arrays.asList(comment9, comment10));

        Query activityQuery = getEntityManager().createQuery("from Activity");
        Query commentQuery = getEntityManager().createQuery("from Comment");

        List<Activity> activities = activityQuery.getResultList();
        assertEquals(5, activities.size());

        List<Comment> comments = commentQuery.getResultList();
        assertEquals(commentCount, comments.size());

        sut.execute(Arrays.asList(activity1, activity2, activity3));

        activities = activityQuery.getResultList();
        assertEquals(2, activities.size());

        comments = commentQuery.getResultList();
        assertEquals(6, comments.size());
    }
}
