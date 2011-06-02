/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CommentNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetCommentorIdsByActivityId;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
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
    private final GetCommentorIdsByActivityId commentorsMapper = context.mock(GetCommentorIdsByActivityId.class);

    /** Mock activities mapper. */
    private final DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper = context.mock(DomainMapper.class,
            "activitiesMapper");

    /** Mapper to get the comment. */
    private final DomainMapper<List<Long>, List<CommentDTO>> commentsMapper = context.mock(DomainMapper.class,
            "commentsMapper");

    /** Mapper to get the savers. */
    private final DomainMapper<Long, List<Long>> saversMapper = context.mock(DomainMapper.class, "saversMapper");

    /** System under test. */
    private NotificationTranslator sut;

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

    /** Test data. */
    private static final long SAVER = 7777L;

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new CommentTranslator(commentorsMapper, activitiesMapper, commentsMapper, saversMapper);
    }

    /**
     * Test the translator.
     */
    @Test
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

                oneOf(activitiesMapper).execute(Collections.singletonList(ACTIVITY_ID));
                will(returnValue(Collections.singletonList(activity)));

                oneOf(commentorsMapper).execute(ACTIVITY_ID);
                will(returnValue(Collections.singletonList(COMMENTOR)));

                oneOf(saversMapper).execute(ACTIVITY_ID);
                will(returnValue(Arrays.asList(ACTOR_ID, STREAM_OWNER_ID, COMMENTOR, SAVER)));
            }
        });

        CommentNotificationsRequest request = new CommentNotificationsRequest(null, ACTOR_ID, DESTINATION_ID,
                ACTIVITY_ID, COMMENT_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();

        // check recipients
        assertEquals(4, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.COMMENT_TO_SAVED_POST, SAVER);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(5, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertValue(props, "stream", activity.getDestinationStream());
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertValue(props, "activity", activity);
        PropertyMapTestHelper.assertValue(props, "comment", comment);
    }


    /**
     * Test the translator.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTranslateCommentNotFound()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(commentsMapper).execute(Collections.singletonList(COMMENT_ID));
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        CommentNotificationsRequest request = new CommentNotificationsRequest(null, ACTOR_ID, DESTINATION_ID,
                ACTIVITY_ID, COMMENT_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();
        assertNull(results);
    }

    /**
     * Test the translator.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTranslateActivityNotFound()
    {
        final CommentDTO comment = new CommentDTO();
        comment.setActivityId(ACTIVITY_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(commentsMapper).execute(Collections.singletonList(COMMENT_ID));
                will(returnValue(Collections.singletonList(comment)));

                oneOf(activitiesMapper).execute(Collections.singletonList(ACTIVITY_ID));
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        CommentNotificationsRequest request = new CommentNotificationsRequest(null, ACTOR_ID, DESTINATION_ID,
                ACTIVITY_ID, COMMENT_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();
        assertNull(results);
    }
}
