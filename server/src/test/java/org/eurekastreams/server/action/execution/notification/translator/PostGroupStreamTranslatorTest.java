/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.ActivityNotificationsRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the group stream post notification translator.
 */
@SuppressWarnings("unchecked")
public class PostGroupStreamTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_ID = 1L;

    /** Test data. */
    private static final long COORDINATOR1_ID = 42;

    /** Test data. */
    private static final long COORDINATOR2_ID = 43;

    /** Test data. */
    private static final long SUBSCRIBER1_ID = 111;

    /** Test data. */
    private static final long SUBSCRIBER2_ID = 98;

    /** Test data. */
    private static final long ACTIVITY_ID = 3333L;

    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: Mapper to get list of subscribers of a group. */
    private final DomainMapper<Long, List<Long>> subscribersAllDAO = context.mock(DomainMapper.class,
            "subscribersAllDAO");

    /** Fixture: Mapper to get list of subscribers of a group. */
    private final DomainMapper<Long, List<Long>> subscribersUnrestrictedDAO = context.mock(DomainMapper.class,
            "subscribersUnrestrictedDAO");

    /** Fixture: Mapper to get list of coordinators of a group. */
    private final DomainMapper<Long, List<Long>> coordinatorsDAO = context.mock(DomainMapper.class, "coordinatorsDAO");

    /** Test list of coordinators. */
    private final List<Long> coordinators = Arrays.asList(COORDINATOR1_ID, COORDINATOR2_ID);

    /** Test list of subscribers. */
    private final List<Long> subscribersAll = Arrays.asList(SUBSCRIBER1_ID, SUBSCRIBER2_ID);

    /** Test list of subscribers. */
    private final List<Long> subscribersUnrestricted = Arrays.asList(SUBSCRIBER1_ID, ACTOR_ID, SUBSCRIBER2_ID);

    /** Test group. */
    private final DomainGroup group = context.mock(DomainGroup.class);

    /** System under test. */
    private NotificationTranslator<ActivityNotificationsRequest> sut;

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new PostGroupStreamTranslator(coordinatorsDAO, subscribersAllDAO, subscribersUnrestrictedDAO);
    }

    /**
     * Test creating the notification for the event of posting to a group stream.
     */
    @Test
    public void testTranslateGroupStreamPostNonCoord()
    {
        final List<Long> subscribers = new ArrayList<Long>(subscribersUnrestricted);
        subscribers.add(ACTOR_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(coordinatorsDAO).execute(GROUP_ID);
                will(returnValue(coordinators));
                oneOf(subscribersUnrestrictedDAO).execute(GROUP_ID);
                will(returnValue(subscribers));
            }
        });

        NotificationBatch results = sut.translate(new ActivityNotificationsRequest(null, ACTOR_ID, GROUP_ID,
                ACTIVITY_ID));

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.POST_TO_FOLLOWED_STREAM, subscribers);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(5, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "stream", DomainGroupModelView.class, GROUP_ID);
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertPlaceholder(props, "activity", ActivityDTO.class, ACTIVITY_ID);
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.URL, "#activity/" + ACTIVITY_ID);
    }

    /**
     * Test creating the notification for the event of posting to a group stream.
     */
    @Test
    public void testTranslateGroupStreamPostCoord()
    {
        final List<Long> subscribers = new ArrayList<Long>(subscribersAll);
        subscribers.add(COORDINATOR2_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(coordinatorsDAO).execute(GROUP_ID);
                will(returnValue(coordinators));
                oneOf(subscribersAllDAO).execute(GROUP_ID);
                will(returnValue(subscribers));
            }
        });

        NotificationBatch results = sut.translate(new ActivityNotificationsRequest(null, COORDINATOR2_ID, GROUP_ID,
                ACTIVITY_ID));

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.POST_TO_FOLLOWED_STREAM, subscribers);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(5, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, COORDINATOR2_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "stream", DomainGroupModelView.class, GROUP_ID);
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertPlaceholder(props, "activity", ActivityDTO.class, ACTIVITY_ID);
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.URL, "#activity/" + ACTIVITY_ID);
    }

    /**
     * Test creating the notification for the event of posting to a group stream when there are no members or none with
     * notifications enabled.
     */
    @Test
    public void testTranslateGroupStreamPostNoMembers()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(coordinatorsDAO).execute(GROUP_ID);
                will(returnValue(coordinators));
                oneOf(subscribersUnrestrictedDAO).execute(GROUP_ID);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        NotificationBatch results = sut.translate(new ActivityNotificationsRequest(null, ACTOR_ID, GROUP_ID,
                ACTIVITY_ID));

        context.assertIsSatisfied();
        assertNull(results);
    }
}
