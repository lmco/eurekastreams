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
package org.eurekastreams.server.persistence.mappers.db.notification;

import static junit.framework.Assert.assertEquals;
import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test of the mapper.
 */
public class GetNotificationFilterPreferencesByPersonIdTest extends MapperTest
{
    /** System under test. */
    private GetNotificationFilterPreferencesByPersonId sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetNotificationFilterPreferencesByPersonId();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test a multi-row case.
     */
    @Test
    public void testExecuteMultipleRows()
    {
        final long id = 42L;

        NotificationFilterPreferenceDTO expected1 = new NotificationFilterPreferenceDTO(id, "email", "COMMENT");
        NotificationFilterPreferenceDTO expected2 = new NotificationFilterPreferenceDTO(id, "email", "LIKE");

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(id);

        assertEquals(2, list.size());
        assertTrue(Matchers.hasItem(equalInternally(expected1)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(expected2)).matches(list));
    }

    /**
     * Test a single-row case.
     */
    @Test
    public void testExecuteSingleRow()
    {
        final long id = 99L;

        NotificationFilterPreferenceDTO expected1 = new NotificationFilterPreferenceDTO(id, "email", "COMMENT");

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(id);

        assertEquals(1, list.size());
        assertTrue(Matchers.hasItem(equalInternally(expected1)).matches(list));
    }

    /**
     * Test a zero-row case.
     */
    @Test
    public void testExecuteZeroRows()
    {
        final long id = 98L;

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(id);

        assertEquals(0, list.size());
    }
}
