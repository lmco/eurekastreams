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
package org.eurekastreams.server.action.execution.notification.filter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.response.notification.GetUserNotificationFilterPreferencesResponse;
import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.db.notification.GetNotificationFilterPreferencesByPersonId;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the action.
 */
public class GetUserNotificationFilterPreferencesExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final long PERSON_ID = 88L;

    /** Test data. */
    private final NotificationFilterPreferenceDTO dto1 = new NotificationFilterPreferenceDTO(PERSON_ID, "EMAIL",
            "COMMENT");

    /** Test data - notifier not in map. */
    private final NotificationFilterPreferenceDTO dto2 = new NotificationFilterPreferenceDTO(PERSON_ID, "SMS",
            "COMMENT");

    /** Test data - category not in set. */
    private final NotificationFilterPreferenceDTO dto3 = new NotificationFilterPreferenceDTO(PERSON_ID, "EMAIL", "OLD");

    /** Fixture: notifier map. */
    private final Map<String, String> notifierTypes = new HashMap<String, String>()
    {
        {
            put("EMAIL", "Email");
            put("IM", "Instant Message");
        }
    };

    /** Fixture: category set. */
    private final Set<String> categories = Collections.singleton("COMMENT");

    /** Fixture: Mapper. */
    private final GetNotificationFilterPreferencesByPersonId mapper = context
            .mock(GetNotificationFilterPreferencesByPersonId.class);

    /** Fixture: to return settings. */
    private final Map<String, Object> settings = new HashMap<String, Object>();

    /** Fixture: to return support data. */
    private final Map<String, Object> support = new HashMap<String, Object>();

    /** Fixture: response message under construction. */
    private final RetrieveSettingsResponse response = new RetrieveSettingsResponse(settings, support);

    /** SUT. */
    private ExecutionStrategy<PrincipalActionContext> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setup()
    {
        sut = new GetUserNotificationFilterPreferencesExecution(mapper, notifierTypes, categories);
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
                will(returnValue(Arrays.asList(dto1, dto2, dto3)));
            }
        });

        PrincipalActionContext ctx = TestContextCreator.createPrincipalActionContext(null, null, PERSON_ID);
        GetUserNotificationFilterPreferencesResponse result = (GetUserNotificationFilterPreferencesResponse) sut
                .execute(ctx);

        context.assertIsSatisfied();

        assertEquals(1, result.getPreferences().size());
        assertEquals(dto1, result.getPreferences().iterator().next());
        assertEquals(2, result.getNotifierTypes().size());
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

        PrincipalActionContext ctx = TestContextCreator.createPrincipalActionContext(null, null, PERSON_ID);
        GetUserNotificationFilterPreferencesResponse result = (GetUserNotificationFilterPreferencesResponse) sut
                .execute(ctx);

        context.assertIsSatisfied();

        assertTrue(result.getPreferences().isEmpty());
        assertEquals(2, result.getNotifierTypes().size());
    }
}
