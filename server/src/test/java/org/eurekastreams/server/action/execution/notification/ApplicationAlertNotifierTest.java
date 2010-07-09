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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.SyncUnreadApplicationAlertCountCacheByUserId;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the application alert notifier.
 */
public class ApplicationAlertNotifierTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    private ApplicationAlertNotifier sut;

    /** Insert mapper. */
    @Autowired
    InsertMapper<ApplicationAlertNotification> insertMapper;

    /** Sync mapper. */
    @Autowired
    SyncUnreadApplicationAlertCountCacheByUserId syncMapper;

    /**
     * Setup the sut.
     */
    @Before
    public void setup()
    {
        syncMapper.setCache(getCache());
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

        assertTrue(getCache().get(CacheKeys.UNREAD_APPLICATION_ALERT_COUNT_BY_USER + recipientId) == null);

        NotificationDTO commentNotification =
                new NotificationDTO(Collections.singletonList(recipientId),
                NotificationType.COMMENT_TO_COMMENTED_POST, actorId, destinationId, EntityType.PERSON, activityId);
        sut.notify(commentNotification);

        NotificationDTO followNotification =
                new NotificationDTO(Collections.singletonList(recipientId),
                NotificationType.FOLLOW_PERSON, actorId, 0, EntityType.PERSON, 0);
        UserActionRequest currentAsyncRequest = sut.notify(followNotification);
        List<ApplicationAlertNotification> alerts = getEntityManager().createQuery(
                "from ApplicationAlertNotification a WHERE recipient.id=:id").setParameter("id", recipientId)
                .getResultList();

        assertEquals(2, alerts.size());
        assertEquals(2, getCache().get(CacheKeys.UNREAD_APPLICATION_ALERT_COUNT_BY_USER + recipientId));
        assertTrue(NotificationType.COMMENT_TO_COMMENTED_POST == alerts.get(0).getNotificiationType());
        assertTrue(NotificationType.FOLLOW_PERSON == alerts.get(1).getNotificiationType());
        assertNull(currentAsyncRequest);
    }
}
