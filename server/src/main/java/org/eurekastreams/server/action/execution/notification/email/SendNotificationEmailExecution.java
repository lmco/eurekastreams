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

import java.io.Serializable;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.execution.email.NotificationEmailDTO;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to send notification emails.
 */
public class SendNotificationEmailExecution implements ExecutionStrategy<ActionContext>
{
    /** Log. */
    private static Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** For sending email. */
    private final EmailerFactory emailer;

    /**
     * Constructor.
     *
     * @param inEmailer
     *            For sending email.
     */
    public SendNotificationEmailExecution(final EmailerFactory inEmailer)
    {
        emailer = inEmailer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        NotificationEmailDTO request = (NotificationEmailDTO) inActionContext.getParams();

        try
        {
            MimeMessage message = emailer.createMessage();

            emailer.setSubject(message, request.getSubject());
            emailer.setTextBody(message, request.getTextBody());
            emailer.setHtmlBody(message, request.getHtmlBody());
            if (request.getToRecipient() != null && !request.getToRecipient().isEmpty())
            {
                emailer.setTo(message, request.getToRecipient());
            }
            if (request.getBccRecipients() != null && !request.getBccRecipients().isEmpty())
            {
                emailer.setBcc(message, request.getBccRecipients());
            }
            emailer.sendMail(message);

            log.debug("Sent email for notification {}", request.getDescription());
        }
        catch (MessagingException ex)
        {
            String msg = "Failed to send email message for notification " + request.getDescription();
            log.error(msg, ex);
            throw new ExecutionException(msg, ex);
        }

        return null;
    }
}
