/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests DisableNotificationCategoryExecution.
 */
public class DisableNotificationCategoryExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: mapper. */
    private final DomainMapper<SetUserNotificationFilterPreferencesRequest, Void> mapper = context.mock(
            DomainMapper.class, "mapper");

    /** SUT. */
    private DisableNotificationCategoryExecution sut;

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        final Map<String, String> notifierTypes = new LinkedHashMap<String, String>();
        notifierTypes.put("EMAIL", "");
        notifierTypes.put("INAPP", "");
        sut = new DisableNotificationCategoryExecution(mapper, notifierTypes);

        final AnonymousClassInterceptor<SetUserNotificationFilterPreferencesRequest> rqstInt = // \n
        new AnonymousClassInterceptor<SetUserNotificationFilterPreferencesRequest>();

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(any(SetUserNotificationFilterPreferencesRequest.class)));
                will(rqstInt);
            }
        });

        PrincipalActionContext ctx = TestContextCreator.createPrincipalActionContext("FOLLOW", null, 8L);
        sut.execute(ctx);

        context.assertIsSatisfied();
        SetUserNotificationFilterPreferencesRequest rqst = rqstInt.getObject();
        assertEquals(8L, rqst.getPersonId());
        List<NotificationFilterPreferenceDTO> list = (List<NotificationFilterPreferenceDTO>) rqst.getPrefList();
        assertEquals(2, list.size());

        // the two DTOs we expect must be present, but the person id can either be empty or set to the right value
        assertTrue(Matchers.hasItem(equalInternally(new NotificationFilterPreferenceDTO(8L, "EMAIL", "FOLLOW")))
                .matches(list)
                || Matchers.hasItem(equalInternally(new NotificationFilterPreferenceDTO("EMAIL", "FOLLOW"))).matches(
                        list));
        assertTrue(Matchers.hasItem(equalInternally(new NotificationFilterPreferenceDTO(8L, "INAPP", "FOLLOW")))
                .matches(list)
                || Matchers.hasItem(equalInternally(new NotificationFilterPreferenceDTO("INAPP", "FOLLOW"))).matches(
                        list));

        // assertEquals(8L, list.get(0).getPersonId());
        // assertEquals("FOLLOW", list.get(0).getNotificationCategory());
        // assertEquals("EMAIL", list.get(0).getNotifierType());
        // assertEquals(8L, list.get(1).getPersonId());
        // assertEquals("FOLLOW", list.get(1).getNotificationCategory());
        // assertEquals("INAPP", list.get(1).getNotifierType());
    }
}
