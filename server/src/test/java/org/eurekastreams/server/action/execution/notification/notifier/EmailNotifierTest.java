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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.email.NotificationEmailDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
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
    private static final Long RECIPIENT1 = 50L;

    /** Test data. */
    private static final Long RECIPIENT2 = 52L;

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
    private static final NotificationType OK_TYPE = NotificationType.COMMENT_TO_COMMENTED_POST;

    /** Test data. */
    private static final String EMAIL1 = "person1@eurekastreams.org";

    /** Test data. */
    private static final String EMAIL2 = "person2@eurekastreams.org";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine = context.mock(VelocityEngine.class);

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext = context.mock(Context.class);

    /** Fixture: velocity template. */
    private final Template textBodyTemplate = context.mock(Template.class, "textBodyTemplate");

    /** Fixture: velocity template. */
    private final Template htmlBodyTemplate = context.mock(Template.class, "htmlBodyTemplate");

    /** To fetch people for email addresses. */
    private final DomainMapper<List<Long>, List<PersonModelView>> personsMapper = context.mock(DomainMapper.class,
            "personsMapper");

    /** Dummy person. */
    private final PersonModelView person1 = context.mock(PersonModelView.class, "person1");

    /** Dummy person. */
    private final PersonModelView person2 = context.mock(PersonModelView.class, "person2");

    /** SUT. */
    private EmailNotifier sut;

    /** Templates. */
    private final Map<NotificationType, EmailNotificationTemplate> templates;

    /** Recipients. */
    private final Collection<Long> recipients = Collections.unmodifiableList(Arrays.asList(RECIPIENT1, RECIPIENT2));

    /**
     * One-time setup.
     */
    public EmailNotifierTest()
    {
        EmailNotificationTemplate template = new EmailNotificationTemplate();
        template.setHtmlBody(HTML_BODY_RESOURCE);
        template.setSubject(SUBJECT_TEMPLATE);
        template.setTextBody(TEXT_BODY_RESOURCE);

        templates = Collections.unmodifiableMap(Collections.singletonMap(OK_TYPE, template));
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new EmailNotifier(velocityEngine, velocityGlobalContext, templates, personsMapper, PREFIX);
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
        UserActionRequest result = sut
                .notify(NotificationType.POST_TO_GROUP_STREAM, recipients, Collections.EMPTY_MAP);

        context.assertIsSatisfied();

        assertNull(result);
    }

    /**
     * Common setup for rendering tests.
     */
    private void commonSetup()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(velocityEngine).getTemplate(HTML_BODY_RESOURCE);
                will(returnValue(htmlBodyTemplate));

                oneOf(velocityEngine).getTemplate(TEXT_BODY_RESOURCE);
                will(returnValue(textBodyTemplate));

                oneOf(velocityEngine).evaluate(with(any(VelocityContext.class)), with(any(StringWriter.class)),
                        with(equal("EmailSubject-COMMENT_TO_COMMENTED_POST")), with(equal(SUBJECT_TEMPLATE)));
                will(new Action()
                {
                    @Override
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        ((StringWriter) inv.getParameter(1)).append(SUBJECT_RENDERED);
                        return true;
                    }

                    @Override
                    public void describeTo(final Description arg0)
                    {
                    }
                });

                oneOf(textBodyTemplate).merge(with(any(VelocityContext.class)), with(any(StringWriter.class)));
                will(new Action()
                {
                    @Override
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        ((StringWriter) inv.getParameter(1)).append(TEXT_BODY_RENDERED);
                        return null;
                    }

                    @Override
                    public void describeTo(final Description arg0)
                    {
                    }
                });

                oneOf(htmlBodyTemplate).merge(with(any(VelocityContext.class)), with(any(StringWriter.class)));
                will(new Action()
                {
                    @Override
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        ((StringWriter) inv.getParameter(1)).append(HTML_BODY_RENDERED);
                        return null;
                    }

                    @Override
                    public void describeTo(final Description arg0)
                    {
                    }
                });

                allowing(person1).getEmail();
                will(returnValue(EMAIL1));

                allowing(person2).getEmail();
                will(returnValue(EMAIL2));
            }
        });

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

        context.checking(new Expectations()
        {
            {
                allowing(personsMapper).execute(Collections.singletonList(RECIPIENT1));
                will(returnValue(Collections.singletonList(person1)));
            }
        });

        UserActionRequest result = sut.notify(OK_TYPE, Collections.singletonList(RECIPIENT1), Collections.EMPTY_MAP);
        context.assertIsSatisfied();

        assertNotNull(result);
        assertEquals("sendEmailNotificationAction", result.getActionKey());
        NotificationEmailDTO request = (NotificationEmailDTO) result.getParams();
        assertEquals(HTML_BODY_RENDERED, request.getHtmlBody());
        assertEquals(TEXT_BODY_RENDERED, request.getTextBody());
        assertEquals(PREFIX + SUBJECT_RENDERED, request.getSubject());
        assertEquals(EMAIL1, request.getToRecipient());
        assertTrue(request.getBccRecipients() == null || request.getBccRecipients().isEmpty());
        assertTrue(request.getDescription() != null && !request.getDescription().isEmpty());
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

        context.checking(new Expectations()
        {
            {
                allowing(personsMapper).execute(Arrays.asList(RECIPIENT1, RECIPIENT2));
                will(returnValue(Arrays.asList(person1, person2)));
            }
        });

        UserActionRequest result = sut.notify(OK_TYPE, Arrays.asList(RECIPIENT1, RECIPIENT2), Collections.EMPTY_MAP);
        context.assertIsSatisfied();

        assertNotNull(result);
        assertEquals("sendEmailNotificationAction", result.getActionKey());
        NotificationEmailDTO request = (NotificationEmailDTO) result.getParams();
        assertEquals(HTML_BODY_RENDERED, request.getHtmlBody());
        assertEquals(TEXT_BODY_RENDERED, request.getTextBody());
        assertEquals(PREFIX + SUBJECT_RENDERED, request.getSubject());
        assertTrue(request.getToRecipient() == null || request.getToRecipient().isEmpty());
        assertEquals(EMAIL1 + "," + EMAIL2, request.getBccRecipients());
        assertTrue(request.getDescription() != null && !request.getDescription().isEmpty());
    }

}
