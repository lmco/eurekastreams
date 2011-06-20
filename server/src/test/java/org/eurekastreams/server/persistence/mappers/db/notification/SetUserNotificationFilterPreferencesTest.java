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
public class SetUserNotificationFilterPreferencesTest extends MapperTest
{
    /** System under test. */
    private SetUserNotificationFilterPreferences sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SetUserNotificationFilterPreferences();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNothingExists()
    {
        final long personId = 4507L;
        NotificationFilterPreferenceDTO dto = new NotificationFilterPreferenceDTO("SMS", "FOLLOW");
        SetUserNotificationFilterPreferencesRequest rqst = new SetUserNotificationFilterPreferencesRequest(personId,
                Collections.singletonList(dto));
        sut.execute(rqst);

        dto.setPersonId(personId);
        checkAgainstDb(personId, Collections.singletonList(dto));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNoOverlap()
    {
        final long personId = 99L;
        NotificationFilterPreferenceDTO dto = new NotificationFilterPreferenceDTO("SMS", "FOLLOW");
        SetUserNotificationFilterPreferencesRequest rqst = new SetUserNotificationFilterPreferencesRequest(personId,
                Collections.singletonList(dto));
        sut.execute(rqst);

        dto.setPersonId(personId);
        checkAgainstDb(personId, Arrays.asList(dto, new NotificationFilterPreferenceDTO(personId, "email", "COMMENT")));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecutePartialOverlap()
    {
        final long personId = 42L;
        NotificationFilterPreferenceDTO dto1 = new NotificationFilterPreferenceDTO("SMS", "FOLLOW");
        NotificationFilterPreferenceDTO dto2 = new NotificationFilterPreferenceDTO("email", "FOLLOW");
        SetUserNotificationFilterPreferencesRequest rqst = new SetUserNotificationFilterPreferencesRequest(personId,
                Arrays.asList(dto1, dto2));
        sut.execute(rqst);

        dto1.setPersonId(personId);
        dto2.setPersonId(personId);
        checkAgainstDb(personId,
                Arrays.asList(dto1, dto2, new NotificationFilterPreferenceDTO(personId, "email", "LIKE")));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteCompleteOverlap()
    {
        final long personId = 42L;
        NotificationFilterPreferenceDTO dto1 = new NotificationFilterPreferenceDTO("email", "LIKE");
        NotificationFilterPreferenceDTO dto2 = new NotificationFilterPreferenceDTO("email", "FOLLOW");
        SetUserNotificationFilterPreferencesRequest rqst = new SetUserNotificationFilterPreferencesRequest(personId,
                Arrays.asList(dto1, dto2));
        sut.execute(rqst);

        dto1.setPersonId(personId);
        dto2.setPersonId(personId);
        checkAgainstDb(personId, Arrays.asList(dto1, dto2));
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
    private void checkAgainstDb(final long personId,
            final List<NotificationFilterPreferenceDTO> expectedList)
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
            assertTrue(Matchers.hasItem(equalInternally(item)).matches(list));
        }
    }
}
