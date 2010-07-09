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

import static junit.framework.Assert.assertEquals;
import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test of the mapper.
 */
public class GetNotificationFilterPreferencesByPersonIdTest extends MapperTest
{
    /** System under test. */
    @Autowired
    private GetNotificationFilterPreferencesByPersonId sut;

    /**
     * Test a multi-row case.
     */
    @Test
    public void testExecuteMultipleRows()
    {
        final long id = 42L;

        NotificationFilterPreferenceDTO expected1 =
         new NotificationFilterPreferenceDTO(id, "email", Category.FOLLOW_PERSON);
        NotificationFilterPreferenceDTO expected2 =
                new NotificationFilterPreferenceDTO(id, "email", Category.FOLLOW_GROUP);

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

        NotificationFilterPreferenceDTO expected1 =
                new NotificationFilterPreferenceDTO(id, "email", Category.COMMENT);

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
