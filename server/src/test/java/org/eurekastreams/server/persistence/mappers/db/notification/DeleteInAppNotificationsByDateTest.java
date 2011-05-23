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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests DeleteInAppNotificationsByDate.
 */
public class DeleteInAppNotificationsByDateTest extends MapperTest
{
    /** SUT. */
    private DeleteInAppNotificationsByDate sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new DeleteInAppNotificationsByDate();
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
        Query query = getEntityManager().createQuery("SELECT COUNT(*) FROM InAppNotification");
        long oldCount = (Long) query.getSingleResult();

        // test
        sut.execute(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2010-02-03 00:00"));

        // verify
        long newCount = (Long) query.getSingleResult();
        assertEquals(oldCount - 6, newCount);
    }
}
