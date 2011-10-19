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
package org.eurekastreams.server.action.execution.notification.notifier;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeHtmlReference;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.email.NotificationEmailDTO;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.domain.HasEmail;
import org.eurekastreams.server.domain.HasId;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.email.TokenContentEmailAddressBuilder;
import org.eurekastreams.server.service.email.TokenContentFormatter;

/**
 * Notifier for in-app notifications. Builds the messages and stores them in the database.
 */
public class EmailNotifier implements Notifier
{
    /** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine;

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext;

    /** Message templates by notification type. */
    private final Map<NotificationType, EmailNotificationTemplate> templates;

    /** Prefix to use on email subjects. */
    private final String subjectPrefix;

    /** Builds the token content. */
    private final TokenContentFormatter tokenContentFormatter;

    /** Builds the recipient email address with a token. */
    private final TokenContentEmailAddressBuilder tokenAddressBuilder;

    /**
     * Constructor.
     *
     * @param inVelocityEngine
     *            Apache Velocity templating engine.
     * @param inVelocityGlobalContext
     *            Global context for Apache Velocity templating engine.
     * @param inTemplates
     *            Message templates by notification type.
     * @param inSubjectPrefix
     *            Prefix to use on email subjects.
     * @param inTokenContentFormatter
     *            Builds the token content.
     * @param inTokenAddressBuilder
     *            Builds the recipient email address with a token.
     */
    public EmailNotifier(final VelocityEngine inVelocityEngine, final Context inVelocityGlobalContext,
            final Map<NotificationType, EmailNotificationTemplate> inTemplates, final String inSubjectPrefix,
            final TokenContentFormatter inTokenContentFormatter,
            final TokenContentEmailAddressBuilder inTokenAddressBuilder)
    {
        velocityEngine = inVelocityEngine;
        velocityGlobalContext = inVelocityGlobalContext;
        templates = inTemplates;
        subjectPrefix = inSubjectPrefix;
        tokenContentFormatter = inTokenContentFormatter;
        tokenAddressBuilder = inTokenAddressBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserActionRequest> notify(final NotificationType inType, final Collection<Long> inRecipients,
            final Map<String, Object> inProperties, final Map<Long, PersonModelView> inRecipientIndex)
            throws Exception
    {
        EmailNotificationTemplate template = templates.get(inType);
        if (template == null)
        {
            // Not an error - this is an easy way to disable a given notification.
            return null;
        }

        NotificationEmailDTO email = new NotificationEmailDTO();

        Context velocityContext = new VelocityContext(new VelocityContext(inProperties, velocityGlobalContext));
        velocityContext.put("context", velocityContext);
        velocityContext.put("type", inType);
        if (inRecipients.size() == 1)
        {
            velocityContext.put("recipient", inRecipientIndex.get(inRecipients.iterator().next()));
        }

        // build the subject
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(velocityContext, writer, "EmailSubject-" + inType, template.getSubjectTemplate());
        email.setSubject(subjectPrefix + writer.toString());

        // build the text body
        writer.getBuffer().setLength(0);
        Template vt = velocityEngine.getTemplate(template.getTextBodyTemplateResourcePath());
        vt.merge(velocityContext, writer);
        email.setTextBody(writer.toString());

        // -- build the HTML body --
        // HTML-escape all content inserted
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(new EscapeHtmlReference());
        ec.attachToContext(velocityContext);

        writer.getBuffer().setLength(0);
        vt = velocityEngine.getTemplate(template.getHtmlBodyTemplateResourcePath());
        vt.merge(velocityContext, writer);
        email.setHtmlBody(writer.toString());

        // set the priority
        email.setHighPriority(Boolean.TRUE.equals(inProperties.get(NotificationPropertyKeys.HIGH_PRIORITY)));

        if (template.isReplyTokenRequired())
        {
            Object obj = inProperties.get("activity");
            if (!(obj instanceof HasId))
            {
                throw new ExecutionException("Notification requires activity property for building token.");
            }
            String tokenData = tokenContentFormatter.buildForActivity(((HasId) obj).getId());

            // build individual email for each user with reply address containing user-specific token
            List<UserActionRequest> requests = new ArrayList<UserActionRequest>(inRecipients.size());
            for (long recipientId : inRecipients)
            {
                String address = inRecipientIndex.get(recipientId).getEmail();
                if (StringUtils.isNotBlank(address))
                {
                    String replyAddress = tokenAddressBuilder.build(tokenData, recipientId);

                    NotificationEmailDTO userEmail = email.clone();
                    userEmail.setReplyTo(replyAddress);
                    userEmail.setToRecipient(address);
                    // set the description (for logging / debugging)
                    userEmail.setDescription(inType + " with token to " + address);

                    requests.add(new UserActionRequest("sendEmailNotificationAction", null, userEmail));
                }
            }
            return requests;
        }
        else
        {
            // set the recipients, filtering empty addresses
            List<String> addresses = new ArrayList<String>(inRecipients.size());
            for (long recipientId : inRecipients)
            {
                String address = inRecipientIndex.get(recipientId).getEmail();
                if (StringUtils.isNotBlank(address))
                {
                    addresses.add(address);
                }
            }
            if (addresses.isEmpty())
            {
                return null;
            }
            if (addresses.size() == 1)
            {
                final String address = addresses.get(0);
                email.setToRecipient(address);
                // set the description (for logging / debugging)
                email.setDescription(inType + " to " + address);
            }
            else
            {
                email.setBccRecipients(StringUtils.join(addresses, ','));
                // set the description (for logging / debugging)
                email.setDescription(inType + " to " + inRecipients.size() + " recipients");
            }

            // set the reply-to to the actor (so replies to emails go to the actor, not the system)
            Object obj = inProperties.get(NotificationPropertyKeys.ACTOR);
            if (obj instanceof HasEmail)
            {
                HasEmail actor = (HasEmail) obj;
                email.setReplyTo(actor.getEmail());
            }

            return Collections.singletonList(new UserActionRequest("sendEmailNotificationAction", null, email));
        }
    }
}
