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

import java.io.Serializable;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;

/**
 * Builds and sends an email for a notification.
 */
public class CreateEmailNotificationExecution implements ExecutionStrategy<ActionContext>
{
    /** For sending email. */
    private EmailerFactory emailer;

    /** For creating the content of the email for each kind of notification. */
    private Map<NotificationType, NotificationEmailBuilder> builders;

    /** Log. */
    private final Log log = LogFactory.make();

    /**
     * Constructor.
     *
     * @param inEmailer
     *            For sending email.
     * @param inBuilders
     *            For creating the content of the email for each kind of notification.
     */
    public CreateEmailNotificationExecution(final EmailerFactory inEmailer,
            final Map<NotificationType, NotificationEmailBuilder> inBuilders)
    {
        emailer = inEmailer;
        builders = inBuilders;
    }

    /**
     * {@inheritDoc}.
     *
     * Takes a single request paramater of type {@link NotificationDTO} to build an email for the notification.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        NotificationDTO notification = (NotificationDTO) inActionContext.getParams();
        NotificationType type = notification.getType();
        MimeMessage message;

        try
        {
            NotificationEmailBuilder builder = builders.get(type);
            if (builder == null)
            {
                throw new Exception("No email builder for notification type " + type);
            }

            message = emailer.createMessage();

            builder.build(notification, message);

            emailer.sendMail(message);
        }
        catch (Exception ex)
        {
            log.error("Failed to send email message for notification " + type, ex);
            throw new ExecutionException("Failed to send email message for notification " + type, ex);
        }

        return null;
    }

}
