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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.icu.util.Calendar;

/**
 * Tests mapper to set all alerts as read.
 */
public class SetAllApplicationAlertsAsReadByUserIdTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private SetAllApplicationAlertsAsReadByUserId sut;

    /**
     * Test execute method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute()
    {
        final Long userId = 99L;
        final int year = 2010;
        final int day = 25;
        Date date = new GregorianCalendar(year, Calendar.FEBRUARY, day).getTime();
        sut.execute(userId, date);

        List<ApplicationAlertNotification> results = getEntityManager().createQuery(
                "from ApplicationAlertNotification where recipient.id = :userId").setParameter("userId", userId)
                .getResultList();

        assertEquals(3, results.size());
        assertTrue(results.get(0).isRead());
        assertTrue(results.get(1).isRead());
        assertTrue(results.get(2).isRead());
    }
}
