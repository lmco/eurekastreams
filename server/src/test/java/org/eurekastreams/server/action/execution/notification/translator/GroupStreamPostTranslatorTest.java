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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
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
@SuppressWarnings("unchecked")
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
    private static final long MEMBER1_ID = 111;

    /** Test data. */
    private static final long MEMBER2_ID = 98;

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

    /** Fixture: Mapper to get list of members of a group. */
    private DomainMapper<Long, List<Long>> memberMapper = context.mock(DomainMapper.class);

    /** System under test. */
    private GroupStreamPostTranslator sut;

    /** Test list of coordinators. */
    private List<Long> coordinators = Arrays.asList(COORDINATOR1_ID, COORDINATOR2_ID);

    /** Test list of members. */
    private List<Long> members = Arrays.asList(MEMBER1_ID, MEMBER2_ID);


    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new GroupStreamPostTranslator(mapper, memberMapper);
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
                oneOf(memberMapper).execute(GROUP_ID);
                will(returnValue(members));
            }
        });

        Collection<NotificationDTO> notifs = sut.translate(ACTOR_ID, GROUP_ID, ACTIVITY_ID);

        context.assertIsSatisfied();

        Assert.assertNotNull(notifs);
        Assert.assertEquals(2, notifs.size());

        Iterator<NotificationDTO> iter = notifs.iterator();

        NotificationDTO notif1 = iter.next();
        Assert.assertEquals(ACTIVITY_ID, notif1.getActivityId());
        Assert.assertEquals(NotificationType.POST_TO_GROUP_STREAM, notif1.getType());
        Assert.assertEquals(GROUP_ID, notif1.getDestinationId());
        Assert.assertEquals(EntityType.GROUP, notif1.getDestinationType());
        Assert.assertEquals(coordinators, notif1.getRecipientIds());
        Assert.assertEquals(ACTOR_ID, notif1.getActorId());

        NotificationDTO notif2 = iter.next();
        Assert.assertEquals(ACTIVITY_ID, notif2.getActivityId());
        Assert.assertEquals(NotificationType.POST_TO_JOINED_GROUP, notif2.getType());
        Assert.assertEquals(GROUP_ID, notif2.getDestinationId());
        Assert.assertEquals(EntityType.GROUP, notif2.getDestinationType());
        Assert.assertArrayEquals(new Object[] { MEMBER1_ID }, notif2.getRecipientIds().toArray());
        Assert.assertEquals(ACTOR_ID, notif2.getActorId());
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
                oneOf(memberMapper).execute(GROUP_ID);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        Collection<NotificationDTO> notifs = sut.translate(COORDINATOR1_ID, GROUP_ID, ACTIVITY_ID);
        Assert.assertEquals(1, notifs.size());

        NotificationDTO notif = notifs.iterator().next();
        Assert.assertEquals(COORDINATOR2_ID, (long) notif.getRecipientIds().get(0));

        context.assertIsSatisfied();
    }

    /**
     * Test that the notification is not sent for a member posting to his own group stream.
     */
    @Test
    public void testTranslateOwnGroupStreamPostMember()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(GROUP_ID);
                will(returnValue(Collections.EMPTY_LIST));
                oneOf(memberMapper).execute(GROUP_ID);
                will(returnValue(members));
            }
        });

        Collection<NotificationDTO> notifs = sut.translate(MEMBER1_ID, GROUP_ID, ACTIVITY_ID);
        Assert.assertEquals(1, notifs.size());

        NotificationDTO notif = notifs.iterator().next();
        Assert.assertEquals(1, notif.getRecipientIds().size());
        Assert.assertEquals(MEMBER2_ID, (long) notif.getRecipientIds().get(0));

        context.assertIsSatisfied();
    }

}
