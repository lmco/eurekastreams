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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test of the mapper.
 */
public class SetUserNotificationFilterPreferencesTest extends MapperTest
{
    /** Test data. */
    private static final long PERSON_ID = 42L;

    /** Test data. */
    private NotificationFilterPreferenceDTO dto1;
    /** Test data. */
    private NotificationFilterPreferenceDTO dto2;
    /** Test data. */
    private NotificationFilterPreferenceDTO dto3;


    /** System under test. */
    @Autowired
    private SetUserNotificationFilterPreferences sut;

    /**
     * Constructor.
     */
    public SetUserNotificationFilterPreferencesTest()
    {
        dto1 = new NotificationFilterPreferenceDTO(PERSON_ID, "SMS", Category.POST_TO_GROUP_STREAM);
        dto2 = new NotificationFilterPreferenceDTO(PERSON_ID, "SMS", Category.COMMENT_IN_GROUP_STREAM);
        dto3 = new NotificationFilterPreferenceDTO(PERSON_ID, "IM", Category.COMMENT_IN_GROUP_STREAM);
    }

    /**
     * Test a multi-row case.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteMultipleRows()
    {
        sut.execute(new SetUserNotificationFilterPreferencesRequest(PERSON_ID, Arrays.asList(dto1, dto2, dto3)));

        List list = checkAgainstDb(PERSON_ID, 3);
        assertTrue(Matchers.hasItem(equalInternally(dto1)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(dto2)).matches(list));
        assertTrue(Matchers.hasItem(equalInternally(dto3)).matches(list));
    }

    /**
     * Test an empty case.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteZeroRows()
    {
        sut.execute(new SetUserNotificationFilterPreferencesRequest(PERSON_ID, Arrays.asList(dto1)));

        List list = checkAgainstDb(PERSON_ID, 1);
        assertTrue(Matchers.hasItem(equalInternally(dto1)).matches(list));
    }

    /**
     * Test with no initial rows.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteNoInitialRows()
    {
        final long id = 4507L;

        sut.execute(new SetUserNotificationFilterPreferencesRequest(id, Arrays.asList(dto1, dto2)));

        checkAgainstDb(id, 2);
    }

    /**
     * Gets results from the db to verify.
     *
     * @param personId
     *            Person id.
     * @param count
     *            Expected number of rows.
     * @return List of results.
     */
    @SuppressWarnings("unchecked")
    private List checkAgainstDb(final long personId, final int count)
    {
        List list =
                getEntityManager().createQuery(
                        "select new org.eurekastreams.server.domain.NotificationFilterPreferenceDTO"
                                + "(person.id,notifierType,notificationCategory) "
                                + "from NotificationFilterPreference where person.id = :personId").setParameter(
                        "personId", personId).getResultList();

        assertEquals(count, list.size());
        return list;
    }
}
