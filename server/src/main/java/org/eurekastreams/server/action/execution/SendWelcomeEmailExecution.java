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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.SendWelcomeEmailRequest;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;

/**
 * Execution strategy for sending a new user welcome email.
 */
public class SendWelcomeEmailExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * The logger.
     */
    Log log = LogFactory.make();

    /**
     * Emailer factory that sends email.
     */
    private EmailerFactory emailerFactory;

    /**
     * Base URL for the application.
     */
    private String baseUrl;

    /**
     * Subject of welcome email.
     */
    private String subject;

    /**
     * Body of welcome email.
     */
    private String body;

    /**
     * Constructor.
     * 
     * @param inEmailerFactory
     *            {@link EmailerFactory}.
     * @param inBaseUrl
     *            base system url for generating links.
     * @param inSubject
     *            subject of welcome email.
     * @param inBody
     *            body of welcome email.
     */
    public SendWelcomeEmailExecution(final EmailerFactory inEmailerFactory, final String inBaseUrl,
            final String inSubject, final String inBody)
    {
        emailerFactory = inEmailerFactory;
        baseUrl = inBaseUrl;
        subject = inSubject;
        body = inBody;
    }

    /**
     * Increase an organizations employee count by a given amount.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return True if successful.
     */
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        SendWelcomeEmailRequest request = (SendWelcomeEmailRequest) inActionContext.getParams();
        String emailAddress = request.getEmailAddress();
        String accountId = request.getAccountId();

        if (emailAddress != null && emailAddress != "")
        {
            try
            {
                MimeMessage msg = emailerFactory.createMessage();
                emailerFactory.setTo(msg, emailAddress);
                emailerFactory.setSubject(msg, subject);
                emailerFactory.setHtmlBody(msg, body.replace("$(url)", baseUrl + "#people/" + accountId));
                emailerFactory.sendMail(msg);

                if (log.isInfoEnabled())
                {
                    log.info("New user email sent to: " + emailAddress);
                }
            }
            catch (MessagingException ex)
            {
                log.error("Failed to send new user email to: " + emailAddress);
            }
        }
        return Boolean.TRUE;
    }
}
