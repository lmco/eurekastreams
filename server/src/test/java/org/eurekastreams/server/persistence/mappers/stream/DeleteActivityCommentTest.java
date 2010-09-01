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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for DeleteActivityComment.
 * 
 */
public class DeleteActivityCommentTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteActivityComment sut;

    /**
     * Activity DAO.
     */
    @Autowired
    private BulkActivitiesDbMapper activityDAO;

    /**
     * Activity id from dataset.xml.
     */
    private final long activityId = 6789;

    /**
     * Test the execute method with bogus id.
     */
    @Test
    public void testExecuteBogusId()
    {
        final long bogusId = -9879843;
        assertTrue(sut.execute(bogusId));
    }

    /**
     * Test execute removing first comment.
     */
    @Test
    public void testExecuteRemoveFirstComment()
    {
        // hit the mapper to load activity into cache.
        loadAndVerifyInitialActivity();

        // delete a comment via sut.
        assertTrue(sut.execute(1L));

        // grab activity directly from cache and verify state has changed.
        ActivityDTO activity = activityDAO.execute(new ArrayList<Long>()
        {
            {
                add(activityId);
            }
        }).get(0);
        assertEquals(2, activity.getFirstComment().getId());
        assertEquals(3, activity.getLastComment().getId());
        assertEquals(2, activity.getCommentCount());

    }

    /**
     * Test execute removing last comment.
     */
    @Test
    public void testExecuteRemoveLastComment()
    {
        // hit the mapper to load activity into cache.
        loadAndVerifyInitialActivity();

        // delete a comment via sut.
        assertTrue(sut.execute(3L));

        // grab activity directly from cache and verify state has changed.
        ActivityDTO activity = activityDAO.execute(new ArrayList<Long>()
        {
            {
                add(activityId);
            }
        }).get(0);
        assertEquals(1, activity.getFirstComment().getId());
        assertEquals(2, activity.getLastComment().getId());
        assertEquals(2, activity.getCommentCount());
    }

    /**
     * Test execute removing a non-endpoint comments.
     */
    @Test
    public void testExecuteRemoveMiddleComment()
    {
        // hit the mapper to load activity into cache.
        loadAndVerifyInitialActivity();

        // delete a comment via sut.
        assertTrue(sut.execute(2L));

        // grab activity directly from cache and verify state has changed.
        ActivityDTO activity = activityDAO.execute(new ArrayList<Long>()
        {
            {
                add(activityId);
            }
        }).get(0);
        assertEquals(1, activity.getFirstComment().getId());
        assertEquals(3, activity.getLastComment().getId());
        assertEquals(2, activity.getCommentCount());
    }

    /**
     * Test execute removing all but one comment.
     */
    @Test
    public void testExecuteRemoveAllButOne()
    {
        // hit the mapper to load activity into cache.
        loadAndVerifyInitialActivity();

        // delete a comment via sut.
        assertTrue(sut.execute(1L));
        // delete a comment via sut.
        assertTrue(sut.execute(2L));

        // grab activity directly from cache and verify state has changed.
        ActivityDTO activity = activityDAO.execute(new ArrayList<Long>() { { add(activityId); } }).get(0);
        assertEquals(3, activity.getFirstComment().getId());
        assertNull(activity.getLastComment());
        assertEquals(1, activity.getCommentCount());
    }

    /**
     * Test execute removing all comments.
     */
    @Test
    public void testExecuteRemoveAll()
    {
        // hit the mapper to load activity into cache.
        loadAndVerifyInitialActivity();

        // delete a comment via sut.
        assertTrue(sut.execute(1L));
        // delete a comment via sut.
        assertTrue(sut.execute(2L));
        // delete a comment via sut.
        assertTrue(sut.execute(3L));

        // grab activity directly from cache and verify state has changed.
        ActivityDTO activity = activityDAO.execute(new ArrayList<Long>() { { add(activityId); } }).get(0);
        assertNull(activity.getFirstComment());
        assertNull(activity.getLastComment());
        assertEquals(0, activity.getCommentCount());
    }

    /**
     * Hit the mapper to load activity and verify state.
     */
    @SuppressWarnings("serial")
    private void loadAndVerifyInitialActivity()
    {
        // grab activity directly from cache and verify state.
        ActivityDTO activity = activityDAO.execute(new ArrayList<Long>()
        {
            {
                add(activityId);
            }
        }).get(0);
        assertEquals(1, activity.getFirstComment().getId());
        assertEquals(3, activity.getLastComment().getId());
        assertEquals(3, activity.getCommentCount());

    }
}
