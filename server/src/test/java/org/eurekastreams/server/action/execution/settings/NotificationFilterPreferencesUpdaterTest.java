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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.persistence.mappers.db.SetUserNotificationFilterPreferences;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the action.
 */
public class NotificationFilterPreferencesUpdaterTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture data. */
    private Map<String, String> notifierTypes = new HashMap<String, String>()
    {
        {
            put("EMAIL", "Email");
            put("SMS", "Text Message");
        }
    };

    /** Fixture: user. */
        private Principal user = context.mock(Principal.class);

    /** Fixture: mapper. */
    private SetUserNotificationFilterPreferences mapper = context.mock(SetUserNotificationFilterPreferences.class);

    /** Fixture: settings data container. */
    private Map<String, Serializable> settings = new HashMap<String, Serializable>();

    /** SUT. */
    private NotificationFilterPreferencesUpdater sut;

    /**
     * Setup for each test.
     */
    @Before
    public void setUp()
    {
        sut = new NotificationFilterPreferencesUpdater(notifierTypes, mapper);
        context.checking(new Expectations()
        {
            {
                allowing(user).getId();
                will(returnValue(9L));
            }
        });
        settings.clear();
    }

    /**
     * Test perform action.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testPerformAction() throws Exception
    {
        NotificationFilterPreferenceDTO pref1 = new NotificationFilterPreferenceDTO("EMAIL", Category.COMMENT);
        NotificationFilterPreferenceDTO pref2 = new NotificationFilterPreferenceDTO("PHONE", Category.FOLLOW_GROUP);
        NotificationFilterPreferenceDTO pref3 = new NotificationFilterPreferenceDTO("SMS", Category.FOLLOW_PERSON);

        settings.put("notif-pref1", pref1);
        settings.put("notif-pref2", pref2);
        settings.put("notif-pref3", pref3);
        settings.put("notif-pref4", null);

        final AnonymousClassInterceptor<SetUserNotificationFilterPreferencesRequest> rqstInt =
                new AnonymousClassInterceptor<SetUserNotificationFilterPreferencesRequest>();

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(any(SetUserNotificationFilterPreferencesRequest.class)));
                will(rqstInt);
            }
        });

        sut.update(settings, user);

        context.assertIsSatisfied();
        SetUserNotificationFilterPreferencesRequest rqst = rqstInt.getObject();
        assertEquals(9, rqst.getPersonId());
        assertEquals(2, rqst.getPrefList().size());
        assertTrue(Matchers.hasItem(pref1).matches(rqst.getPrefList()));
        assertTrue(Matchers.hasItem(pref3).matches(rqst.getPrefList()));
    }

    /**
     * Test perform action.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testPerformActionZeroDto() throws Exception
    {
        NotificationFilterPreferenceDTO pref1 = new NotificationFilterPreferenceDTO("SHOUT", Category.COMMENT);
        NotificationFilterPreferenceDTO pref2 = new NotificationFilterPreferenceDTO("PHONE", Category.FOLLOW_GROUP);
        NotificationFilterPreferenceDTO pref3 = new NotificationFilterPreferenceDTO("IM", Category.FOLLOW_PERSON);

        settings.put("notif-pref1", pref1);
        settings.put("notif-pref2", pref2);
        settings.put("notif-pref3", pref3);
        settings.put("notif-pref4", null);

        final AnonymousClassInterceptor<SetUserNotificationFilterPreferencesRequest> rqstInt =
                new AnonymousClassInterceptor<SetUserNotificationFilterPreferencesRequest>();

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(any(SetUserNotificationFilterPreferencesRequest.class)));
                will(rqstInt);
            }
        });

        sut.update(settings, user);

        context.assertIsSatisfied();
        SetUserNotificationFilterPreferencesRequest rqst = rqstInt.getObject();
        assertEquals(9, rqst.getPersonId());
        assertEquals(0, rqst.getPrefList().size());
    }
}
