/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eurekastreams.commons.test.IsEqualInternally;
import org.junit.Test;

/**
 * Tests InAppNotificationEntity.
 */
public class InAppNotificationEntityTest
{
    /**
     * Tests copy constructor.
     */
    @Test
    public void testClone()
    {
        Person p = new Person("account", "first", "middle", "last", "preferred");

        // build the first object
        InAppNotificationEntity first = new InAppNotificationEntity();
        first.setRecipient(p);
        first.setNotificationType(NotificationType.COMMENT_TO_COMMENTED_POST);
        first.setNotificationDate(new Date());
        first.setMessage("Hi");
        first.setUrl("http://www.eurekastreams.org");
        first.setHighPriority(true);
        first.setRead(true);
        first.setSourceType(EntityType.APPLICATION);
        first.setSourceUniqueId("app");
        first.setSourceName("App Name");
        first.setAvatarOwnerType(EntityType.PERSON);
        first.setAvatarOwnerUniqueId("jdoe");

        // clone
        InAppNotificationEntity second = new InAppNotificationEntity(first);

        // compare
        assertTrue(IsEqualInternally.areEqualInternally(first, second));
    }
}
