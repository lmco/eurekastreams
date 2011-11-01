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
package org.eurekastreams.server.action.execution.notification.notifier;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.email.NotificationEmailDTO;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.execution.notification.notifier.EmailNotificationTemplate.ReplyAction;
import org.eurekastreams.server.domain.HasEmail;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.email.TokenContentEmailAddressBuilder;
import org.eurekastreams.server.service.email.TokenContentFormatter;
import org.eurekastreams.server.service.utility.authorization.ActivityInteractionAuthorizationStrategy;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests EmailNotifier.
 */
public class EmailNotifierTest
{
    /** Test data. */
    private static final String PREFIX = "Subject Prefix: ";

    /** Test data. */
    private static final Long RECIPIENT1 = 51L;

    /** Test data. */
    private static final Long RECIPIENT2 = 52L;

    /** Test data. */
    private static final Long RECIPIENT3 = 53L;

    /** Test data. */
    private static final String SUBJECT_TEMPLATE = "This is the subject template";

    /** Test data. */
    private static final String TEXT_BODY_RESOURCE = "This is the text body resource path";

    /** Test data. */
    private static final String HTML_BODY_RESOURCE = "This is the HTML body resource path";

    /** Test data. */
    private static final String SUBJECT_RENDERED = "This is the rendered subject template";

    /** Test data. */
    private static final String TEXT_BODY_RENDERED = "This is the rendered text body template";

    /** Test data. */
    private static final String HTML_BODY_RENDERED = "This is the rendered HTML body template";

    /** Test data. */
    private static final NotificationType NOTIFICATION_TYPE_NO_REPLY = NotificationType.COMMENT_TO_COMMENTED_POST;

    /** Test data. */
    private static final NotificationType NOTIFICATION_TYPE_TOKEN = NotificationType.COMMENT_TO_PERSONAL_POST;

    /** Test data. */
    private static final NotificationType NOTIFICATION_TYPE_ACTOR_REPLY = NotificationType.FOLLOW_PERSON;

    /** Test data. */
    private static final String EMAIL1 = "person1@eurekastreams.org";

    /** Test data. */
    private static final String EMAIL2 = "person2@eurekastreams.org";

    /** Test data. */
    private static final long ACTIVITY_ID = 80L;

    /** Test data. */
    private static final String TOKEN_CONTENT = "Stuff in the token";

    /** Used for mocking objects. */
    private final JUnit4Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine = mockery.mock(VelocityEngine.class);

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext = mockery.mock(Context.class);

    /** Fixture: velocity template. */
    private final Template textBodyTemplate = mockery.mock(Template.class, "textBodyTemplate");

    /** Fixture: velocity template. */
    private final Template htmlBodyTemplate = mockery.mock(Template.class, "htmlBodyTemplate");

    /** Dummy person. */
    private final PersonModelView person1 = mockery.mock(PersonModelView.class, "person1");

    /** Dummy person. */
    private final PersonModelView person2 = mockery.mock(PersonModelView.class, "person2");

    /** Dummy person. */
    private final PersonModelView person1a = mockery.mock(PersonModelView.class, "person1a");

    /** Dummy person. */
    private final PersonModelView person2a = mockery.mock(PersonModelView.class, "person2a");

    /** Dummy person. */
    private final PersonModelView person3a = mockery.mock(PersonModelView.class, "person3a");

    /** Fixture: activity. */
    private final ActivityDTO activity = mockery.mock(ActivityDTO.class, "activity");

    /** SUT. */
    private EmailNotifier sut;

    /** Templates. */
    private final Map<NotificationType, EmailNotificationTemplate> templates;

    /** Recipients. */
    private final Collection<Long> recipients = Collections.unmodifiableList(Arrays.asList(RECIPIENT1, RECIPIENT2,
            RECIPIENT3));

    /** Recipient index. */
    private final Map<Long, PersonModelView> recipientIndex = new HashMap<Long, PersonModelView>();
    /** Builds the token content. */
    private final TokenContentFormatter tokenContentFormatter = mockery.mock(TokenContentFormatter.class,
            "tokenContentFormatter");

    /** Builds the recipient email address with a token. */
    private final TokenContentEmailAddressBuilder tokenAddressBuilder = mockery.mock(
            TokenContentEmailAddressBuilder.class, "tokenAddressBuilder");

    /** For determining if users can comment on an activity. */
    private final ActivityInteractionAuthorizationStrategy activityAuthorizer = mockery
            .mock(ActivityInteractionAuthorizationStrategy.class);

    /**
     * One-time setup.
     */
    public EmailNotifierTest()
    {
        Map map = new HashMap(2);

        EmailNotificationTemplate template = new EmailNotificationTemplate();
        template.setHtmlBody(HTML_BODY_RESOURCE);
        template.setSubject(SUBJECT_TEMPLATE);
        template.setTextBody(TEXT_BODY_RESOURCE);
        map.put(NOTIFICATION_TYPE_NO_REPLY, template);
        template = new EmailNotificationTemplate();
        template.setHtmlBody(HTML_BODY_RESOURCE);
        template.setSubject(SUBJECT_TEMPLATE);
        template.setTextBody(TEXT_BODY_RESOURCE);
        template.setReplyAddressType(ReplyAction.ACTOR);
        map.put(NOTIFICATION_TYPE_ACTOR_REPLY, template);
        template = new EmailNotificationTemplate();
        template.setHtmlBody(HTML_BODY_RESOURCE);
        template.setSubject(SUBJECT_TEMPLATE);
        template.setTextBody(TEXT_BODY_RESOURCE);
        template.setReplyAddressType(ReplyAction.COMMENT);
        map.put(NOTIFICATION_TYPE_TOKEN, template);

        templates = Collections.unmodifiableMap(map);
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new EmailNotifier(velocityEngine, velocityGlobalContext, templates, PREFIX, tokenContentFormatter,
                tokenAddressBuilder, activityAuthorizer);

        recipientIndex.clear();
        recipientIndex.put(RECIPIENT1, person1);
        recipientIndex.put(RECIPIENT2, person2);
        recipientIndex.put(RECIPIENT3, person3a);
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyUnknownTemplate() throws Exception
    {
        Collection<UserActionRequest> result = sut.notify(NotificationType.PASS_THROUGH, recipients,
                Collections.EMPTY_MAP, recipientIndex);

        mockery.assertIsSatisfied();

        assertNull(result);
    }

    /**
     * Common setup for rendering tests.
     */
    private void commonSetup()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(velocityEngine).getTemplate(HTML_BODY_RESOURCE);
                will(returnValue(htmlBodyTemplate));

                allowing(velocityEngine).getTemplate(TEXT_BODY_RESOURCE);
                will(returnValue(textBodyTemplate));

                allowing(velocityEngine).evaluate(with(any(VelocityContext.class)), with(any(StringWriter.class)),
                        with(any(String.class)), with(equal(SUBJECT_TEMPLATE)));
                will(new CustomAction("Render subject")
                {
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        ((StringWriter) inv.getParameter(1)).append(SUBJECT_RENDERED);
                        return true;
                    }
                });

                allowing(textBodyTemplate).merge(with(any(VelocityContext.class)), with(any(StringWriter.class)));
                will(new CustomAction("Render text body")
                {
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        ((StringWriter) inv.getParameter(1)).append(TEXT_BODY_RENDERED);
                        return null;
                    }
                });

                allowing(htmlBodyTemplate).merge(with(any(VelocityContext.class)), with(any(StringWriter.class)));
                will(new CustomAction("Render HTML body")
                {
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        ((StringWriter) inv.getParameter(1)).append(HTML_BODY_RENDERED);
                        return null;
                    }
                });

                allowing(person1).getEmail();
                will(returnValue(EMAIL1));

                allowing(person2).getEmail();
                will(returnValue(EMAIL2));

                allowing(person1a).getEmail();
                will(returnValue(null));

                allowing(person2a).getEmail();
                will(returnValue(""));

                allowing(person3a).getEmail();
                will(returnValue(" "));
            }
        });

    }

    /**
     * Helper: asserts the result is a non-null collection containing a single UserActionRequest for sending email.
     *
     * @param results
     *            Result list.
     * @return Param data from the result.
     */
    private NotificationEmailDTO assertGetSingleResult(final Collection<UserActionRequest> results)
    {
        assertNotNull(results);
        assertEquals(1, results.size());
        UserActionRequest result = results.iterator().next();
        assertNotNull(result);
        assertEquals("sendEmailNotificationAction", result.getActionKey());
        return (NotificationEmailDTO) result.getParams();
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyTokenNone() throws Exception
    {
        commonSetup();

        mockery.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(ACTIVITY_ID));

                allowing(tokenContentFormatter).buildForActivity(ACTIVITY_ID);
                will(returnValue(TOKEN_CONTENT));

                allowing(activityAuthorizer).authorize(activity, ActivityInteractionType.COMMENT, false);
                will(returnValue(true));
            }
        });

        recipientIndex.put(RECIPIENT1, person1a);
        recipientIndex.put(RECIPIENT2, person2a);

        Collection<UserActionRequest> result = sut.notify(NOTIFICATION_TYPE_TOKEN, recipients,
                Collections.singletonMap("activity", (Object) activity), recipientIndex);
        mockery.assertIsSatisfied();

        assertNull(result);
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyNone() throws Exception
    {
        commonSetup();

        recipientIndex.put(RECIPIENT1, person1a);
        recipientIndex.put(RECIPIENT2, person2a);

        Collection<UserActionRequest> result = sut.notify(NOTIFICATION_TYPE_NO_REPLY, recipients,
                Collections.EMPTY_MAP, recipientIndex);
        mockery.assertIsSatisfied();

        assertNull(result);
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyOne() throws Exception
    {
        commonSetup();

        recipientIndex.put(RECIPIENT2, person2a);

        Collection<UserActionRequest> results = sut.notify(NOTIFICATION_TYPE_NO_REPLY, recipients,
                Collections.EMPTY_MAP, recipientIndex);
        mockery.assertIsSatisfied();

        NotificationEmailDTO request = assertGetSingleResult(results);
        assertEquals(HTML_BODY_RENDERED, request.getHtmlBody());
        assertEquals(TEXT_BODY_RENDERED, request.getTextBody());
        assertEquals(PREFIX + SUBJECT_RENDERED, request.getSubject());
        assertEquals(EMAIL1, request.getToRecipient());
        assertTrue(request.getBccRecipients() == null || request.getBccRecipients().isEmpty());
        assertTrue(request.getDescription() != null && !request.getDescription().isEmpty());
        assertFalse(request.isHighPriority());
        assertNull(request.getReplyTo());
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyMultiple() throws Exception
    {
        commonSetup();

        final Object actorEmail = "actor@eurekastreams.org";
        final HasEmail actor = mockery.mock(HasEmail.class, "actor");
        mockery.checking(new Expectations()
        {
            {
                allowing(actor).getEmail();
                will(returnValue(actorEmail));
            }
        });

        Collection<UserActionRequest> results = sut.notify(NOTIFICATION_TYPE_ACTOR_REPLY, recipients,
                Collections.singletonMap(NotificationPropertyKeys.ACTOR, (Object) actor), recipientIndex);
        mockery.assertIsSatisfied();

        NotificationEmailDTO request = assertGetSingleResult(results);
        assertEquals(HTML_BODY_RENDERED, request.getHtmlBody());
        assertEquals(TEXT_BODY_RENDERED, request.getTextBody());
        assertEquals(PREFIX + SUBJECT_RENDERED, request.getSubject());
        assertTrue(request.getToRecipient() == null || request.getToRecipient().isEmpty());
        assertEquals(EMAIL1 + "," + EMAIL2, request.getBccRecipients());
        assertEquals(actorEmail, request.getReplyTo());
        assertTrue(request.getDescription() != null && !request.getDescription().isEmpty());
        assertFalse(request.isHighPriority());
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyMultipleToken() throws Exception
    {
        commonSetup();

        final String person1Reply = "system+ABC@eurekastreams.org";
        final String person2Reply = "system+DEF@eurekastreams.org";
        mockery.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(ACTIVITY_ID));

                allowing(tokenContentFormatter).buildForActivity(ACTIVITY_ID);
                will(returnValue(TOKEN_CONTENT));

                allowing(tokenAddressBuilder).build(TOKEN_CONTENT, RECIPIENT1);
                will(returnValue(person1Reply));
                allowing(tokenAddressBuilder).build(TOKEN_CONTENT, RECIPIENT2);
                will(returnValue(person2Reply));

                allowing(activityAuthorizer).authorize(activity, ActivityInteractionType.COMMENT, false);
                will(returnValue(true));
            }
        });

        Collection<UserActionRequest> results = sut.notify(NOTIFICATION_TYPE_TOKEN, recipients,
                Collections.singletonMap("activity", (Object) activity), recipientIndex);
        mockery.assertIsSatisfied();

        assertNotNull(results);
        assertEquals(2, results.size());
        List<String> addresses = Arrays.asList(EMAIL1, EMAIL2);
        List<String> replyAddresses = Arrays.asList(person1Reply, person2Reply);
        for (UserActionRequest result : results)
        {
            assertNotNull(result);
            assertEquals("sendEmailNotificationAction", result.getActionKey());
            NotificationEmailDTO request = (NotificationEmailDTO) result.getParams();
            assertEquals(HTML_BODY_RENDERED, request.getHtmlBody());
            assertEquals(TEXT_BODY_RENDERED, request.getTextBody());
            assertEquals(PREFIX + SUBJECT_RENDERED, request.getSubject());
            assertTrue(addresses.contains(request.getToRecipient()));
            assertTrue(request.getBccRecipients() == null || request.getBccRecipients().isEmpty());
            assertTrue(replyAddresses.contains(request.getReplyTo()));
            assertTrue(request.getDescription() != null && !request.getDescription().isEmpty());
            assertFalse(request.isHighPriority());
        }
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyMultipleTokenSplitPermissions() throws Exception
    {
        commonSetup();

        final String person1Reply = "system+ABC@eurekastreams.org";
        mockery.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(ACTIVITY_ID));

                allowing(tokenContentFormatter).buildForActivity(ACTIVITY_ID);
                will(returnValue(TOKEN_CONTENT));

                allowing(tokenAddressBuilder).build(TOKEN_CONTENT, RECIPIENT1);
                will(returnValue(person1Reply));

                allowing(activityAuthorizer).authorize(activity, ActivityInteractionType.COMMENT, false);
                will(returnValue(false));

                allowing(activityAuthorizer).authorize(RECIPIENT1, activity, ActivityInteractionType.COMMENT);
                will(returnValue(true));

                allowing(activityAuthorizer).authorize(RECIPIENT2, activity, ActivityInteractionType.COMMENT);
                will(returnValue(false));
            }
        });

        Collection<UserActionRequest> results = sut.notify(NOTIFICATION_TYPE_TOKEN, recipients,
                Collections.singletonMap("activity", (Object) activity), recipientIndex);
        mockery.assertIsSatisfied();

        assertNotNull(results);
        assertEquals(2, results.size());
        for (UserActionRequest result : results)
        {
            assertNotNull(result);
            assertEquals("sendEmailNotificationAction", result.getActionKey());
            NotificationEmailDTO request = (NotificationEmailDTO) result.getParams();
            assertEquals(HTML_BODY_RENDERED, request.getHtmlBody());
            assertEquals(TEXT_BODY_RENDERED, request.getTextBody());
            assertEquals(PREFIX + SUBJECT_RENDERED, request.getSubject());
            assertTrue(request.getBccRecipients() == null || request.getBccRecipients().isEmpty());
            assertTrue(request.getDescription() != null && !request.getDescription().isEmpty());
            assertFalse(request.isHighPriority());

            // can't be sure of the order, so make sure to and reply-to match up
            final String toRecipient = request.getToRecipient();
            if (EMAIL1.equals(toRecipient))
            {
                assertEquals(person1Reply, request.getReplyTo());
            }
            else if (EMAIL2.equals(toRecipient))
            {
                assertNull(request.getReplyTo());
            }
            else
            {
                fail("Unexpected recipient " + toRecipient);
            }
        }
    }

    // ---- MISCELLANY TESTS -----

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyHighPriority() throws Exception
    {
        commonSetup();

        Collection<UserActionRequest> results = sut.notify(NOTIFICATION_TYPE_NO_REPLY,
                Collections.singletonList(RECIPIENT1),
                Collections.singletonMap(NotificationPropertyKeys.HIGH_PRIORITY, (Object) true), recipientIndex);
        mockery.assertIsSatisfied();

        NotificationEmailDTO request = assertGetSingleResult(results);
        assertTrue(request.isHighPriority());
    }

    // ----- ANOMALY TESTS -----

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test(expected = ExecutionException.class)
    public void testNotifyMultipleTokenMissingActivity() throws Exception
    {
        commonSetup();

        sut.notify(NOTIFICATION_TYPE_TOKEN, recipients, Collections.EMPTY_MAP, recipientIndex);
        mockery.assertIsSatisfied();
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test(expected = ExecutionException.class)
    public void testNotifyTokenActivityWrongType() throws Exception
    {
        commonSetup();

        sut.notify(NOTIFICATION_TYPE_TOKEN, recipients, Collections.singletonMap("activity", new Object()),
                recipientIndex);
        mockery.assertIsSatisfied();
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyActorWrongType() throws Exception
    {
        commonSetup();

        Collection<UserActionRequest> results = sut.notify(NOTIFICATION_TYPE_ACTOR_REPLY, recipients,
                Collections.singletonMap(NotificationPropertyKeys.ACTOR, (Object) "WRONG!"), recipientIndex);
        mockery.assertIsSatisfied();

        NotificationEmailDTO request = assertGetSingleResult(results);
        assertNull(request.getReplyTo());
    }

    /**
     * Tests notify.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotifyActorBadEmail() throws Exception
    {
        commonSetup();

        final HasEmail actor = mockery.mock(HasEmail.class, "actor");
        mockery.checking(new Expectations()
        {
            {
                allowing(actor).getEmail();
                will(returnValue(" "));
            }
        });

        Collection<UserActionRequest> results = sut.notify(NOTIFICATION_TYPE_ACTOR_REPLY, recipients,
                Collections.singletonMap(NotificationPropertyKeys.ACTOR, (Object) "WRONG!"), recipientIndex);
        mockery.assertIsSatisfied();

        NotificationEmailDTO request = assertGetSingleResult(results);
        assertNull(request.getReplyTo());
    }
}
