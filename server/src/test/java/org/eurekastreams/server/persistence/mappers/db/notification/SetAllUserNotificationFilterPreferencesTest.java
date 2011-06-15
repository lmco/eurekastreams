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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test of the mapper.
 */
public class SetAllUserNotificationFilterPreferencesTest extends MapperTest
{
    /** Test data. */
    private static final long PERSON_ID = 42L;

    /** Test data. */
    private static final long OTHER_ID = 99L;

    /** Test data. */
    private final NotificationFilterPreferenceDTO dto1;
    /** Test data. */
    private final NotificationFilterPreferenceDTO dto2;
    /** Test data. */
    private final NotificationFilterPreferenceDTO dto3;

    /** System under test. */
    private SetAllUserNotificationFilterPreferences sut;

    /**
     * Constructor.
     */
    public SetAllUserNotificationFilterPreferencesTest()
    {
        // Note: supply a different user id to confirm that the mapper uses the ID in the request and not the one in the
        // DTO
        dto1 = new NotificationFilterPreferenceDTO(OTHER_ID, "SMS", "POST_TO_PERSONAL_STREAM");
        dto2 = new NotificationFilterPreferenceDTO(OTHER_ID, "SMS", "COMMENT");
        dto3 = new NotificationFilterPreferenceDTO(OTHER_ID, "IM", "LIKE");
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SetAllUserNotificationFilterPreferences();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test a multi-row case.
     */
    @Test
    public void testExecuteMultipleRows()
    {
        sut.execute(new SetUserNotificationFilterPreferencesRequest(PERSON_ID, Arrays.asList(dto1, dto2, dto3)));

        checkAgainstDb(PERSON_ID, Arrays.asList(dto1, dto2, dto3));
    }

    /**
     * Test an empty case.
     */
    @Test
    public void testExecuteZeroRows()
    {
        sut.execute(new SetUserNotificationFilterPreferencesRequest(PERSON_ID, Collections.singletonList(dto1)));

        checkAgainstDb(PERSON_ID, Collections.singletonList(dto1));
    }

    /**
     * Test with no initial rows.
     */
    @Test
    public void testExecuteNoInitialRows()
    {
        final long id = 4507L;

        sut.execute(new SetUserNotificationFilterPreferencesRequest(id, Arrays.asList(dto1, dto2)));

        checkAgainstDb(id, Arrays.asList(dto1, dto2));
    }

    /**
     * Gets results from the db to verify.
     *
     * @param personId
     *            Person id.
     * @param expectedList
     *            Expected results.
     */
    @SuppressWarnings("unchecked")
    private void checkAgainstDb(final long personId, final List<NotificationFilterPreferenceDTO> expectedList)
    {
        List<NotificationFilterPreferenceDTO> list = getEntityManager()
                .createQuery(
                        "select new org.eurekastreams.server.domain.NotificationFilterPreferenceDTO"
                                + "(person.id,notifierType,notificationCategory) "
                                + "from NotificationFilterPreference where person.id = :personId")
                .setParameter("personId", personId).getResultList();

        assertEquals(expectedList.size(), list.size());
        for (NotificationFilterPreferenceDTO item : expectedList)
        {
            // expect the DTO to contain the person ID from the main request and not the person ID from the individual
            // DTO
            NotificationFilterPreferenceDTO expected = new NotificationFilterPreferenceDTO(personId,
                    item.getNotifierType(), item.getNotificationCategory());
            assertTrue(Matchers.hasItem(equalInternally(expected)).matches(list));
        }
    }
}
