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

import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Like translator test.
 */
public class LikeTranslatorTest
{
    /** Test data. */
    private static final Long AUTHOR_ID = 81L;
    /** Test data. */
    private static final Long ORIGINAL_AUTHOR_ID = 84L;
    /** Test data. */
    private static final Long ACTIVITY_STREAM_ID = 82L;
    /** Test data. */
    private static final Long ACTIVITY_ID = 83L;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: For getting activity info. */
    private final DomainMapper<List<Long>, List<ActivityDTO>> activityMapper = context.mock(DomainMapper.class,
            "activityMapper");

    /** Fixture: activity. */
    private final ActivityDTO activity = context.mock(ActivityDTO.class);
    /** Fixture: author. */
    private final StreamEntityDTO author = context.mock(StreamEntityDTO.class, "author");
    /** Fixture: original author. */
    private final StreamEntityDTO originalAuthor = context.mock(StreamEntityDTO.class, "originalAuthor");

    /** SUT. */
    LikeTranslator sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new LikeTranslator(activityMapper);

        final StreamEntityDTO destination = context.mock(StreamEntityDTO.class, "destination");

        context.checking(new Expectations()
        {
            {
                allowing(activityMapper).execute(Collections.singletonList(ACTIVITY_ID));
                will(returnValue(Collections.singletonList(activity)));
                allowing(activity).getDestinationStream();
                will(returnValue(destination));

                allowing(destination).getDestinationEntityId();
                will(returnValue(ACTIVITY_STREAM_ID));
                allowing(destination).getType();
                will(returnValue(EntityType.GROUP));

                allowing(activity).getActor();
                will(returnValue(author));
                allowing(author).getId();
                will(returnValue(AUTHOR_ID));

                allowing(originalAuthor).getId();
                will(returnValue(ORIGINAL_AUTHOR_ID));
            }
        });
    }

    /**
     * Translate.
     */
    @Test
    public void testTranslateNotShared()
    {
        context.checking(new Expectations()
        {
            {
                allowing(author).getType();
                will(returnValue(EntityType.PERSON));
                allowing(activity).getOriginalActor();
                will(returnValue(null));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(null, 1L, 0L, ACTIVITY_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.LIKE_ACTIVITY, AUTHOR_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(3, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, 1L);
        PropertyMapTestHelper.assertValue(props, "stream", activity.getDestinationStream());
        PropertyMapTestHelper.assertValue(props, "activity", activity);
    }

    /**
     * Translate; actor is author (likes own activity).
     */
    @Test
    public void testTranslateNotSharedActorIsAuthor()
    {
        context.checking(new Expectations()
        {
            {
                allowing(author).getType();
                will(returnValue(EntityType.PERSON));
                allowing(activity).getOriginalActor();
                will(returnValue(null));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(null, AUTHOR_ID, 0L, ACTIVITY_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();
        assertNull(results);
    }

    /**
     * Translate.
     */
    @Test
    public void testTranslateAuthorIsGroup()
    {
        context.checking(new Expectations()
        {
            {
                allowing(author).getType();
                will(returnValue(EntityType.GROUP));
                allowing(activity).getOriginalActor();
                will(returnValue(null));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(null, 1L, 0L, ACTIVITY_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();
        assertNull(results);
    }

    /**
     * Translate.
     */
    @Test
    public void testTranslateShared()
    {
        context.checking(new Expectations()
        {
            {
                allowing(author).getType();
                will(returnValue(EntityType.PERSON));
                allowing(activity).getOriginalActor();
                will(returnValue(originalAuthor));
                allowing(originalAuthor).getType();
                will(returnValue(EntityType.PERSON));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(null, 1L, 0L, ACTIVITY_ID);
        NotificationBatch results = sut.translate(request);

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.LIKE_ACTIVITY, AUTHOR_ID, ORIGINAL_AUTHOR_ID);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(3, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, 1L);
        PropertyMapTestHelper.assertValue(props, "stream", activity.getDestinationStream());
        PropertyMapTestHelper.assertValue(props, "activity", activity);
    }
}
