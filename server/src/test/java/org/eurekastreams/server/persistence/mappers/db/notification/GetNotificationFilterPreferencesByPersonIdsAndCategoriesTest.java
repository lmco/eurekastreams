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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.notification.GetNotificationFilterPreferenceRequest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test of the mapper.
 */
public class GetNotificationFilterPreferencesByPersonIdsAndCategoriesTest extends MapperTest
{
    /** System under test. */
    private GetNotificationFilterPreferencesByPersonIdsAndCategories sut;

    /** Id for smithers. */
    static final long SMITHERS_ID = 98L;

    /** Id for burns. */
    static final long BURNS_ID = 99L;

    /** Id for fordp. */
    static final long FORDP_ID = 42L;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetNotificationFilterPreferencesByPersonIdsAndCategories();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test with multiple users preferences.
     */
    @Test
    public void testExecuteMultipleRows()
    {
        NotificationFilterPreferenceDTO expected1 = new NotificationFilterPreferenceDTO(FORDP_ID, "email", "COMMENT");
        NotificationFilterPreferenceDTO expected2 = new NotificationFilterPreferenceDTO(FORDP_ID, "email", "LIKE");
        NotificationFilterPreferenceDTO expected3 = new NotificationFilterPreferenceDTO(BURNS_ID, "email", "COMMENT");

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                Arrays.asList(FORDP_ID, BURNS_ID), Arrays.asList("COMMENT", "LIKE")));

        assertEquals(3, list.size());
        assertTrue(Matchers.hasItem(equalInternally(expected1)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(expected2)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(expected3)).matches(list));
    }

    /**
     * Test with multiple users preferences.
     */
    @Test
    public void testExecuteLimitByCategory()
    {
        NotificationFilterPreferenceDTO expected1 = new NotificationFilterPreferenceDTO(FORDP_ID, "email", "COMMENT");
        NotificationFilterPreferenceDTO expected3 = new NotificationFilterPreferenceDTO(BURNS_ID, "email", "COMMENT");

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                Arrays.asList(FORDP_ID, BURNS_ID), Arrays.asList("COMMENT")));

        assertEquals(2, list.size());
        assertTrue(Matchers.hasItem(equalInternally(expected1)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(expected3)).matches(list));
    }

    /**
     * Tests one user.
     */
    @Test
    public void testExecuteSingleRow()
    {
        NotificationFilterPreferenceDTO expected1 = new NotificationFilterPreferenceDTO(BURNS_ID, "email", "COMMENT");

        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                Arrays.asList(BURNS_ID), Arrays.asList("COMMENT", "LIKE")));

        assertEquals(1, list.size());
        assertTrue(Matchers.hasItem(equalInternally(expected1)).matches(list));
    }

    /**
     * Test not found.
     */
    @Test
    public void testExecuteZeroRows()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                Arrays.asList(SMITHERS_ID), Arrays.asList("COMMENT", "LIKE")));
        assertEquals(0, list.size());
    }

    /**
     * Test empty recipients.
     */
    @Test
    public void testExecuteWithEmptyRecipients()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                Collections.EMPTY_LIST, Collections.singleton("category")));
        assertEquals(0, list.size());
    }

    /**
     * Test null recipients.
     */
    @Test
    public void testExecuteWithNullRecipients()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                null, Collections.singleton("category")));
        assertEquals(0, list.size());
    }

    /**
     * Test empty Categories.
     */
    @Test
    public void testExecuteWithEmptyCategories()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                Collections.singleton(BURNS_ID), Collections.EMPTY_LIST));
        assertEquals(0, list.size());
    }

    /**
     * Test null Categories.
     */
    @Test
    public void testExecuteWithNullCategories()
    {
        Collection<NotificationFilterPreferenceDTO> list = sut.execute(new GetNotificationFilterPreferenceRequest(
                Collections.singleton(BURNS_ID), null));
        assertEquals(0, list.size());
    }
}
