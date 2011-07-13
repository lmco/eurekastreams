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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.requests.InsertActivityCommentRequest;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.hibernate.validator.InvalidStateException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for InsertActivityComment class.
 * 
 */
public class InsertActivityCommentTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private InsertActivityComment sut;

    /**
     * Mapper for looking up activities by id.
     */
    @Autowired
    private BulkActivitiesDbMapper activityMapper;

    /**
     * A comment that is 1000 characters.
     */
    String is1000 = "this comment is 250 characters this comment is 250 characters this comment is 250 "
            + "characters this comment is 250 characters this comment is 250 characters this comment is "
            + "250 characters this comment is 250 characters this comment is 250 characters th"
            + "this comment is 250 characters this comment is 250 characters this comment is 250 "
            + "characters this comment is 250 characters this comment is 250 characters this comment is "
            + "250 characters this comment is 250 characters this comment is 250 characters th"
            + "this comment is 250 characters this comment is 250 characters this comment is 250 "
            + "characters this comment is 250 characters this comment is 250 characters this comment is "
            + "250 characters this comment is 250 characters this comment is 250 characters th"
            + "this comment is 250 characters this comment is 250 characters this comment is 250 "
            + "characters this comment is 250 characters this comment is 250 characters this comment is "
            + "250 characters this comment is 250 characters this comment is 250 characters th";

    /**
     * A comment over 1000 charachters.
     */
    String over1000 = "this comment is over 250 characters this comment is over 250 characters this comment "
            + "is over 250 characters this comment is over 250 characters this comment is over 250 "
            + "characters this comment is over 250 characters this comment is over 250 characters"
            + "this comment is over 250 characters this comment is over 250 characters this comment "
            + "is over 250 characters this comment is over 250 characters this comment is over 250 "
            + "characters this comment is over 250 characters this comment is over 250 characters"
            + "this comment is over 250 characters this comment is over 250 characters this comment "
            + "is over 250 characters this comment is over 250 characters this comment is over 250 "
            + "characters this comment is over 250 characters this comment is over 250 characters"
            + "this comment is over 250 characters this comment is over 250 characters this comment "
            + "is over 250 characters this comment is over 250 characters this comment is over 250 "
            + "characters this comment is over 250 characters this comment is over 250 characters";

    /**
     * Cache.
     */
    @Autowired
    Cache memcachedCache;

    /**
     * test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        final long activityId = 6790L;
        final long smithersId = 98L;

        // get person
        Person smithers = (Person) getEntityManager().createQuery("FROM Person where id = :smithersId").setParameter(
                "smithersId", smithersId).getSingleResult();

        assertEquals(smithersId, smithers.getId());

        // Get the activityDTO for activity that will be commented on.
        // and assert initial state is correct.
        List<Long> activityIds = new ArrayList<Long>(1);
        activityIds.add(activityId);
        ActivityDTO activityDTO = activityMapper.execute(activityIds).get(0);

        assertEquals(activityId, activityDTO.getId());
        assertEquals(3, activityDTO.getCommentCount());
        assertEquals(5L, activityDTO.getFirstComment().getId());
        assertEquals(7, activityDTO.getLastComment().getId());

        // Create the insertRequest and insert the comment via sut.
        InsertActivityCommentRequest insertRequest = new InsertActivityCommentRequest(smithersId, activityId, is1000);
        CommentDTO result = sut.execute(insertRequest);

        // Grab entity id for later use
        long newCommentId = result.getId();

        // flush and clear entityManager before query the DB.
        getEntityManager().flush();
        getEntityManager().clear();

        // Query comment from DB to assert that record was inserted in.
        Comment comment = (Comment) getEntityManager().createQuery("FROM Comment where id = :commentId").setParameter(
                "commentId", newCommentId).getSingleResult();

        // verify correct values were inserted into DB.
        assertNotNull(comment);
        assertEquals(smithersId, comment.getAuthor().getId());
        assertEquals(activityId, comment.getTarget().getId());
        assertEquals(is1000, comment.getBody());

        // Get the activity DTO and assert is was updated correctly by sut.
        activityDTO = activityMapper.execute(activityIds).get(0);
        assertEquals(activityId, activityDTO.getId());
        assertEquals(4, activityDTO.getCommentCount());
        assertEquals(5L, activityDTO.getFirstComment().getId());
        assertEquals(newCommentId, activityDTO.getLastComment().getId());

        // Insert another comment to make sure first/last comment works.
        insertRequest.setContent("another comment");
        result = sut.execute(insertRequest);

        // Get the activity DTO and assert is was updated correctly.
        activityDTO = activityMapper.execute(activityIds).get(0);
        assertEquals(5L, activityDTO.getFirstComment().getId());
        assertEquals(result.getId(), activityDTO.getLastComment().getId());
    }

    /**
     * test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = InvalidStateException.class)
    public void testExecuteCommentT0oBig()
    {
        final long activityId = 6790L;
        final long smithersId = 98L;

        // get person
        Person smithers = (Person) getEntityManager().createQuery("FROM Person where id = :smithersId").setParameter(
                "smithersId", smithersId).getSingleResult();

        assertEquals(smithersId, smithers.getId());

        // Get the activityDTO for activity that will be commented on.
        // and assert initial state is correct.
        List<Long> activityIds = new ArrayList<Long>(1);
        activityIds.add(activityId);
        ActivityDTO activityDTO = activityMapper.execute(activityIds).get(0);

        assertEquals(activityId, activityDTO.getId());
        assertEquals(3, activityDTO.getCommentCount());
        assertEquals(5L, activityDTO.getFirstComment().getId());
        assertEquals(7, activityDTO.getLastComment().getId());

        // Create the insertRequest and insert the comment via sut.
        InsertActivityCommentRequest insertRequest = new InsertActivityCommentRequest(smithersId, activityId, over1000);

        sut.execute(insertRequest);
    }

}
