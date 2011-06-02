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
package org.eurekastreams.server.persistence.mappers.db.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.List;

import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.domain.InAppNotificationEntity;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests InsertInAppNotificationForAllUsers.
 */
public class InsertInAppNotificationForAllUsersTest extends MapperTest
{
    /** Test data. */
    private static final String MESSAGE = "The message";

    /** Test data. */
    private static final String URL = "The URL";

    /** SUT. */
    private InsertInAppNotificationForAllUsers sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new InsertInAppNotificationForAllUsers();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Tests execute.
     *
     * @throws ParseException
     *             Won't.
     */
    @Test
    public void testExecute() throws ParseException
    {
        long maxId = (Long) getEntityManager().createQuery("SELECT MAX(id) FROM InAppNotification").getSingleResult();

        getEntityManager().createQuery("UPDATE Person SET accountLocked=true WHERE id > 100").executeUpdate();
        long unlockedPersonCount = (Long) getEntityManager().createQuery(
                "SELECT COUNT(*) FROM Person WHERE accountLocked=false").getSingleResult();
        assertTrue("Test is meaningless with no unlocked users.", unlockedPersonCount > 0);

        // test
        sut.execute(new SendPrebuiltNotificationRequest(true, null, MESSAGE, URL));

        // verify
        List results = getEntityManager().createQuery("FROM InAppNotification WHERE id > :id")
                .setParameter("id", maxId).getResultList();
        assertEquals(unlockedPersonCount, results.size());
        for (InAppNotificationEntity notif : (List<InAppNotificationEntity>) results)
        {
            assertTrue(notif.isHighPriority());
            assertEquals(MESSAGE, notif.getMessage());
            assertEquals(URL, notif.getUrl());
        }
    }

}
