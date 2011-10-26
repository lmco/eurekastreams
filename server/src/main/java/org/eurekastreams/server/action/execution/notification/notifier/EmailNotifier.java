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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eurekastreams.server.action.execution.notification.notifier.EmailNotificationTemplate.ReplyAction;
import org.eurekastreams.server.domain.HasEmail;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.email.TokenContentEmailAddressBuilder;
import org.eurekastreams.server.service.email.TokenContentFormatter;
import org.eurekastreams.server.service.utility.authorization.ActivityInteractionAuthorizationStrategy;

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

    /** For determining if users can comment on an activity. */
    private final ActivityInteractionAuthorizationStrategy activityAuthorizer;

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
     * @param inActivityAuthorizer
     *            For determining if users can comment on an activity.
     */
    public EmailNotifier(final VelocityEngine inVelocityEngine, final Context inVelocityGlobalContext,
            final Map<NotificationType, EmailNotificationTemplate> inTemplates, final String inSubjectPrefix,
            final TokenContentFormatter inTokenContentFormatter,
            final TokenContentEmailAddressBuilder inTokenAddressBuilder,
            final ActivityInteractionAuthorizationStrategy inActivityAuthorizer)
    {
        velocityEngine = inVelocityEngine;
        velocityGlobalContext = inVelocityGlobalContext;
        templates = inTemplates;
        subjectPrefix = inSubjectPrefix;
        tokenContentFormatter = inTokenContentFormatter;
        tokenAddressBuilder = inTokenAddressBuilder;
        activityAuthorizer = inActivityAuthorizer;
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

        // prepare recipient lists
        // determine which recipients get individual emails with reply tokens and which get a mass general email
        List<String> addresses = new ArrayList<String>(inRecipients.size());
        Map<String, String> addressesWithTokens = Collections.EMPTY_MAP;
        if (template.getReplyAddressType() == ReplyAction.COMMENT)
        {
            Object obj = inProperties.get("activity");
            if (obj == null || !(obj instanceof ActivityDTO))
            {
                throw new ExecutionException("Notification requires activity property for building token.");
            }
            ActivityDTO activity = (ActivityDTO) obj;

            addressesWithTokens = new HashMap<String, String>(inRecipients.size());
            String tokenData = tokenContentFormatter.buildForActivity(activity.getId());
            // ok to use relaxed mode here: the translators wouldn't include recipients who do not have access to the
            // activity
            boolean generallyAllowed = activityAuthorizer.authorize(activity, ActivityInteractionType.COMMENT, true);

            for (long recipientId : inRecipients)
            {
                String address = inRecipientIndex.get(recipientId).getEmail();
                if (StringUtils.isNotBlank(address))
                {
                    // Note: checking on a per-user basis is very inefficient. That's why the generallyAllowed
                    // optimization was put in to omit the per-user check for streams that allow commenting. Both the
                    // generallyAllowed and the per-user call could be replaced with an authorizer that takes a list of
                    // users to check and returns a list of only those which are allowed (i.e. an authorization filter).
                    // If the scenario arises where there are streams which do not allow commenting with many email
                    // subscribers, then a bulk authorizer could be used to significantly improve performance.
                    if (generallyAllowed
                            || activityAuthorizer.authorize(recipientId, activity, ActivityInteractionType.COMMENT))
                    {
                        String replyAddress = tokenAddressBuilder.build(tokenData, recipientId);
                        addressesWithTokens.put(address, replyAddress);
                    }
                    else
                    {
                        addresses.add(address);
                    }
                }
            }
        }
        else
        {
            for (long recipientId : inRecipients)
            {
                String address = inRecipientIndex.get(recipientId).getEmail();
                if (StringUtils.isNotBlank(address))
                {
                    addresses.add(address);
                }
            }
        }

        int emailCount = addressesWithTokens.size() + (addresses.isEmpty() ? 0 : 1);
        if (emailCount == 0)
        {
            return null;
        }
        List<UserActionRequest> requests = new ArrayList<UserActionRequest>(emailCount);

        // -- prepare the email --

        NotificationEmailDTO email = new NotificationEmailDTO();

        Context velocityContext = new VelocityContext(new VelocityContext(inProperties, velocityGlobalContext));
        velocityContext.put("context", velocityContext);
        velocityContext.put("type", inType);
        if (addressesWithTokens.size() + addresses.size() == 1)
        {
            velocityContext.put("recipient", inRecipientIndex.get(inRecipients.iterator().next()));
        }

        // build the subject
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(velocityContext, writer, "EmailSubject-" + inType, template.getSubjectTemplate());
        email.setSubject(subjectPrefix + writer.toString());

        // set the priority
        email.setHighPriority(Boolean.TRUE.equals(inProperties.get(NotificationPropertyKeys.HIGH_PRIORITY)));

        // render the body

        String noReplyTextBody = null;
        String replyTextBody = null;
        String noReplyHtmlBody = null;
        String replyHtmlBody = null;

        // build the text body
        Template vt = velocityEngine.getTemplate(template.getTextBodyTemplateResourcePath());
        if (!addresses.isEmpty())
        {
            velocityContext.put("hasReplyAddress", false);
            writer.getBuffer().setLength(0);
            vt.merge(velocityContext, writer);
            noReplyTextBody = writer.toString();
        }
        if (!addressesWithTokens.isEmpty())
        {
            velocityContext.put("hasReplyAddress", true);
            writer.getBuffer().setLength(0);
            vt.merge(velocityContext, writer);
            replyTextBody = writer.toString();
        }

        // build the HTML body
        vt = velocityEngine.getTemplate(template.getHtmlBodyTemplateResourcePath());
        // HTML-escape all content inserted
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(new EscapeHtmlReference());
        ec.attachToContext(velocityContext);
        if (!addresses.isEmpty())
        {
            velocityContext.put("hasReplyAddress", false);
            writer.getBuffer().setLength(0);
            vt.merge(velocityContext, writer);
            noReplyHtmlBody = writer.toString();
        }
        if (!addressesWithTokens.isEmpty())
        {
            velocityContext.put("hasReplyAddress", true);
            writer.getBuffer().setLength(0);
            vt.merge(velocityContext, writer);
            replyHtmlBody = writer.toString();
        }

        // -- create requests to send emails --
        if (!addressesWithTokens.isEmpty())
        {
            email.setTextBody(replyTextBody);
            email.setHtmlBody(replyHtmlBody);
            for (Entry<String, String> entry : addressesWithTokens.entrySet())
            {
                NotificationEmailDTO userEmail = email.clone();
                userEmail.setReplyTo(entry.getValue());
                String address = entry.getKey();
                userEmail.setToRecipient(address);
                // set the description (for logging / debugging)
                userEmail.setDescription(inType + " with token to " + address);

                requests.add(new UserActionRequest("sendEmailNotificationAction", null, userEmail));
            }
        }
        if (!addresses.isEmpty())
        {
            email.setTextBody(noReplyTextBody);
            email.setHtmlBody(noReplyHtmlBody);

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

            if (template.getReplyAddressType() == ReplyAction.ACTOR)
            {
                Object obj = inProperties.get(NotificationPropertyKeys.ACTOR);
                if (obj instanceof HasEmail)
                {
                    HasEmail actor = (HasEmail) obj;
                    String actorEmail = actor.getEmail();
                    if (StringUtils.isNotBlank(actorEmail))
                    {
                        email.setReplyTo(actorEmail);
                    }
                }
            }

            requests.add(new UserActionRequest("sendEmailNotificationAction", null, email));
        }
        return requests;
    }
}
