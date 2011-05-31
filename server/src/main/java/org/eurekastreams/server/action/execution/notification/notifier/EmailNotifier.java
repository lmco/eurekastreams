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
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeHtmlReference;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.email.NotificationEmailDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;

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

    /** To fetch people for email addresses. */
    private final DomainMapper<List<Long>, List<PersonModelView>> personsMapper;

    /** Prefix to use on email subjects. */
    private final String subjectPrefix;

    /**
     * Constructor.
     *
     * @param inVelocityEngine
     *            Apache Velocity templating engine.
     * @param inVelocityGlobalContext
     *            Global context for Apache Velocity templating engine.
     * @param inTemplates
     *            Message templates by notification type.
     * @param inPersonsMapper
     *            To fetch people for email addresses.
     * @param inSubjectPrefix
     *            Prefix to use on email subjects.
     */
    public EmailNotifier(final VelocityEngine inVelocityEngine, final Context inVelocityGlobalContext,
            final Map<NotificationType, EmailNotificationTemplate> inTemplates,
            final DomainMapper<List<Long>, List<PersonModelView>> inPersonsMapper, final String inSubjectPrefix)
    {
        velocityEngine = inVelocityEngine;
        velocityGlobalContext = inVelocityGlobalContext;
        templates = inTemplates;
        personsMapper = inPersonsMapper;
        subjectPrefix = inSubjectPrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserActionRequest notify(final NotificationType inType, final Collection<Long> inRecipients,
            final Map<String, Object> inProperties) throws Exception
    {
        EmailNotificationTemplate template = templates.get(inType);
        if (template == null)
        {
            return null;
        }

        NotificationEmailDTO email = new NotificationEmailDTO();

        Context velocityContext = new VelocityContext(new VelocityContext(inProperties, velocityGlobalContext));
        velocityContext.put("context", velocityContext);
        // velocityContext.put("notificationProperties", inProperties);

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

        // set the recipients
        List<PersonModelView> persons = personsMapper.execute(new ArrayList<Long>(inRecipients));
        if (persons.size() == 1)
        {
            email.setToRecipient(persons.get(0).getEmail());
        }
        else
        {
            email.setBccRecipients(EmailerFactory.buildEmailList(persons));
        }

        email.setDescription(inType + " to " + persons.size() + " recipients");

        return new UserActionRequest("sendEmailNotificationAction", null, email);
    }
}
