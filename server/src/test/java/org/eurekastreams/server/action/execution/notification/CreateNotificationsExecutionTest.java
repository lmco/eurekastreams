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
package org.eurekastreams.server.action.execution.notification;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.action.execution.notification.filter.RecipientFilter;
import org.eurekastreams.server.action.execution.notification.notifier.Notifier;
import org.eurekastreams.server.action.execution.notification.translator.NotificationTranslator;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Property;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.notification.GetNotificationFilterPreferenceRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.testing.TestContextCreator;
import org.eurekastreams.server.testing.TestHelper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests CreateNotificationsExecution.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CreateNotificationsExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: translator. */
    private final NotificationTranslator translator = context.mock(NotificationTranslator.class, "translator");

    /** Map of valid translators. */
    private final Map<RequestType, NotificationTranslator> translators = Collections.singletonMap(RequestType.COMMENT,
            translator);

    /** Fixture: notifier. */
    private final Notifier notifier = context.mock(Notifier.class, "notifier");

    /** List of notifiers that should be executed. */
    private final Map<String, Notifier> notifiers = Collections.singletonMap("EMAIL", notifier);

    /** Mapper to filter out unwanted notifications per recipient. */
    private final DomainMapper<GetNotificationFilterPreferenceRequest, List<NotificationFilterPreferenceDTO>> // \n
    preferencesMapper = context.mock(DomainMapper.class, "preferencesMapper");

    /** Mapper to get people for filtering (determining locked users, etc.). */
    private final DomainMapper<List<Long>, List<PersonModelView>> personsMapper = context.mock(DomainMapper.class,
            "personsMapper");

    /** Provides the category for each notification type. */
    private final Map<NotificationType, String> notificationTypeToCategory = Collections.singletonMap(
            NotificationType.LIKE_ACTIVITY, "LIKE");

    /** Fixture: recipient filter. */
    private final RecipientFilter recipientFilter = context.mock(RecipientFilter.class, "recipientFilter");

    /** Recipient-based filter strategies per notifier type. */
    private final Map<String, Collection<RecipientFilter>> recipientFilters = // \n
    new HashMap<String, Collection<RecipientFilter>>();

    /** Fixture: bulk filter. */
    private final RecipientFilter bulkFilter = context.mock(RecipientFilter.class, "bulkFilter");

    /** Recipient-independent filter strategies per notifier type. */
    private final Map<String, Collection<RecipientFilter>> bulkFilters = // \n
    new HashMap<String, Collection<RecipientFilter>>();

    /** Mappers for loading notification properties. */
    private final Map<Class, DomainMapper<Serializable, Object>> propertyLoadMappers = context.mock(Map.class,
            "propertyLoadMappers");

    /** Properties provided to all notifications. */
    private final Map<String, Property<Object>> defaultProperties = new HashMap<String, Property<Object>>();

    /** Fixture: person. */
    private final PersonModelView person1 = context.mock(PersonModelView.class, "person1");

    /** Fixture: person. */
    private final PersonModelView person2 = context.mock(PersonModelView.class, "person2");

    /** Fixture: person. */
    private final PersonModelView person3 = context.mock(PersonModelView.class, "person3");

    /** Fixture: asyncRequest. */
    private final Serializable asyncRequest = context.mock(Serializable.class, "asyncRequest");

    /** SUT. */
    private CreateNotificationsExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        bulkFilters.clear();
        recipientFilters.clear();
        sut = new CreateNotificationsExecution(translators, notifiers, preferencesMapper, personsMapper,
                notificationTypeToCategory, bulkFilters, recipientFilters, defaultProperties, propertyLoadMappers);

        context.checking(new Expectations()
        {
            {
                allowing(person1).getId();
                will(returnValue(1L));
                allowing(person2).getId();
                will(returnValue(2L));
                allowing(person3).getId();
                will(returnValue(3L));
            }
        });
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNoTranslator()
    {
        TaskHandlerActionContext<ActionContext> ac = TestContextCreator
                .createTaskHandlerAsyncContext(new CreateNotificationsRequest(RequestType.LIKE, 0));
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNoBatch()
    {
        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(null));
            }
        });

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteBatchNoRecipients()
    {
        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(new NotificationBatch()));
            }
        });

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteBulkFilterReject()
    {
        bulkFilters.put("EMAIL", Collections.singletonList(bulkFilter));

        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(new NotificationBatch(NotificationType.PASS_THROUGH, 1L)));

                allowing(personsMapper).execute(Collections.singletonList(1L));
                will(returnValue(Collections.singletonList(person1)));

                oneOf(bulkFilter).shouldFilter(with(equal(NotificationType.PASS_THROUGH)),
                        (PersonModelView) with(equal(null)), with(any(Map.class)), with(equal("EMAIL")));
                will(returnValue(true));
            }
        });

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /**
     * Tests execute.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testExecuteShortCircuit1() throws Exception
    {
        final List<Long> recips = Collections.singletonList(1L);
        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(new NotificationBatch(NotificationType.PASS_THROUGH, 1L)));

                allowing(personsMapper).execute(recips);
                will(returnValue(Collections.singletonList(person1)));

                oneOf(notifier).notify(with(equal(NotificationType.PASS_THROUGH)), with(equal(recips)),
                        with(any(Map.class)), with(any(Map.class)));
                will(returnValue(null));
            }
        });

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /**
     * Tests execute.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testExecuteShortCircuit2() throws Exception
    {
        recipientFilters.put("EMAIL", Collections.EMPTY_LIST);

        final List<Long> recips = Arrays.asList(1L, 2L, 3L);
        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(new NotificationBatch(NotificationType.LIKE_ACTIVITY, recips)));

                allowing(personsMapper).execute(recips);
                will(returnValue(Arrays.asList(person1, person2, person3)));

                oneOf(notifier).notify(with(equal(NotificationType.LIKE_ACTIVITY)), with(equal(recips)),
                        with(any(Map.class)), with(any(Map.class)));
                will(returnValue(Collections.EMPTY_LIST));
            }
        });
        expectPrefsMapper(recips, "LIKE");

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /**
     * Tests execute.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testExecuteSomeFiltering() throws Exception
    {
        recipientFilters.put("EMAIL", Collections.singletonList(recipientFilter));

        final List<Long> recips = Arrays.asList(1L, 2L, 3L);
        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(new NotificationBatch(NotificationType.LIKE_ACTIVITY, recips)));

                allowing(personsMapper).execute(recips);
                will(returnValue(Arrays.asList(person1, person2, person3)));

                allowing(recipientFilter).shouldFilter(with(equal(NotificationType.LIKE_ACTIVITY)),
                        with(same(person2)), with(any(Map.class)), with(equal("EMAIL")));
                will(returnValue(true));
                allowing(recipientFilter).shouldFilter(with(equal(NotificationType.LIKE_ACTIVITY)),
                        with(same(person3)), with(any(Map.class)), with(equal("EMAIL")));
                will(returnValue(false));

                oneOf(notifier).notify(with(equal(NotificationType.LIKE_ACTIVITY)),
                        with(equal(Collections.singletonList(3L))), with(any(Map.class)), with(any(Map.class)));
                will(returnValue(Collections.singletonList(new UserActionRequest("async", null, asyncRequest))));
            }
        });
        expectPrefsMapper(recips, "LIKE", new NotificationFilterPreferenceDTO(1L, "EMAIL", "LIKE"));

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertEquals(1, ac.getUserActionRequests().size());
        assertEquals("async", ac.getUserActionRequests().get(0).getActionKey());
    }

    /**
     * Tests execute.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testExecutePrefFilterAll() throws Exception
    {
        recipientFilters.put("EMAIL", Collections.singletonList(recipientFilter));

        final List<Long> recips = Arrays.asList(1L, 2L);
        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(new NotificationBatch(NotificationType.LIKE_ACTIVITY, recips)));

                allowing(personsMapper).execute(recips);
                will(returnValue(Arrays.asList(person1, person2)));
            }
        });
        expectPrefsMapper(recips, "LIKE", new NotificationFilterPreferenceDTO(1L, "EMAIL", "LIKE"),
                new NotificationFilterPreferenceDTO(2L, "EMAIL", "LIKE"));

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /**
     * Tests execute.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testExecuteCoverageNotifierException() throws Exception
    {
        final List<Long> recips = Collections.singletonList(1L);
        final CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.COMMENT, 0);
        context.checking(new Expectations()
        {
            {
                oneOf(translator).translate(request);
                will(returnValue(new NotificationBatch(NotificationType.PASS_THROUGH, 1L)));

                allowing(personsMapper).execute(recips);
                will(returnValue(Collections.singletonList(person1)));

                oneOf(notifier).notify(with(equal(NotificationType.PASS_THROUGH)), with(equal(recips)),
                        with(any(Map.class)), with(any(Map.class)));
                will(throwException(new Exception("BAD")));
            }
        });

        TaskHandlerActionContext<ActionContext> ac = TestContextCreator.createTaskHandlerAsyncContext(request);
        sut.execute(ac);

        context.assertIsSatisfied();
        assertTrue(ac.getUserActionRequests().isEmpty());
    }

    /* ------------ helper functions ------------ */

    /**
     * Prepares the expectation for invocation of the preferences mapper.
     *
     * @param recips
     *            Expected Recipients.
     * @param category
     *            Expected category.
     * @param results
     *            Returned prefs.
     */
    private void expectPrefsMapper(final List<Long> recips, final String category,
            final NotificationFilterPreferenceDTO... results)
    {
        context.checking(new Expectations()
        {
            {
                allowing(preferencesMapper).execute(with(new EasyMatcher<GetNotificationFilterPreferenceRequest>()
                {
                    @Override
                    protected boolean isMatch(final GetNotificationFilterPreferenceRequest inTestObject)
                    {
                        return TestHelper.containsExactly(inTestObject.getPersonIds(), recips)
                                && inTestObject.getCategories().size() == 1
                                && category.equals(inTestObject.getCategories().iterator().next());
                    }
                }));
                will(returnValue(Arrays.asList(results)));
            }
        });
    }
}
