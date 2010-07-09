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
package org.eurekastreams.server.action.execution.settings;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;
import org.eurekastreams.server.domain.NotificationFilterPreference;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.db.GetNotificationFilterPreferencesByPersonId;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the action.
 */
public class NotificationFilterPreferencesRetrieverTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final long PERSON_ID = 88L;

    /** Test data. */
    private NotificationFilterPreferenceDTO dto1 =
            new NotificationFilterPreferenceDTO(PERSON_ID, "EMAIL", NotificationFilterPreference.Category.COMMENT);

    /** Test data - notifier not in map. */
    private NotificationFilterPreferenceDTO dto2 =
            new NotificationFilterPreferenceDTO(PERSON_ID, "SMS", NotificationFilterPreference.Category.COMMENT);

    /** Fixture: notifier map. */
    private Map<String, String> notifierTypes = new HashMap<String, String>()
    {
        {
            put("EMAIL", "Email");
            put("IM", "Instant Message");
        }
    };

    /** Fixture: Mapper. */
    private GetNotificationFilterPreferencesByPersonId mapper =
            context.mock(GetNotificationFilterPreferencesByPersonId.class);

    /** Fixture: to return settings. */
    private Map<String, Object> settings = new HashMap<String, Object>();

    /** Fixture: to return support data. */
    private Map<String, Object> support = new HashMap<String, Object>();

    /** Fixture: response message under construction. */
    private RetrieveSettingsResponse response = new RetrieveSettingsResponse(settings, support);

    /** SUT. */
    private NotificationFilterPreferencesRetriever sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setup()
    {
        sut = new NotificationFilterPreferencesRetriever(notifierTypes, mapper);
        settings.clear();
        support.clear();
    }

    /**
     * Test - should filter one row and allow one through.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testPerformActionWithMultipleRows() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(PERSON_ID);
                will(returnValue(Arrays.asList(dto1, dto2)));
            }
        });

        sut.retrieve(PERSON_ID, response);

        context.assertIsSatisfied();

        List<NotificationFilterPreferenceDTO> filters = (List<NotificationFilterPreferenceDTO>) settings
                .get(RetrieveSettingsResponse.SETTINGS_NOTIFICATION_FILTERS);
        Map<String, String> notifiers =
                (Map<String, String>) support.get(RetrieveSettingsResponse.SUPPORT_NOTIFIER_TYPES);

        assertEquals(1, filters.size());
        assertEquals(dto1, filters.iterator().next());
        assertEquals(2, notifiers.size());
    }

    /**
     * Test - should filter one row and allow one through.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testPerformActionWithNoRows() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(PERSON_ID);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        sut.retrieve(PERSON_ID, response);

        context.assertIsSatisfied();

        List<NotificationFilterPreferenceDTO> filters = (List<NotificationFilterPreferenceDTO>) settings
                .get(RetrieveSettingsResponse.SETTINGS_NOTIFICATION_FILTERS);
        Map<String, String> notifiers =
                (Map<String, String>) support.get(RetrieveSettingsResponse.SUPPORT_NOTIFIER_TYPES);

        assertEquals(0, filters.size());
        assertEquals(2, notifiers.size());
    }

}
