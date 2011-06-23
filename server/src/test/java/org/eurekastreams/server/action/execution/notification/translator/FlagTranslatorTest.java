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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.ActivityNotificationsRequest;
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
 * Tests the flagged activty event translator.
 */
// TODO Make this a an integration test.
public class FlagTranslatorTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: activity DAO. */
    private final DomainMapper<Long, ActivityDTO> activityDAO = context.mock(DomainMapper.class, "activityDAO");

    /** Fixture: mapper. */
    private final DomainMapper<Serializable, List<Long>> systemAdminIdsMapper = context.mock(DomainMapper.class,
            "systemAdminIdsMapper");

    /** SUT. */
    private NotificationTranslator<ActivityNotificationsRequest> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new FlagTranslator(activityDAO, systemAdminIdsMapper);
    }

    /**
     * Tests translating.
     */
    @Test
    public void testTranslate()
    {
        StreamEntityDTO stream = new StreamEntityDTO();
        stream.setType(EntityType.PERSON);
        stream.setDestinationEntityId(4L);
        final ActivityDTO activity = new ActivityDTO();
        activity.setDestinationStream(stream);

        final List<Long> admins = Arrays.asList(7L, 5L);
        context.checking(new Expectations()
        {
            {
                allowing(activityDAO).execute(3L);
                will(returnValue(activity));
                allowing(systemAdminIdsMapper).execute(null);
                will(returnValue(admins));
            }
        });

        NotificationBatch results = sut.translate(new ActivityNotificationsRequest(null, 1L, 0L, 3L));

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.FLAG_ACTIVITY, admins);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        PropertyMapTestHelper.assertPlaceholder(props, NotificationPropertyKeys.ACTOR, PersonModelView.class, 1L);
        PropertyMapTestHelper.assertValue(props, "stream", activity.getDestinationStream());
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertValue(props, "activity", activity);
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.HIGH_PRIORITY, true);
    }

    /**
     * Tests translating.
     */
    @Test
    public void testTranslateGroup()
    {
        StreamEntityDTO stream = new StreamEntityDTO();
        stream.setType(EntityType.GROUP);
        stream.setDestinationEntityId(4L);
        final ActivityDTO activity = new ActivityDTO();
        activity.setDestinationStream(stream);

        final List<Long> admins = Arrays.asList(7L, 5L);
        context.checking(new Expectations()
        {
            {
                allowing(activityDAO).execute(3L);
                will(returnValue(activity));
                allowing(systemAdminIdsMapper).execute(null);
                will(returnValue(admins));
            }
        });

        NotificationBatch results = sut.translate(new ActivityNotificationsRequest(null, 1L, 0L, 3L));

        context.assertIsSatisfied();

        // check recipients
        assertEquals(1, results.getRecipients().size());
        TranslatorTestHelper.assertRecipients(results, NotificationType.FLAG_ACTIVITY, admins);

        // check properties
        PropertyMap<Object> props = results.getProperties();
        PropertyMapTestHelper.assertPlaceholder(props, NotificationPropertyKeys.ACTOR, PersonModelView.class, 1L);
        PropertyMapTestHelper.assertValue(props, "stream", activity.getDestinationStream());
        PropertyMapTestHelper.assertAlias(props, "source", "stream");
        PropertyMapTestHelper.assertValue(props, "activity", activity);
        PropertyMapTestHelper.assertValue(props, NotificationPropertyKeys.HIGH_PRIORITY, true);
    }
}
