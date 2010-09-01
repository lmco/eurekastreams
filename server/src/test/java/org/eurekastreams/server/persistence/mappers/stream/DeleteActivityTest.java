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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.DeleteActivityRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for DeleteActivity class.
 *
 */
public class DeleteActivityTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteActivity sut;

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
     * Everyone compositeStreamId.
     */
    private final long everyoneCompStreamId = 5002L;

    /**
     * additional compositeStreamId contiaining destination stream.
     */
    private final long customCompStreamId = 17L;

    /**
     * Smithers user id.
     */
    private final long smithersId = 98L;

    /**
     * Test the execute method with bogus id.
     */
    @Test
    public void testExecuteBogusId()
    {
        final long bogusId = -9879843;
        assertNull(sut.execute(new DeleteActivityRequest(smithersId, bogusId)));
    }

    /**
     * Test execute removing first comment.
     */
    @Test
    public void testExecute()
    {
        //verify initial state.
        loadAndVerifyInitialActivity();

        //delete a activity via sut.
        assertNotNull(sut.execute(new DeleteActivityRequest(smithersId, activityId)));

        //verify delete activity comments from DB.
        assertEquals(0, getEntityManager().createQuery("FROM Comment c WHERE c.id = :commentId")
            .setParameter("commentId", commentId).getResultList().size());

        //verify delete activity from DB.
        assertEquals(0, getEntityManager().createQuery("FROM Activity WHERE id = :activityId")
            .setParameter("activityId", activityId).getResultList().size());

        //verify it was removed from activity destination CompositeStream.
        List<Long> activityIds =
            cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + simthersCompStreamId);
        assertEquals(1, activityIds.size());

        //verify it was removed from everyone CompositeStream.
        List<Long> everyoneActivityIds =
            cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + everyoneCompStreamId);
        assertEquals(4, everyoneActivityIds.size());

        //verify it was removed from custom CompositeStream.
        List<Long> customActivityIds =
            cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + customCompStreamId);
        assertEquals(1, customActivityIds.size());

    }

    /**
    * Hit the mapper to load activity into cache and verify state.
    */
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

       compositeStreamActivityIdsDAO.execute(everyoneCompStreamId, smithersId);
       List<Long> everyoneActivityIds = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + everyoneCompStreamId);
       assertEquals(5, everyoneActivityIds.size());

       compositeStreamActivityIdsDAO.execute(customCompStreamId, smithersId);
       List<Long> customActivityIds =
           cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + customCompStreamId);
       assertEquals(2, customActivityIds.size());
   }

}
