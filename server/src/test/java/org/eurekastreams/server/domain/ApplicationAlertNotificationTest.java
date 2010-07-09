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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.ibm.icu.util.Calendar;



/**
 * Tests the ApplicationAlertNotification.
 */
public class ApplicationAlertNotificationTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Tests the getters and setters.
     */
    @Test
    public void testGettersSetters()
    {
        ApplicationAlertNotification sut = new ApplicationAlertNotification();

        // general and management fields

        Person recipient = context.mock(Person.class);
        sut.setRecipient(recipient);
        assertEquals(recipient, sut.getRecipient());

        sut.setNotificiationType(NotificationType.FOLLOW_PERSON);
        assertEquals(NotificationType.FOLLOW_PERSON, sut.getNotificiationType());

        final int year = 2010;
        Date date = new GregorianCalendar(year, Calendar.JANUARY, 5).getTime();
        sut.setNotificationDate(date);
        assertEquals(date, sut.getNotificationDate());

        sut.setRead(true);
        assertTrue(sut.isRead());

        // actor

        sut.setActorAccountId("accountId");
        assertEquals("accountId", sut.getActorAccountId());

        sut.setActorName("Actor");
        assertEquals("Actor", sut.getActorName());

        // activity

        sut.setActivityId(1L);
        assertEquals((Long) 1L, sut.getActivityId());

        sut.setActivityType(BaseObjectType.NOTE);
        assertEquals(BaseObjectType.NOTE, sut.getActivityType());

        // destination

        sut.setDestinationName("Group");
        assertEquals("Group", sut.getDestinationName());

        sut.setDestinationType(EntityType.GROUP);
        assertEquals(EntityType.GROUP, sut.getDestinationType());

        sut.setDestinationUniqueId("groupshortname");
        assertEquals("groupshortname", sut.getDestinationUniqueId());

        // auxiliary

        sut.setAuxiliaryName("AnOrg");
        assertEquals("AnOrg", sut.getAuxiliaryName());

        sut.setAuxiliaryUniqueId("anorg");
        assertEquals("anorg", sut.getAuxiliaryUniqueId());

        sut.setAuxiliaryType(EntityType.ORGANIZATION);
        assertEquals(EntityType.ORGANIZATION, sut.getAuxiliaryType());
    }

    /**
     * Tests creation from a DTO.
     */
    @Test
    public void testCreateFromDTO()
    {
        NotificationDTO dto = new NotificationDTO(Collections.EMPTY_LIST, NotificationType.REQUEST_NEW_GROUP, 0L);
        dto.setActorAccountId("accountId");
        dto.setActorName("Actor");
        dto.setActivityId(1L);
        dto.setActivityType(BaseObjectType.NOTE);
        dto.setDestinationName("Group");
        dto.setDestinationType(EntityType.GROUP);
        dto.setDestinationUniqueId("groupshortname");
        dto.setAuxiliaryName("AnOrg");
        dto.setAuxiliaryUniqueId("anorg");
        dto.setAuxiliaryType(EntityType.ORGANIZATION);

        Person recipient = context.mock(Person.class);

        ApplicationAlertNotification sut = new ApplicationAlertNotification(dto, recipient);

        assertEquals(recipient, sut.getRecipient());
        assertEquals(NotificationType.REQUEST_NEW_GROUP, sut.getNotificiationType());
        assertFalse(sut.isRead());
        assertEquals("accountId", sut.getActorAccountId());
        assertEquals("Actor", sut.getActorName());
        assertEquals((Long) 1L, sut.getActivityId());
        assertEquals(BaseObjectType.NOTE, sut.getActivityType());
        assertEquals("Group", sut.getDestinationName());
        assertEquals(EntityType.GROUP, sut.getDestinationType());
        assertEquals("groupshortname", sut.getDestinationUniqueId());
        assertEquals("AnOrg", sut.getAuxiliaryName());
        assertEquals("anorg", sut.getAuxiliaryUniqueId());
        assertEquals(EntityType.ORGANIZATION, sut.getAuxiliaryType());
    }

}
