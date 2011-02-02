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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.action.execution.notification.translator.NotificationTranslator;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetNotificationFilterPreferencesByPeopleIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

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

    /** Mock comment translator. */
    private final NotificationTranslator commentTranslator = context.mock(NotificationTranslator.class,
            "commentTranslator");

    /** Mock follower translator. */
    private final NotificationTranslator followerTranslator = context.mock(NotificationTranslator.class,
            "followerTranslator");

    /** Fixture: populator. */
    private final NotificationPopulator populator = context.mock(NotificationPopulator.class);

    /** Mock application alert notifier. */
    private final Notifier applicationNotifier = context.mock(Notifier.class, "appAlertNotifier");

    /** Mock email notifier. */
    private final Notifier emailNotifier = context.mock(Notifier.class, "emailNotifier");

    /** The mock preferences mapper. */
    private final GetNotificationFilterPreferencesByPeopleIds preferencesMapper = context
            .mock(GetNotificationFilterPreferencesByPeopleIds.class);

    /** Mapper to get people for determining locked users. */
    private final DomainMapper<Long, PersonModelView> personMapper = context.mock(DomainMapper.class, "personMapper");

    /** Fixture: person. */
    private final PersonModelView person = context.mock(PersonModelView.class);

    /** Fixture: person. */
    private final PersonModelView person2 = context.mock(PersonModelView.class, "person2");

    /** Fixture: filter. */
    private final RecipientFilter filterAppAlert = context.mock(RecipientFilter.class, "filterAppAlert");

    /** Fixture: filter. */
    private final RecipientFilter filterEmail = context.mock(RecipientFilter.class, "filterEmail");

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

        Map<String, Iterable<RecipientFilter>> filters = new HashMap<String, Iterable<RecipientFilter>>();
        filters.put("APP_ALERT", Collections.singletonList(filterAppAlert));
        filters.put("EMAIL", Collections.singletonList(filterEmail));

        Map<NotificationType, Category> notificationTypeToCategory = new HashMap<NotificationType, Category>();
        notificationTypeToCategory.put(NotificationType.FOLLOW_PERSON, Category.FOLLOW_PERSON);
        notificationTypeToCategory.put(NotificationType.COMMENT_TO_COMMENTED_POST, Category.COMMENT);

        sut = new CreateNotificationsExecution(translators, populator, notifiers, preferencesMapper, personMapper,
                notificationTypeToCategory, filters);

        context.checking(new Expectations()
        {
            {
                // make filters by default not reject anything
                allowing(filterAppAlert).shouldFilter(with(any(PersonModelView.class)),
                        with(any(NotificationDTO.class)), with(any(String.class)));
                will(returnValue(false));
                allowing(filterEmail).shouldFilter(with(same(person)), with(any(NotificationDTO.class)),
                        with(any(String.class)));
                will(returnValue(false));
            }
        });
    }

    /**
     * Tests execute.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute() throws Exception
    {
        final List<Long> recipients = Collections.singletonList(4L);
        final NotificationDTO notification = new NotificationDTO(recipients, NotificationType.FOLLOW_PERSON, 1L, 2L,
                EntityType.PERSON, 3L);

        context.checking(new Expectations()
        {
            {
                oneOf(followerTranslator).translate(1, 2, 3);
                will(returnValue(Collections.singletonList(notification)));

                allowing(personMapper).execute(with(equal(4L)));
                will(returnValue(person));

                oneOf(populator).populate(with(same(notification)));

                oneOf(preferencesMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<NotificationFilterPreferenceDTO>()));

                oneOf(applicationNotifier).notify(with(any(NotificationDTO.class)));
                will(returnValue(null));
                oneOf(emailNotifier).notify(with(any(NotificationDTO.class)));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FOLLOWER, 1, 2, 3);
        sut.execute(TestContextCreator.createTaskHandlerAsyncContext(request));
        context.assertIsSatisfied();
    }

    /**
     * Tests execute.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteNoFilters() throws Exception
    {
        final List<Long> recipients = Collections.singletonList(4L);
        final NotificationDTO notification = new NotificationDTO(recipients, NotificationType.FOLLOW_PERSON, 1L, 2L,
                EntityType.PERSON, 3L);

        context.checking(new Expectations()
        {
            {
                oneOf(followerTranslator).translate(1, 2, 3);
                will(returnValue(Collections.singletonList(notification)));

                allowing(personMapper).execute(with(equal(4L)));
                will(returnValue(person));

                oneOf(populator).populate(with(same(notification)));

                oneOf(preferencesMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<NotificationFilterPreferenceDTO>()));

                oneOf(emailNotifier).notify(with(any(NotificationDTO.class)));
            }
        });

        sut = new CreateNotificationsExecution(Collections.singletonMap(RequestType.FOLLOWER, followerTranslator),
                populator, Collections.singletonMap("EMAIL", emailNotifier), preferencesMapper, personMapper,
                Collections.singletonMap(NotificationType.FOLLOW_PERSON, Category.FOLLOW_PERSON),
                Collections.EMPTY_MAP);

        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FOLLOWER, 1, 2, 3);
        sut.execute(TestContextCreator.createTaskHandlerAsyncContext(request));
        context.assertIsSatisfied();
    }

    /**
     * Tests execute where users have notification filter preferences for this event type. Insures the preferences don't
     * interact between notifiers.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithPrefernceFilteredResults() throws Exception
    {
        final List<Long> recipients = Arrays.asList(4L, 5L);
        final NotificationDTO notification = new NotificationDTO(recipients,
                NotificationType.COMMENT_TO_COMMENTED_POST, 1L, 2L, EntityType.PERSON, 3L);

        final List<NotificationFilterPreferenceDTO> prefs = new ArrayList<NotificationFilterPreferenceDTO>();
        prefs.add(new NotificationFilterPreferenceDTO(4L, "EMAIL", Category.COMMENT));
        prefs.add(new NotificationFilterPreferenceDTO(5L, "APP_ALERT", Category.COMMENT));

        context.checking(new Expectations()
        {
            {
                oneOf(commentTranslator).translate(1, 2, 3);
                will(returnValue(Collections.singletonList(notification)));

                allowing(personMapper).execute(with(equal(4L)));
                will(returnValue(person));

                allowing(personMapper).execute(with(equal(5L)));
                will(returnValue(person2));

                oneOf(preferencesMapper).execute(with(any(List.class)));
                will(returnValue(prefs));

                oneOf(populator).populate(with(same(notification)));

                allowing(filterEmail).shouldFilter(with(any(PersonModelView.class)), with(any(NotificationDTO.class)),
                        with(any(String.class)));
                will(returnValue(false));

                oneOf(applicationNotifier).notify(with(new EasyMatcher<NotificationDTO>()
                {
                    @Override
                    protected boolean isMatch(final NotificationDTO inTestObject)
                    {
                        return inTestObject.getRecipientIds().contains(4L)
                                && !inTestObject.getRecipientIds().contains(5L);
                    }
                }));
                will(returnValue(null));
                oneOf(emailNotifier).notify(with(new EasyMatcher<NotificationDTO>()
                {
                    @Override
                    protected boolean isMatch(final NotificationDTO inTestObject)
                    {
                        return inTestObject.getRecipientIds().contains(5L)
                                && !inTestObject.getRecipientIds().contains(4L);
                    }
                }));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 1, 2, 3);
        sut.execute(TestContextCreator.createTaskHandlerAsyncContext(request));
        context.assertIsSatisfied();
    }


    /**
     * Tests execute where one recipient gets filtered.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithFilteredRecipient() throws Exception
    {
        final List<Long> recipients = Arrays.asList(4L, 5L);
        final NotificationDTO notification = new NotificationDTO(recipients, NotificationType.FOLLOW_PERSON, 1L, 2L,
                EntityType.PERSON, 3L);

        context.checking(new Expectations()
        {
            {
                oneOf(followerTranslator).translate(1, 2, 3);
                will(returnValue(Collections.singletonList(notification)));

                allowing(personMapper).execute(with(equal(4L)));
                will(returnValue(person));

                allowing(personMapper).execute(with(equal(5L)));
                will(returnValue(person2));

                allowing(filterEmail).shouldFilter(person2, notification, "EMAIL");
                will(returnValue(true));

                oneOf(preferencesMapper).execute(with(any(List.class)));
                will(returnValue(Collections.EMPTY_LIST));

                oneOf(populator).populate(with(same(notification)));

                oneOf(applicationNotifier).notify(with(new EasyMatcher<NotificationDTO>()
                {
                    @Override
                    protected boolean isMatch(final NotificationDTO inTestObject)
                    {
                        return inTestObject.getRecipientIds().containsAll(Arrays.asList(4L, 5L));
                    }
                }));
                will(returnValue(null));
                oneOf(emailNotifier).notify(with(new EasyMatcher<NotificationDTO>()
                {
                    @Override
                    protected boolean isMatch(final NotificationDTO inTestObject)
                    {
                        return inTestObject.getRecipientIds().contains(4L)
                                && !inTestObject.getRecipientIds().contains(5L);
                    }
                }));
            }
        });

        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FOLLOWER, 1, 2, 3);
        sut.execute(TestContextCreator.createTaskHandlerAsyncContext(request));
        context.assertIsSatisfied();
    }



    /**
     * Tests execute with a notifier exception (for coverage).
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteNotifierError() throws Exception
    {
        final List<Long> recipients = Collections.singletonList(4L);
        final NotificationDTO notification = new NotificationDTO(recipients, NotificationType.FOLLOW_PERSON, 1L, 2L,
                EntityType.PERSON, 3L);

        context.checking(new Expectations()
        {
            {
                oneOf(followerTranslator).translate(1, 2, 3);
                will(returnValue(Collections.singletonList(notification)));

                allowing(personMapper).execute(with(equal(4L)));
                will(returnValue(person));

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
        sut.execute(TestContextCreator.createTaskHandlerAsyncContext(request));
        context.assertIsSatisfied();
    }

    /**
     * Tests when there is no translator for the request type.
     */
    @Test
    public void testExecuteUnlistedRequestType()
    {
        CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.FLAG_ACTIVITY, 1, 2, 3);
        assertEquals(Boolean.FALSE, sut.execute(TestContextCreator.createTaskHandlerAsyncContext(request)));

        context.assertIsSatisfied();
    }
}
