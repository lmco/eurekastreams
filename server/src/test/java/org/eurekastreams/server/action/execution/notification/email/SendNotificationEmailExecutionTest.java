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
package org.eurekastreams.server.action.execution.notification.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.execution.email.NotificationEmailDTO;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests SendNotificationEmailExecution.
 */
public class SendNotificationEmailExecutionTest
{
    /** Test data. */
    private static final String SUBJECT = "The subject";

    /** Test data. */
    private static final String TEXT_BODY = "Text body";

    /** Test data. */
    private static final String HTML_BODY = "HTML body";

    /** Test data. */
    private static final String TO_RECIPIENT = "jdoe@eurekastreams.org";

    /** Test data. */
    private static final String BCC_RECIPIENTS = "abc@eurekastreams.org;smith@eurekastreams.org";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: For sending email. */
    private final EmailerFactory emailer = context.mock(EmailerFactory.class);

    /** Fixture: message. */
    private final MimeMessage message = context.mock(MimeMessage.class);

    /** Fixture: request. */
    private NotificationEmailDTO request;

    /** SUT. */
    private SendNotificationEmailExecution sut;

    /**
     * Setup before each test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Before
    public void setUp() throws MessagingException
    {
        sut = new SendNotificationEmailExecution(emailer);
        request = new NotificationEmailDTO();
        request.setSubject(SUBJECT);
        request.setTextBody(TEXT_BODY);
        request.setHtmlBody(HTML_BODY);
        request.setDescription("whatever");

        context.checking(new Expectations()
        {
            {
                oneOf(emailer).createMessage();
                will(returnValue(message));

                oneOf(emailer).setSubject(message, SUBJECT);
                oneOf(emailer).setTextBody(message, TEXT_BODY);
                oneOf(emailer).setHtmlBody(message, HTML_BODY);
            }
        });
    }

    /**
     * Tests execute.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecute() throws MessagingException
    {
        request.setToRecipient(TO_RECIPIENT);
        request.setBccRecipients(BCC_RECIPIENTS);
        request.setHighPriority(true);
        final String replyTo = "replyto@eurekastreams.org";
        request.setReplyTo(replyTo);

        context.checking(new Expectations()
        {
            {
                oneOf(emailer).setTo(message, TO_RECIPIENT);
                oneOf(emailer).setBcc(message, BCC_RECIPIENTS);
                oneOf(emailer).setReplyTo(message, replyTo);
                oneOf(message).addHeader("Importance", "high");
                oneOf(message).addHeader("X-Priority", "1");
                oneOf(emailer).sendMail(with(same(message)));
            }
        });

        sut.execute(TestContextCreator.createPrincipalActionContext(request, null));

        context.assertIsSatisfied();
    }

    /**
     * Tests execute.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteBlankRecipients() throws MessagingException
    {
        request.setToRecipient("");
        request.setBccRecipients("");

        context.checking(new Expectations()
        {
            {
                oneOf(emailer).sendMail(with(same(message)));
            }
        });

        sut.execute(TestContextCreator.createPrincipalActionContext(request, null));

        context.assertIsSatisfied();
    }

    /**
     * Tests execute.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteNullRecipients() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).sendMail(with(same(message)));
            }
        });

        sut.execute(TestContextCreator.createPrincipalActionContext(request, null));

        context.assertIsSatisfied();
    }

    /**
     * Tests execute.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteException() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).sendMail(with(same(message)));
                will(throwException(new MessagingException("BAD")));
            }
        });

        sut.execute(TestContextCreator.createPrincipalActionContext(request, null));

        context.assertIsSatisfied();
    }
}
