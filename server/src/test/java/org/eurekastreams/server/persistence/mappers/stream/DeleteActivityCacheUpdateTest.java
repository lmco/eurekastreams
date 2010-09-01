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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.action.request.stream.DeleteActivityCacheUpdateRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for DeleteActivityCacheUpdate class.
 *
 */
public class DeleteActivityCacheUpdateTest extends CachedMapperTest
{
    /**
     * System cache.
     */
    @Autowired
    private Cache cache;

    /**
     * CompositeStreamActivityIds DAO.
     */
    @Autowired
    private CompositeStreamActivityIdsMapper compositeStreamActivityIdsDAO;

    /**
     * StarredActivity DAO.
     */
    @Autowired
    private GetStarredActivityIds starredActivityIdDAO;

    /**
     * Activity by id DAO.
     */
    @Autowired
    private BulkActivitiesDbMapper activityByIdDAO;

    /**
     * Comment ids by activity id DAO.
     */
    @Autowired
    private GetOrderedCommentIdsByActivityId commentIdsByActivityIdDAO;

    /**
     * System under test.
     */
    @Autowired
    private DeleteActivityCacheUpdate sut;

    /**
     * Activity id from dataset.xml.
     */
    private final long activityId = 6789;

    /**
     * Comment id.
     */
    private final long commentId = 1L;

    /**
     * Smithers compositeStreamId.
     */
    private final long simthersCompStreamId = 4L;

    /**
     * additional compositeStreamId containing destination stream.
     */
    private final long customCompStreamId = 17L;

    /**
     * Smithers user id.
     */
    private final long smithersId = 98L;

    /**
     * Mr. Burns user id.
     */
    private final long mrburnsId = 99L;

    /**
     * Test execute method.
     */
    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void testExecute()
    {
        loadAndVerifyInitialActivity();

        List<ActivityDTO> activityList =
            activityByIdDAO.execute(Arrays.asList(activityId));
        
        assertEquals(1, activityList.size());
        
        ActivityDTO activity = activityList.get(0);
        assertNotNull(activity);

        List<Long> commentIds = new ArrayList<Long>(3);
        commentIds.add(1L);
        commentIds.add(2L);
        commentIds.add(3L);

        List<Long> personIdsWithStarredActivity = new ArrayList<Long>(1);
        personIdsWithStarredActivity.add(mrburnsId);

        //update cache
        assertNotNull(sut.execute(
                new DeleteActivityCacheUpdateRequest(
                        activity, commentIds, personIdsWithStarredActivity)));

        List<Long> activityIds =
            cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + simthersCompStreamId);
        assertEquals(1, activityIds.size());

        List<Long> customActivityIds =
            cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + customCompStreamId);
        assertEquals(1, customActivityIds.size());

        List<Long> starredActivityIds =
            cache.getList(CacheKeys.STARRED_BY_PERSON_ID + mrburnsId);
        assertEquals(0, starredActivityIds.size());

        List<Long> commentIdsList =
            cache.getList(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activityId);
        assertNull(commentIdsList);

        assertNull(cache.get(CacheKeys.ACTIVITY_BY_ID + activityId));

        assertNull(cache.get(CacheKeys.COMMENT_BY_ID + 1));
        assertNull(cache.get(CacheKeys.COMMENT_BY_ID + 2));
        assertNull(cache.get(CacheKeys.COMMENT_BY_ID + 3));
    }



    /**
     * Hit the mapper to load activity into cache and verify state.
     */
    @SuppressWarnings({ "unchecked" })
    private void loadAndVerifyInitialActivity()
    {
        //verify delete activity comments from DB.
        assertEquals(1, getEntityManager().createQuery("FROM Comment c WHERE c.id = :commentId")
            .setParameter("commentId", commentId).getResultList().size());

        //verify delete activity from DB.
        assertEquals(1, getEntityManager().createQuery("FROM Activity WHERE id = :activityId")
            .setParameter("activityId", activityId).getResultList().size());

        compositeStreamActivityIdsDAO.execute(simthersCompStreamId, smithersId);
        List<Long> activityIds =
            cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + simthersCompStreamId);
        assertEquals(2, activityIds.size());

        compositeStreamActivityIdsDAO.execute(customCompStreamId, smithersId);
        List<Long> customActivityIds =
            cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + customCompStreamId);
        assertEquals(2, customActivityIds.size());

        starredActivityIdDAO.execute(mrburnsId);
        List<Long> starredActivityIds =
            cache.getList(CacheKeys.STARRED_BY_PERSON_ID + mrburnsId);
        assertEquals(1, starredActivityIds.size());

        commentIdsByActivityIdDAO.execute(activityId);

        ActivityDTO activity = activityByIdDAO.execute(new ArrayList() { { add(activityId); } }).get(0);

        cache.set(CacheKeys.ACTIVITY_BY_ID + activityId, activity);
        cache.set(CacheKeys.COMMENT_BY_ID + 1, new CommentDTO());
        cache.set(CacheKeys.COMMENT_BY_ID + 3, new CommentDTO());
    }


}
