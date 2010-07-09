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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.notification.translator.CommentTranslator;
import org.eurekastreams.server.action.execution.notification.translator.FollowerTranslator;
import org.eurekastreams.server.action.execution.notification.translator.NotificationTranslator;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.persistence.mappers.db.GetNotificationFilterPreferencesByPeopleIds;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Test suite for the {@link CreateNotificationsExecution} class.
 *
 */
public class CreateNotificationsExecutionTest
{
    /**
     * System under test.
     */
    private CreateNotificationsExecution sut;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock comment translator.
     */
    private CommentTranslator commentTranslator = context.mock(CommentTranslator.class);

    /**
     * Mock follower translator.
     */
    private FollowerTranslator followerTranslator = context.mock(FollowerTranslator.class);

    /** Fixture: populator. */
    private NotificationPopulator populator = context.mock(NotificationPopulator.class);

    /**
     * Mock application alert notifier.
     */
    private ApplicationAlertNotifier applicationNotifier = context.mock(ApplicationAlertNotifier.class);

    /**
     * Mock email notifier.
     */
    private Notifier emailNotifier = context.mock(Notifier.class);

    /**
     * The mock preferences mapper.
     */
    private GetNotificationFilterPreferencesByPeopleIds preferencesMapper = context
            .mock(GetNotificationFilterPreferencesByPeopleIds.class);

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        Map<RequestType, NotificationTranslator> translators = new HashMap<RequestType, NotificationTranslator>();
        translators.put(RequestType.FOLLOWER, followerTranslator);
        translators.put(RequestType.COMMENT, commentTranslator);

        Map<String, Notifier> notifiers = new HashMap<String, Notifier>();
        notifiers.put("APP_ALERT", applicationNotifier);
        notifiers.put("EMAIL", emailNotifier);

        Map<NotificationType, Category> notificationTypeToCategory = new HashMap<NotificationType, Category>();
        notificationTypeToCategory.put(NotificationType.FOLLOW_PERSON, Category.FOLLOW_PERSON);

        sut =
                new CreateNotificationsExecution(translators, populator, notifiers, preferencesMapper,
                        notificationTypeToCategory);
    }

    /**
     * Tests performAction.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute() throws Exception
    {
        final List<Long> recipients = new ArrayList<Long>();
        recipients.add(4L);
        final NotificationDTO notification =
                new NotificationDTO(recipients, NotificationType.FOLLOW_PERSON, 1L, 2L,
                EntityType.PERSON, 3L);
        final Collection<NotificationDTO> notifications = Collections.singletonList(notification);

        context.checking(new Expectations()
        {
            {
                oneOf(followerTranslator).translate(1, 2, 3);
                will(returnValue(notifications));

                oneOf(populator).populate(with(same(notification)));

                oneOf(preferencesMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<NotificationFilterPreferenceDTO>()));

                oneOf(applicationNotifier).notify(with(any(NotificationDTO.class)));
                will(returnValue(null));
                oneOf(emailNotifier).notify(with(any(NotificationDTO.class)));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FOLLOWER, 1, 2, 3);
        AsyncActionContext currentContext = new AsyncActionContext(request);
        TaskHandlerActionContext<ActionContext> currentTaskHandlerContext = new TaskHandlerActionContext<ActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerContext);
        context.assertIsSatisfied();
    }

    /**
     * Tests performAction where one of the users has a notification filter preference for this event type.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithFilteredResults() throws Exception
    {
        final List<Long> recipients = new ArrayList<Long>();
        recipients.add(4L);
        final NotificationDTO notification =
                new NotificationDTO(recipients, NotificationType.FOLLOW_PERSON, 1L, 2L,
                EntityType.PERSON, 3L);
        final Collection<NotificationDTO> notifications = Collections.singletonList(notification);

        final NotificationFilterPreferenceDTO pref1 = new NotificationFilterPreferenceDTO(4L, "EMAIL",
                Category.FOLLOW_PERSON);
        final List<NotificationFilterPreferenceDTO> prefs = new ArrayList<NotificationFilterPreferenceDTO>();
        prefs.add(pref1);

        context.checking(new Expectations()
        {
            {
                oneOf(followerTranslator).translate(1, 2, 3);
                will(returnValue(notifications));

                oneOf(populator).populate(with(same(notification)));

                oneOf(preferencesMapper).execute(with(any(List.class)));
                will(returnValue(prefs));

                oneOf(applicationNotifier).notify(with(any(NotificationDTO.class)));
                will(returnValue(null));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FOLLOWER, 1, 2, 3);
        AsyncActionContext currentContext = new AsyncActionContext(request);
        TaskHandlerActionContext<ActionContext> currentTaskHandlerContext = new TaskHandlerActionContext<ActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerContext);
        context.assertIsSatisfied();
    }

    /**
     * Tests performAction with a notifier exception (for coverage).
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteNotifierError() throws Exception
    {
        final List<Long> recipients = Arrays.asList(4L);
        final NotificationDTO notification =
                new NotificationDTO(recipients, NotificationType.FOLLOW_PERSON, 1L, 2L,
                EntityType.PERSON, 3L);
        final Collection<NotificationDTO> notifications = Collections.singletonList(notification);

        context.checking(new Expectations()
        {
            {
                oneOf(followerTranslator).translate(1, 2, 3);
                will(returnValue(notifications));

                oneOf(populator).populate(with(same(notification)));

                oneOf(preferencesMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<NotificationFilterPreferenceDTO>()));

                oneOf(applicationNotifier).notify(with(any(NotificationDTO.class)));
                will(returnValue(null));
                oneOf(emailNotifier).notify(with(any(NotificationDTO.class)));
                will(throwException(new Exception("BAD")));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FOLLOWER, 1, 2, 3);
        AsyncActionContext currentContext = new AsyncActionContext(request);
        TaskHandlerActionContext<ActionContext> currentTaskHandlerContext = new TaskHandlerActionContext<ActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerContext);
        context.assertIsSatisfied();
    }

    /**
     * Tests when there is no translator for the request type.
     */
    @Test
    public void testExecuteUnlistedRequestType()
    {
        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FLAG_ACTIVITY, 1, 2, 3);
        AsyncActionContext currentContext = new AsyncActionContext(request);
        TaskHandlerActionContext<ActionContext> currentTaskHandlerContext =
                new TaskHandlerActionContext<ActionContext>(currentContext, new ArrayList<UserActionRequest>());
        assertEquals(Boolean.FALSE, sut.execute(currentTaskHandlerContext));

        context.assertIsSatisfied();
    }
}
