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

import static junit.framework.Assert.assertEquals;

import java.util.Collection;

import org.eurekastreams.server.action.execution.notification.idle.NotificationDTO;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests GroupMembershipResponseTranslator.
 */
public class GroupMembershipResponseTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long GROUP_ID = 2222L;

    /** Test data. */
    private static final long FOLLOWER_ID = 150L;

    /**
     * Test creating the notification for a group membership request approval/denial.
     */
    @Test
    public void testTranslate()
    {
        GroupMembershipResponseTranslator sut = new GroupMembershipResponseTranslator(
                NotificationType.REQUEST_GROUP_ACCESS_APPROVED);
        Collection<NotificationDTO> notifs = sut.translate(ACTOR_ID, GROUP_ID, FOLLOWER_ID);

        Assert.assertNotNull(notifs);
        Assert.assertEquals(1, notifs.size());
        NotificationDTO notif = notifs.iterator().next();

        assertEquals(ACTOR_ID, notif.getActorId());
        assertEquals(GROUP_ID, notif.getDestinationId());
        assertEquals(EntityType.GROUP, notif.getDestinationType());
        assertEquals(1, notif.getRecipientIds().size());
        assertEquals((Long) FOLLOWER_ID, notif.getRecipientIds().get(0));
        assertEquals(NotificationType.REQUEST_GROUP_ACCESS_APPROVED, notif.getType());
        assertEquals(0, notif.getActivityId());
    }
}
