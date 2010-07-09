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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.stream.GetCoordinatorIdsByGroupId;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the group stream post notification translator.
 */
public class GroupStreamPostTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_ID = 1L;

    /** Test data. */
    private static final long COORDINATOR1_ID = 42;

    /** Test data. */
    private static final long COORDINATOR2_ID = 98;

    /** Test data. */
    private static final long ACTIVITY_ID = 3333L;

    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock coordinator mapper. */
    private GetCoordinatorIdsByGroupId mapper = context.mock(GetCoordinatorIdsByGroupId.class);

    /** System under test. */
    private GroupStreamPostTranslator sut;

    /** Test list of coordinators. */
    private List<Long> coordinators;

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new GroupStreamPostTranslator(mapper);
        coordinators = new ArrayList<Long>();
        coordinators.add(COORDINATOR1_ID);
        coordinators.add(COORDINATOR2_ID);
    }

    /**
     * Test creating the notification for the event of posting to a group stream.
     */
    @Test
    public void testTranslateGroupStreamPost()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(GROUP_ID);
                will(returnValue(coordinators));
            }
        });

        Collection<NotificationDTO> notifs = sut.translate(ACTOR_ID, GROUP_ID, ACTIVITY_ID);

        Assert.assertNotNull(notifs);
        Assert.assertEquals(1, notifs.size());
        NotificationDTO notif = notifs.iterator().next();
        NotificationDTO expected = new NotificationDTO(coordinators, NotificationType.POST_TO_GROUP_STREAM, ACTOR_ID,
                GROUP_ID, EntityType.GROUP, ACTIVITY_ID);
        Assert.assertEquals(expected.getActivityId(), notif.getActivityId());
        Assert.assertEquals(expected.getType(), notif.getType());
        Assert.assertEquals(expected.getDestinationId(), notif.getDestinationId());
        Assert.assertEquals(expected.getRecipientIds(), notif.getRecipientIds());
        
        context.assertIsSatisfied();
    }

    /**
     * Test that the notification is not sent for a coordinator posting to his own group stream.
     */
    @Test
    public void testTranslateOwnGroupStreamPost()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(GROUP_ID);
                will(returnValue(coordinators));
            }
        });

        Collection<NotificationDTO> notifs = sut.translate(COORDINATOR1_ID, GROUP_ID, ACTIVITY_ID);
        Assert.assertEquals(1, notifs.size());

        NotificationDTO notif = notifs.iterator().next();
        Assert.assertEquals(COORDINATOR2_ID, (long) notif.getRecipientIds().get(0));
        
        context.assertIsSatisfied();
    }
}
