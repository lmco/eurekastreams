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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test of the mapper.
 */
public class GetNotificationFilterPreferencesByPeopleIdsTest extends MapperTest
{
    /** System under test. */
    @Autowired
    private GetNotificationFilterPreferencesByPeopleIds sut;

    /** Id for smithers. */
    static final long SMITHERS_ID = 98L;

    /** Id for burns. */
    static final long BURNS_ID = 99L;

    /** Id for fordp. */
    static final long FORDP_ID = 42L;

    /**
     * Test with multiple users preferences.
     */
    @Test
    public void testExecuteMultipleRows()
    {
        NotificationFilterPreferenceDTO expected1 =
         new NotificationFilterPreferenceDTO(FORDP_ID, "email", Category.FOLLOW_PERSON);
        NotificationFilterPreferenceDTO expected2 =
                new NotificationFilterPreferenceDTO(FORDP_ID, "email", Category.FOLLOW_GROUP);
        NotificationFilterPreferenceDTO expected3 =
            new NotificationFilterPreferenceDTO(BURNS_ID, "email", Category.COMMENT);

        List<Long> peopleIds = new ArrayList<Long>();
        peopleIds.add(FORDP_ID);
        peopleIds.add(BURNS_ID);

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(peopleIds);

        assertEquals(3, list.size());
        assertTrue(Matchers.hasItem(equalInternally(expected1)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(expected2)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(expected3)).matches(list));
    }

    /**
     * Tests one user.
     */
    @Test
    public void testExecuteSingleRow()
    {
        NotificationFilterPreferenceDTO expected1 =
                new NotificationFilterPreferenceDTO(BURNS_ID, "email", Category.COMMENT);

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(Arrays.asList(BURNS_ID));

        assertEquals(1, list.size());
        assertTrue(Matchers.hasItem(equalInternally(expected1)).matches(list));
    }

    /**
     * Test not found.
     */
    @Test
    public void testExecuteZeroRows()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(Arrays.asList(SMITHERS_ID));
        assertEquals(0, list.size());
    }

    /**
     * Test empty recipients.
     */
    @Test
    public void testExecuteWithEmptyRecipients()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new ArrayList<Long>());
        assertEquals(0, list.size());
    }

    /**
     * Test null recipients.
     */
    @Test
    public void testExecuteWithNullRecipients()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(null);
        assertEquals(0, list.size());
    }

}
