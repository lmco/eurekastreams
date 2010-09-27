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
package org.eurekastreams.server.action.execution.notification.translator;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetCommentorIdsByActivityId;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the comment notification translator.
 */
public class CommentTranslatorTest
{
    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock commentors mapper. */
    private GetCommentorIdsByActivityId commentorsMapper = context.mock(GetCommentorIdsByActivityId.class);

    /** Mock activities mapper. */
    private BulkActivitiesMapper activitiesMapper = context.mock(BulkActivitiesMapper.class);

    /** Mapper to get the comment. */
    private DomainMapper<List<Long>, List<CommentDTO>> commentsMapper = context.mock(DomainMapper.class);

    /** System under test. */
    private CommentTranslator sut;

    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long STREAM_OWNER_ID = 2222L;

    /** Test data. */
    private static final long DESTINATION_ID = 3333L;

    /** Test data. */
    private static final long ACTIVITY_ID = 4444L;

    /** Test data. */
    private static final long COMMENT_ID = 4545L;

    /** Test data. */
    private static final long COMMENTOR = 5555L;

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new CommentTranslator(commentorsMapper, activitiesMapper, commentsMapper);
    }

    /**
     * Test the translator.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTranslate()
    {
        final StreamEntityDTO actor = new StreamEntityDTO();
        actor.setId(STREAM_OWNER_ID);

        final ActivityDTO activity = new ActivityDTO();
        activity.setActor(actor);

        final CommentDTO comment = new CommentDTO();
        comment.setActivityId(ACTIVITY_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(commentsMapper).execute(Collections.singletonList(COMMENT_ID));
                will(returnValue(Collections.singletonList(comment)));

                oneOf(activitiesMapper).execute(ACTIVITY_ID, null);
                will(returnValue(activity));

                oneOf(commentorsMapper).execute(ACTIVITY_ID);
                will(returnValue(Collections.singletonList(COMMENTOR)));
            }
        });

        Collection<NotificationDTO> results = sut.translate(ACTOR_ID, DESTINATION_ID, COMMENT_ID);

        context.assertIsSatisfied();
        assertEquals(3, results.size());
    }
}
