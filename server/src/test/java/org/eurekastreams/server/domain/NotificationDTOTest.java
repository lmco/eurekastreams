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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Collections;

import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.action.execution.notification.idle.NotificationDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.junit.Test;

/**
 * Tests the NotificationDTO. Normally we don't test DTOs, but this DTO has a copy constructor which needs to copy all
 * the fields.
 */
public class NotificationDTOTest
{
    /**
     * Tests the copy constructor.
     * 
     * @throws IllegalAccessException
     *             Possibly.
     * @throws IllegalArgumentException
     *             Possibly.
     */
    @Test
    public void test() throws IllegalArgumentException, IllegalAccessException
    {
        // build fully non-null source
        NotificationDTO dto1 = new NotificationDTO(Collections.singletonList(8L),
                NotificationType.COMMENT_TO_SAVED_POST, 7L, 6L, EntityType.GROUP, 5L);
        dto1.setActorAccountId("jdoe");
        dto1.setActorName("John Doe");
        dto1.setAuxiliary(EntityType.GROUP, "group1", "First Group");
        dto1.setActivityType(BaseObjectType.FILE);
        dto1.setDestinationName("Group");
        dto1.setDestinationUniqueId("group");

        // insure no fields are null, otherwise when we compare fields of the DTOs and found null == null, we wouldn't
        // know if it was copied or if that was just the default value of dto2.
        for (Field field : dto1.getClass().getDeclaredFields())
        {
            field.setAccessible(true);
            Object value = field.get(dto1);
            assertNotNull("Field " + field.getName() + " is null.  All fields of original DTO need to be non-null "
                    + "for the test to be meaningful.", value);
        }

        // clone
        NotificationDTO dto2 = new NotificationDTO(dto1);

        // insure equal
        assertTrue(IsEqualInternally.areEqualInternally(dto1, dto2));
    }
}
