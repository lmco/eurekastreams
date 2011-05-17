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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.notification.idle.ApplicationAlertNotifier;
import org.eurekastreams.server.action.execution.notification.idle.NotificationDTO;
import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the application alert notifier.
 */
public class ApplicationAlertNotifierTest extends CachedMapperTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** System under test. */
    private ApplicationAlertNotifier sut;

    /** Insert mapper. */
    @Autowired
    InsertMapper<ApplicationAlertNotification> insertMapper;

    /** Sync mapper. */
    private final DomainMapper<Long, Integer> syncMapper = context.mock(DomainMapper.class, "syncMapper");

    /**
     * Setup the sut.
     */
    @Before
    public void setup()
    {
        sut = new ApplicationAlertNotifier(insertMapper, syncMapper);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Tests the notify method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNotify()
    {
        final long recipientId = 42;
        final long actorId = 99;
        final long destinationId = 98;
        final long activityId = 6789;

        context.checking(new Expectations()
        {
            {
                exactly(2).of(syncMapper).execute(recipientId);
            }
        });

        NotificationDTO commentNotification = new NotificationDTO(Collections.singletonList(recipientId),
                NotificationType.COMMENT_TO_COMMENTED_POST, actorId, destinationId, EntityType.PERSON, activityId);
        UserActionRequest asyncRequest1 = sut.notify(commentNotification);

        NotificationDTO followNotification = new NotificationDTO(Collections.singletonList(recipientId),
                NotificationType.FOLLOW_PERSON, actorId, 0, EntityType.PERSON, 0);
        UserActionRequest asyncRequest2 = sut.notify(followNotification);

        context.assertIsSatisfied();

        assertNull(asyncRequest1);
        assertNull(asyncRequest2);

        List<ApplicationAlertNotification> alerts = getEntityManager()
                .createQuery("from ApplicationAlertNotification a WHERE recipient.id=:id")
                .setParameter("id", recipientId).getResultList();
        assertEquals(2, alerts.size());
        assertEquals(NotificationType.COMMENT_TO_COMMENTED_POST, alerts.get(0).getNotificiationType());
        assertEquals(NotificationType.FOLLOW_PERSON, alerts.get(1).getNotificiationType());
    }
}
