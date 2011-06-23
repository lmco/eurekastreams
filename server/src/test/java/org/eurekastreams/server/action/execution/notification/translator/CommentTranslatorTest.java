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
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CommentNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
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

    /** Mock commentors DAO. */
    private final DomainMapper<Long, List<Long>> commentorsMapper = context.mock(DomainMapper.class,
            "commentorsMapper");

    /** Fixture: activity DAO. */
    private final DomainMapper<Long, ActivityDTO> activityDAO = context.mock(DomainMapper.class, "activityDAO");

    /** System under test. */
    private NotificationTranslator<CommentNotificationsRequest> sut;

    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long AUTHOR_ID = 1112L;

    /** Test data. */
    private static final long STREAM_OWNER_ID = 2222L;

    /** Test data. */
    private static final long DESTINATION_ID = 3333L;

    /** Test data. */
    private static final long ACTIVITY_ID = 4444L;

    /** Test data. */
    private static final long COMMENT_ID = 4545L;

    /** Test data. */
    private static final long COMMENTOR1 = 1131L;

    /** Test data. */
    private static final long COMMENTOR2 = 1132L;

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new CommentTranslator(commentorsMapper, activityDAO);
    }

    /**
     * Test the translator.
     */
    @Test
    public void testTranslate()
    {
        final StreamEntityDTO actor = new StreamEntityDTO();
        actor.setId(AUTHOR_ID);

        final ActivityDTO activity = new ActivityDTO();
        activity.setActor(actor);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(ACTIVITY_ID);
                will(returnValue(activity));

                oneOf(commentorsMapper).execute(ACTIVITY_ID);
                will(returnValue(Arrays.asList(ACTOR_ID, AUTHOR_ID, STREAM_OWNER_ID, COMMENTOR1, COMMENTOR2)));
            }
        });

        CommentNotificationsRequest request = new CommentNotificationsRequest(null, ACTOR_ID, DESTINATION_ID,
                ACTIVITY_ID, COMMENT_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();

        // check recipients
        assertEquals(2, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.COMMENT_TO_PERSONAL_POST, AUTHOR_ID);
        TranslatorTestHelper.assertRecipients(results, NotificationType.COMMENT_TO_COMMENTED_POST, STREAM_OWNER_ID,
                COMMENTOR1, COMMENTOR2);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(6, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertValue(props, "stream", activity.getDestinationStream());
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertValue(props, "activity", activity);
        PropertyMapTestHelper.assertPlaceholder(props, "comment", CommentDTO.class, COMMENT_ID);
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.URL, "#activity/" + ACTIVITY_ID);
    }

    /**
     * Test the translator.
     */
    @Test
    public void testTranslateActorIsPostAuthor()
    {
        final StreamEntityDTO actor = new StreamEntityDTO();
        actor.setId(AUTHOR_ID);

        final ActivityDTO activity = new ActivityDTO();
        activity.setActor(actor);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(ACTIVITY_ID);
                will(returnValue(activity));

                oneOf(commentorsMapper).execute(ACTIVITY_ID);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        CommentNotificationsRequest request = new CommentNotificationsRequest(null, AUTHOR_ID, DESTINATION_ID,
                ACTIVITY_ID, COMMENT_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();

        // check recipients
        assertEquals(0, results.getRecipients().size());
    }

    /**
     * Test the translator.
     */
    @Test
    public void testTranslateActivityNotFound()
    {
        final CommentDTO comment = new CommentDTO();
        comment.setActivityId(ACTIVITY_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(ACTIVITY_ID);
                will(returnValue(null));
            }
        });

        CommentNotificationsRequest request = new CommentNotificationsRequest(null, ACTOR_ID, DESTINATION_ID,
                ACTIVITY_ID, COMMENT_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();
        assertNull(results);
    }
}
