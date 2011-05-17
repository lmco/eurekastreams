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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.idle.NotificationDTO;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the group access request notification translator.
 * 
 */
public class RequestGroupAccessTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_ID = 102L;

    /** Test data. */
    private static final long COORDINATOR1_ID = 42;

    /** Test data. */
    private static final long COORDINATOR2_ID = ACTOR_ID;

    /** Test data. */
    private static final long COORDINATOR3_ID = 98;

    /** System under test. */
    private RequestGroupAccessTranslator sut;

    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock coordinator mapper. */
    private DomainMapper<Long, List<Long>> mapper = context.mock(DomainMapper.class);

    /**
     * Setup test.
     */
    @Before
    public void setup()
    {
        sut = new RequestGroupAccessTranslator(mapper);
    }

    /**
     * Test creating the notification for the event of following a person.
     */
    @Test
    public void testTranslate()
    {
        final List<Long> coordinators = new ArrayList<Long>();
        coordinators.add(COORDINATOR1_ID);
        coordinators.add(COORDINATOR3_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(GROUP_ID);
                will(returnValue(coordinators));
            }
        });

        Collection<NotificationDTO> notifs = sut.translate(ACTOR_ID, GROUP_ID, 0L);

        context.assertIsSatisfied();

        Assert.assertNotNull(notifs);
        Assert.assertEquals(1, notifs.size());
        NotificationDTO notif = notifs.iterator().next();

        Assert.assertEquals(NotificationType.REQUEST_GROUP_ACCESS, notif.getType());
        Assert.assertEquals(GROUP_ID, notif.getDestinationId());
        Assert.assertEquals(EntityType.GROUP, notif.getDestinationType());
        Assert.assertEquals(ACTOR_ID, notif.getActorId());
        Assert.assertEquals(0L, notif.getActivityId());

        Assert.assertEquals(2, notif.getRecipientIds().size());
        assertTrue(Matchers.hasItems(COORDINATOR1_ID, COORDINATOR3_ID).matches(notif.getRecipientIds()));
    }

}
