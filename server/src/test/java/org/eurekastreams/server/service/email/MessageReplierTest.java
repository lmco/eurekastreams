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
package org.eurekastreams.server.service.email;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.eurekastreams.server.support.email.EmailTemplate;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests MessageReplier.
 */
public class MessageReplierTest
{
    /** Test data. */
    private static final String USER_EMAIL = "somebody@eurekastreams.org";

    /** Test data. */
    private static final String ACTION_NAME = "actionName";

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

    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** For creating response emails. */
    private final EmailerFactory emailerFactory = mockery.mock(EmailerFactory.class, "emailerFactory");

    /** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine = mockery.mock(VelocityEngine.class, "velocityEngine");

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext = mockery.mock(Context.class, "velocityGlobalContext");

    /** Templates for error response messages. */
    private final Map<String, EmailTemplate> errorMessageTemplates;

    /** Prepares exceptions for returning to the client. */
    private final Transformer<Exception, Exception> exceptionSanitizer = mockery.mock(Transformer.class,
            "exceptionSanitizer");
    /** Fixture: velocity template. */
    private final Template textBodyTemplate = mockery.mock(Template.class, "textBodyTemplate");

    /** Fixture: velocity template. */
    private final Template htmlBodyTemplate = mockery.mock(Template.class, "htmlBodyTemplate");

    /** Response message. */
    MimeMessage response = mockery.mock(MimeMessage.class, "response");

    /** Input mesage. */
    Message message = mockery.mock(Message.class, "message");

    /** User. */
    PersonModelView user = mockery.mock(PersonModelView.class, "user");

    /** Action params. */
    private final Serializable params = mockery.mock(Serializable.class, "params");

    /** Cause exception. */
    private final Exception actionException = mockery.mock(Exception.class, "actionException");

    /** SUT. */
    private MessageReplier sut;

    /** response messages. */
    private final List<Message> responseMessages = new ArrayList<Message>();

    /** Clean exception. */
    private final Exception cleanException = new GeneralException("Clean message");

    /**
     * One-time setup.
     */
    public MessageReplierTest()
    {
        Map<String, EmailTemplate> map = new HashMap<String, EmailTemplate>(2);

        EmailTemplate template = new EmailTemplate();
        template.setHtmlBody(HTML_BODY_RESOURCE);
        template.setSubject(SUBJECT_TEMPLATE);
        template.setTextBody(TEXT_BODY_RESOURCE);
        map.put(ACTION_NAME, template);
        template = new EmailTemplate();
        template.setHtmlBody(HTML_BODY_RESOURCE + "NotUsed");
        template.setSubject(SUBJECT_TEMPLATE + "NotUsed");
        template.setTextBody(TEXT_BODY_RESOURCE + "NotUsed");
        map.put(ACTION_NAME + "NotUsed", template);

        errorMessageTemplates = Collections.unmodifiableMap(map);
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        responseMessages.clear();
        sut = new MessageReplier(emailerFactory, velocityEngine, velocityGlobalContext, exceptionSanitizer,
                errorMessageTemplates);

        mockery.checking(new Expectations()
        {
            {
                allowing(user).getEmail();
                will(returnValue(USER_EMAIL));
            }
        });
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Shouldn't.
     */
    @Test
    public void testTemplateFound() throws MessagingException
    {
        UserActionRequest rqst = new UserActionRequest(ACTION_NAME, null, params);
        mockery.checking(new Expectations()
        {
            {
                allowing(exceptionSanitizer).transform(actionException);
                will(returnValue(cleanException));

                allowing(emailerFactory).createMessage();
                will(returnValue(response));

                oneOf(emailerFactory).setTo(response, USER_EMAIL);
                oneOf(emailerFactory).setSubject(response, SUBJECT_RENDERED);
                oneOf(emailerFactory).setTextBody(response, TEXT_BODY_RENDERED);
                oneOf(emailerFactory).setHtmlBody(response, HTML_BODY_RENDERED);
                oneOf(emailerFactory).addAttachmentMessage(response, message);

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
                        assertVelocityContext(inv);
                        ((StringWriter) inv.getParameter(1)).append(SUBJECT_RENDERED);
                        return true;
                    }
                });

                allowing(textBodyTemplate).merge(with(any(VelocityContext.class)), with(any(StringWriter.class)));
                will(new CustomAction("Render text body")
                {
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        assertVelocityContext(inv);
                        ((StringWriter) inv.getParameter(1)).append(TEXT_BODY_RENDERED);
                        return null;
                    }
                });

                allowing(htmlBodyTemplate).merge(with(any(VelocityContext.class)), with(any(StringWriter.class)));
                will(new CustomAction("Render HTML body")
                {
                    public Object invoke(final Invocation inv) throws Throwable
                    {
                        assertVelocityContext(inv);
                        ((StringWriter) inv.getParameter(1)).append(HTML_BODY_RENDERED);
                        return null;
                    }
                });
            }
        });

        sut.reply(message, user, rqst, actionException, responseMessages);
        mockery.assertIsSatisfied();
        assertEquals(1, responseMessages.size());
        assertSame(response, responseMessages.get(0));
    }

    /**
     * Asserts context was provided with appropriate data.
     *
     * @param inv
     *            Call invocation.
     */
    private void assertVelocityContext(final Invocation inv)
    {
        VelocityContext vc = (VelocityContext) inv.getParameter(0);
        assertNotSame(velocityGlobalContext, vc);

        assertEquals(ACTION_NAME, vc.get("action"));
        assertSame(params, vc.get("params"));
        assertSame(user, vc.get("user"));
        assertSame(cleanException, vc.get("exception"));
        assertEquals(actionException, vc.get("originalException"));
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Shouldn't.
     */
    @Test
    public void testTemplateNotFound() throws MessagingException
    {
        UserActionRequest rqst = new UserActionRequest("SomeOther" + ACTION_NAME, null, params);
        mockery.checking(new Expectations()
        {
            {
                allowing(exceptionSanitizer).transform(actionException);
                will(returnValue(cleanException));

                allowing(emailerFactory).createMessage();
                will(returnValue(response));

                oneOf(emailerFactory).setTo(response, USER_EMAIL);
                oneOf(emailerFactory).setSubject(with(same(response)), with(any(String.class)));
                oneOf(emailerFactory).setTextBody(with(same(response)), with(any(String.class)));
                allowing(emailerFactory).setHtmlBody(with(same(response)), with(any(String.class)));
                oneOf(emailerFactory).addAttachmentMessage(response, message);
            }
        });

        sut.reply(message, user, rqst, actionException, responseMessages);
        mockery.assertIsSatisfied();
        assertEquals(1, responseMessages.size());
        assertSame(response, responseMessages.get(0));
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Shouldn't.
     */
    @Test
    public void testException() throws MessagingException
    {
        UserActionRequest rqst = new UserActionRequest("SomeOther" + ACTION_NAME, null, params);
        mockery.checking(new Expectations()
        {
            {
                allowing(exceptionSanitizer).transform(actionException);
                will(returnValue(cleanException));

                allowing(emailerFactory).createMessage();
                will(throwException(new MessagingException()));
            }
        });

        sut.reply(message, user, rqst, actionException, responseMessages);
        mockery.assertIsSatisfied();
        assertTrue(responseMessages.isEmpty());
    }
}
