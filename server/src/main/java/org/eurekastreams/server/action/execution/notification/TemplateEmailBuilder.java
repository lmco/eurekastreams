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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;

/**
 * Creates notification emails using text templates.
 */
public class TemplateEmailBuilder implements NotificationEmailBuilder
{
    /** Contains the text to display for an activity type where it doesn't match the enum name. */
    private static Map<BaseObjectType, String> activityTypeDisplayNameOverrides =
            new HashMap<BaseObjectType, String>();

    /** UI profile page names for each entity type. */
    private static Map<EntityType, String> entityPageNames = new HashMap<EntityType, String>();

    /**
     * Initialize constant lookup data.
     */
    static
    {
        activityTypeDisplayNameOverrides.put(BaseObjectType.BOOKMARK, "link");
        activityTypeDisplayNameOverrides.put(BaseObjectType.NOTE, "message");
        entityPageNames.put(EntityType.PERSON, "people");
        entityPageNames.put(EntityType.GROUP, "groups");
        entityPageNames.put(EntityType.ORGANIZATION, "organizations");
    }

    /** Template for the subject. */
    private String subjectTemplate;

    /** Template for the text version of the body. */
    private String textBodyTemplate;

    /** Template for the HTML version of the body. */
    private String htmlBodyTemplate;

    /** For sending email. */
    private EmailerFactory emailer;

    /** For getting person info. */
    private GetPeopleByIds peopleMapper;

    /** Extra properties to include during resolution. */
    private Map<String, String> extraProperties;

    /**
     * Constructor.
     *
     * @param inEmailer
     *            For sending email.
     * @param inPeopleMapper
     *            For getting person info.
     * @param inExtraProperties
     *            Extra properties to include during resolution.
     * @param inSubjectTemplate
     *            Template for the subject.
     * @param inTextBodyTemplate
     *            Template for the text version of the body.
     * @param inHtmlBodyTemplate
     *            Template for the HTML version of the body.
     */
    public TemplateEmailBuilder(final EmailerFactory inEmailer, final GetPeopleByIds inPeopleMapper,
            final Map<String, String> inExtraProperties,
            final String inSubjectTemplate, final String inTextBodyTemplate, final String inHtmlBodyTemplate)
    {
        emailer = inEmailer;
        peopleMapper = inPeopleMapper;
        extraProperties = inExtraProperties;
        subjectTemplate = inSubjectTemplate;
        textBodyTemplate = inTextBodyTemplate;
        htmlBodyTemplate = inHtmlBodyTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(final NotificationDTO notif, final MimeMessage inMessage) throws Exception
    {
        build(notif, null, inMessage);
    }

    /**
     * Builds the email message from the notification and initial properties.
     *
     * @param notif
     *            Notification for which to build message.
     * @param invocationProperties
     *            Initial properties to use.
     * @param inMessage
     *            Email message.
     * @throws Exception
     *             On error.
     */
    public void build(final NotificationDTO notif, final Map<String, String> invocationProperties,
            final MimeMessage inMessage) throws Exception
    {
        // -- build properties --

        Map<String, String> properties = new HashMap<String, String>();

        // from upstream builders
        if (invocationProperties != null)
        {
            properties.putAll(invocationProperties);
        }

        // from system configuration
        if (extraProperties != null)
        {
            properties.putAll(extraProperties);
        }

        // actor
        if (notif.getActorId() > 0)
        {
            properties.put("actor.id", Long.toString(notif.getActorId()));
            properties.put("actor.accountid", notif.getActorAccountId());
            properties.put("actor.name", notif.getActorName());
        }

        // activity
        if (notif.getActivityId() > 0)
        {
            properties.put("activity.id", Long.toString(notif.getActivityId()));
            String type = activityTypeDisplayNameOverrides.get(notif.getActivityType());
            if (type == null)
            {
                type = notif.getActivityType().name().toLowerCase();
            }
            properties.put("activity.type", type);
        }

        // destination
        if (notif.getDestinationId() > 0)
        {
            properties.put("dest.id", Long.toString(notif.getDestinationId()));
            properties.put("dest.type", notif.getDestinationType().name());
            properties.put("dest.uniqueid", notif.getDestinationUniqueId());
            properties.put("dest.name", notif.getDestinationName());
            properties.put("dest.page", entityPageNames.get(notif.getDestinationType()));
        }

        // auxiliary
        if (notif.getAuxiliaryType() != null)
        {
            properties.put("aux.type", notif.getAuxiliaryType().name());
            properties.put("aux.uniqueid", notif.getAuxiliaryUniqueId());
            properties.put("aux.name", notif.getAuxiliaryName());
            properties.put("aux.page", entityPageNames.get(notif.getAuxiliaryType()));
        }

        // -- build email --

        // build and set the email parts
        StrSubstitutor transform = new StrSubstitutor(properties, "$(", ")");
        emailer.setSubject(inMessage, transform.replace(subjectTemplate));
        emailer.setTextBody(inMessage, transform.replace(textBodyTemplate));

        transform.setVariableResolver(new HtmlEncodingLookup(transform.getVariableResolver()));
        emailer.setHtmlBody(inMessage, transform.replace(htmlBodyTemplate));

        // look up recipients and put as email recipients
        List<PersonModelView> recipients = peopleMapper.execute(notif.getRecipientIds());
        if (recipients.size() == 1)
        {
            emailer.setTo(inMessage, recipients.get(0).getEmail());
        }
        else
        {
            emailer.setBcc(inMessage, EmailerFactory.buildEmailList(recipients));
        }
    }

    /**
     * Decorator used to insure that all content is appropriately HTML encoded.
     */
    static class HtmlEncodingLookup extends StrLookup
    {
        /** Lookup. */
        private StrLookup decorated;

        /**
         * Constructor.
         *
         * @param inDecorated
         *            Lookup.
         */
        public HtmlEncodingLookup(final StrLookup inDecorated)
        {
            decorated = inDecorated;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String lookup(final String inKey)
        {
            String value = decorated.lookup(inKey);
            return value == null ? null : StringEscapeUtils.escapeHtml(value);
        }
    }
}
