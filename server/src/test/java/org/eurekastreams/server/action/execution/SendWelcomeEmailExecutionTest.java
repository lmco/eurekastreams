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
package org.eurekastreams.server.action.execution;

import javax.mail.internet.MimeMessage;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.SendWelcomeEmailRequest;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing SendWelcomeEmailExecution class.
 */
public class SendWelcomeEmailExecutionTest extends MapperTest
{
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
     * System under test.
     */
    private SendWelcomeEmailExecution sut;

    /**
     * {@link EmailerFactory} mock.
     */
    private EmailerFactory emailerFactory = context.mock(EmailerFactory.class);

    /**
     * {@link MimeMessage} mock.
     */
    private MimeMessage message = context.mock(MimeMessage.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {

        sut = new SendWelcomeEmailExecution(emailerFactory, "http://localhost/", "welcome subject",
                "welcome body with $(url)");
    }

    /**
     * Test execute method.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testExecute() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new SendWelcomeEmailRequest("somebody@example.com", "somebody1")));

                oneOf(emailerFactory).createMessage();
                will(returnValue(message));

                oneOf(emailerFactory).setTo(message, "somebody@example.com");
                oneOf(emailerFactory).setSubject(message, "welcome subject");
                oneOf(emailerFactory).setHtmlBody(message, "welcome body with http://localhost/#people/somebody1");
                oneOf(emailerFactory).sendMail(message);
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
