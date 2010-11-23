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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Like translator test.
 */
public class LikeTranslatorTest
{
    /** SUT. */
    LikeTranslator sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new LikeTranslator();
    }

    /**
     * Translate.
     */
    @Test
    public void translate()
    {
        Collection<NotificationDTO> notifs = sut.translate(1L, 2L, 3L);

        Assert.assertNotNull(notifs);
        Assert.assertEquals(1, notifs.size());
        NotificationDTO notif = notifs.iterator().next();
        NotificationDTO expected = new NotificationDTO(Arrays.asList(2L), NotificationType.LIKE_ACTIVITY, 1L, 2L,
                EntityType.PERSON, 3L);
        Assert.assertEquals(expected.getActivityId(), notif.getActivityId());
        Assert.assertEquals(expected.getType(), notif.getType());
        Assert.assertEquals(expected.getDestinationId(), notif.getDestinationId());
    }

    /**
     * Translate; actor is recipient of action.
     */
    @Test
    public void translateActorIsTarget()
    {
        Collection<NotificationDTO> notifs = sut.translate(1L, 1L, 3L);
        assertTrue(notifs.isEmpty());
    }
}
