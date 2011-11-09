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

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeHtmlReference;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.eurekastreams.server.support.email.EmailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responds to a message with result status.
 */
public class MessageReplier
{
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** For creating response emails. */
    private final EmailerFactory emailerFactory;

    /** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine;

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext;

    /** Templates for error response messages. */
    private final Map<String, EmailTemplate> errorMessageTemplates;

    /** Prepares exceptions for returning to the client. */
    private final Transformer<Exception, Exception> exceptionSanitizer;

    /**
     * For getting system settings. IMPORTANT: Supplied mapper must handle transactions, since this class is invoked
     * outside of one.
     */
    private final DomainMapper< ? extends Serializable, SystemSettings> systemSettingsDao;

    /**
     * Constructor.
     *
     * @param inEmailerFactory
     *            For creating response emails.
     * @param inVelocityEngine
     *            Apache Velocity templating engine.
     * @param inVelocityGlobalContext
     *            Global context for Apache Velocity templating engine. (Holds system-wide properties.)
     * @param inExceptionSanitizer
     *            Prepares exceptions for returning to the client.
     * @param inSystemSettingsDao
     *            For getting system settings.
     * @param inErrorMessageTemplates
     *            Templates for error response messages.
     */
    public MessageReplier(final EmailerFactory inEmailerFactory, final VelocityEngine inVelocityEngine,
            final Context inVelocityGlobalContext, final Transformer<Exception, Exception> inExceptionSanitizer,
            final DomainMapper< ? extends Serializable, SystemSettings> inSystemSettingsDao,
            final Map<String, EmailTemplate> inErrorMessageTemplates)
    {
        emailerFactory = inEmailerFactory;
        velocityEngine = inVelocityEngine;
        velocityGlobalContext = inVelocityGlobalContext;
        exceptionSanitizer = inExceptionSanitizer;
        systemSettingsDao = inSystemSettingsDao;
        errorMessageTemplates = inErrorMessageTemplates;
    }

    /**
     * Responds to the given message.
     *
     * @param message
     *            Original message.
     * @param user
     *            User that sent the message.
     * @param actionSelection
     *            Action executed.
     * @param inException
     *            The processing error which necessitated this reply.
     * @param inResponseMessages
     *            List to add response messages to.
     */
    public void reply(final Message message, final PersonModelView user, final UserActionRequest actionSelection,
            final Exception inException, final List<Message> inResponseMessages)
    {
        final String actionName = actionSelection.getActionKey();
        try
        {
            Exception cleanException = exceptionSanitizer.transform(inException);

            // create reponse message
            MimeMessage response = emailerFactory.createMessage();
            emailerFactory.setTo(response, user.getEmail());

            EmailTemplate template = errorMessageTemplates.get(actionName);
            if (template == null)
            {
                log.warn("Missing template for error response message for action {}.  Sending generic response.",
                        actionName);

                emailerFactory.setSubject(response, "Error processing received email");
                emailerFactory.setTextBody(response,
                        "There was an error processing your email.  " + cleanException.getMessage()
                                + "  Original message is attached.");
            }
            else
            {
                // prepare for template rendering
                Context velocityContext = new VelocityContext(velocityGlobalContext);
                velocityContext.put("action", actionName);
                velocityContext.put("params", actionSelection.getParams());
                velocityContext.put("user", user);
                velocityContext.put("exception", cleanException);
                velocityContext.put("originalException", inException);
                velocityContext.put("settings", systemSettingsDao.execute(null));

                // build the subject
                StringWriter writer = new StringWriter();
                velocityEngine.evaluate(velocityContext, writer, "EmailSubject-" + actionName,
                        template.getSubjectTemplate());
                emailerFactory.setSubject(response, writer.toString());

                // build the text body
                Template vt = velocityEngine.getTemplate(template.getTextBodyTemplateResourcePath());
                writer.getBuffer().setLength(0);
                vt.merge(velocityContext, writer);
                emailerFactory.setTextBody(response, writer.toString());

                // build the HTML body
                vt = velocityEngine.getTemplate(template.getHtmlBodyTemplateResourcePath());
                // HTML-escape all content inserted
                EventCartridge ec = new EventCartridge();
                ec.addEventHandler(new EscapeHtmlReference());
                ec.attachToContext(velocityContext);
                writer.getBuffer().setLength(0);
                vt.merge(velocityContext, writer);
                emailerFactory.setHtmlBody(response, writer.toString());
            }

            // attach original message
            emailerFactory.addAttachmentMessage(response, message);

            inResponseMessages.add(response);
        }
        catch (Exception ex)
        {
            log.error("Error building error response message for failed action '{}'.", actionName, ex);
        }
    }
}
