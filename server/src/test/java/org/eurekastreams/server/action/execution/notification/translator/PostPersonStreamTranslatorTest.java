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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.ActivityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.domain.PropertyMapTestHelper;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the stream post notification translator.
 */
public class PostPersonStreamTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long STREAM_OWNER_ID = 2222L;

    /** Test data. */
    private static final long ACTIVITY_ID = 3333L;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** DAO to get list of followers of a stream. */
    private final DomainMapper<Long, List<Long>> followersDAO = context.mock(DomainMapper.class, "followersDAO");

    /** SUT. */
    private NotificationTranslator<ActivityNotificationsRequest> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new PostPersonStreamTranslator(followersDAO);
    }

    /**
     * Test creating the notification for the event of posting to a personal stream.
     */
    @Test
    public void testTranslatePersonalStreamPost()
    {
        context.checking(new Expectations()
        {
            {
                allowing(followersDAO).execute(STREAM_OWNER_ID);
                will(returnValue(new ArrayList<Long>(Arrays.asList(ACTOR_ID, STREAM_OWNER_ID, 5L, 6L, 7L))));
            }
        });

        NotificationBatch results = sut.translate(new ActivityNotificationsRequest(null, ACTOR_ID, STREAM_OWNER_ID,
                ACTIVITY_ID));

        // check recipients
        assertEquals(2, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.POST_TO_PERSONAL_STREAM, STREAM_OWNER_ID);
        TranslatorTestHelper.assertRecipients(results, NotificationType.POST_TO_FOLLOWED_STREAM, 5L, 6L, 7L);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        assertEquals(5, props.size());
        PropertyMapTestHelper.assertPlaceholder(props, "actor", PersonModelView.class, ACTOR_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "stream", PersonModelView.class, STREAM_OWNER_ID);
        PropertyMapTestHelper.assertPlaceholder(props, "activity", ActivityDTO.class, ACTIVITY_ID);
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.URL, "#activity/" + ACTIVITY_ID);
    }

    /**
     * Test that the notification is not sent for a person posting to his own personal stream.
     */
    @Test
    public void testTranslateOwnPersonalStreamPost()
    {
        context.checking(new Expectations()
        {
            {
                allowing(followersDAO).execute(ACTOR_ID);
                will(returnValue(new ArrayList<Long>()));
            }
        });

        NotificationBatch results = sut.translate(new ActivityNotificationsRequest(null, ACTOR_ID, ACTOR_ID,
                ACTIVITY_ID));

        assertNull(results);
    }
}
