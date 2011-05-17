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

import java.util.Arrays;
import java.util.Collection;

import org.eurekastreams.server.action.execution.notification.idle.NotificationDTO;
import org.eurekastreams.server.action.execution.notification.translator.StreamPostTranslator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the stream post notification translator.
 *
 */
public class StreamPostTranslatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long STREAM_OWNER_ID = 2222L;

    /** Test data. */
    private static final long ACTIVITY_ID = 3333L;

    /**
     * Test creating the notification for the event of posting to a personal stream.
     */
    @Test
    public void testTranslatePersonalStreamPost()
    {
        StreamPostTranslator sut = new StreamPostTranslator();
        Collection<NotificationDTO> notifs = sut.translate(ACTOR_ID, STREAM_OWNER_ID, ACTIVITY_ID);

        Assert.assertNotNull(notifs);
        Assert.assertEquals(1, notifs.size());
        NotificationDTO notif = notifs.iterator().next();
        NotificationDTO expected =
                new NotificationDTO(Arrays.asList(STREAM_OWNER_ID), NotificationType.POST_TO_PERSONAL_STREAM, ACTOR_ID,
                        STREAM_OWNER_ID, EntityType.PERSON, ACTIVITY_ID);
        Assert.assertEquals(expected.getActivityId(), notif.getActivityId());
        Assert.assertEquals(expected.getType(), notif.getType());
        Assert.assertEquals(expected.getDestinationId(), notif.getDestinationId());
    }

    /**
     * Test that the notification is not sent for a person posting to his own personal stream.
     */
    @Test
    public void testTranslateOwnPersonalStreamPost()
    {
        StreamPostTranslator sut = new StreamPostTranslator();
        Collection<NotificationDTO> notifs = sut.translate(ACTOR_ID, ACTOR_ID, ACTIVITY_ID);

        Assert.assertEquals(0, notifs.size());
    }

}
