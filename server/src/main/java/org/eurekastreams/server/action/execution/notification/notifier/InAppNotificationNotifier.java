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
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.InAppNotificationEntity;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Notifier for in-app notifications. Builds the messages and stores them in the database.
 */
public class InAppNotificationNotifier implements Notifier
{
    /** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine;

    /** Global context for Apache Velocity templating engine. (Holds system-wide properties.) */
    private final Context velocityGlobalContext;

    /** Message templates by notification type. */
    private final Map<NotificationType, String> templates;

    /** Mapper to persist the notification. */
    private final BaseArgDomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> insertMapper;

    /** Mapper to sync unread alert count in cache. */
    private final DomainMapper<Long, UnreadInAppNotificationCountDTO> syncMapper;

    /** Provides a dummy person object for persisting the in-app entity. */
    private final DomainMapper<Long, Person> placeholderPersonMapper;

    /**
     * Constructor.
     *
     * @param inVelocityEngine
     *            Apache Velocity templating engine.
     * @param inVelocityGlobalContext
     *            Global context for Apache Velocity templating engine.
     * @param inTemplates
     *            Message templates by notification type.
     * @param inInsertMapper
     *            Mapper to persist the notification.
     * @param inSyncMapper
     *            Mapper to sync unread alert count in cache.
     * @param inPlaceholderPersonMapper
     *            Provides a dummy person object for persisting the in-app entity.
     */
    public InAppNotificationNotifier(final VelocityEngine inVelocityEngine, final Context inVelocityGlobalContext,
            final Map<NotificationType, String> inTemplates,
            final BaseArgDomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> inInsertMapper,
            final DomainMapper<Long, UnreadInAppNotificationCountDTO> inSyncMapper,
            final DomainMapper<Long, Person> inPlaceholderPersonMapper)
    {
        velocityEngine = inVelocityEngine;
        velocityGlobalContext = inVelocityGlobalContext;
        templates = inTemplates;
        insertMapper = inInsertMapper;
        syncMapper = inSyncMapper;
        placeholderPersonMapper = inPlaceholderPersonMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserActionRequest notify(final NotificationType inType, final Collection<Long> inRecipients,
            final Map<String, Object> inProperties) throws Exception
    {
        String template = templates.get(inType);
        if (template == null)
        {
            return null;
        }

        Context velocityContext = new VelocityContext(inProperties, velocityGlobalContext);

        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(velocityContext, writer, "InAppNotification-" + inType, template);

        String message = writer.toString();
        Date now = new Date();

        for (long recipientId : inRecipients)
        {
            // Person recipient = (Person) getHibernateSession().get(Person.class, recipientId);
            Person recipient = placeholderPersonMapper.execute(recipientId);
            if (recipient != null)
            {
                InAppNotificationEntity dbNotif = new InAppNotificationEntity();
                dbNotif.setRecipient(recipient);
                dbNotif.setNotificationType(inType);
                dbNotif.setNotificationDate(now);
                dbNotif.setMessage(message);
                // TODO: DO THESE
                // dbNotif.setUrl(inNotification.get());
                // dbNotif.setHighPriority(inNotification.get());
                // dbNotif.setSourceType(inNotification.get());
                // dbNotif.setSourceUniqueId(inNotification.get());
                // dbNotif.setSourceName(inNotification.get());
                // dbNotif.setAvatarOwnerType(inNotification.get());
                // dbNotif.setAvatarOwnerUniqueId(inNotification.get());

                insertMapper.execute(new PersistenceRequest<InAppNotificationEntity>(dbNotif));
                syncMapper.execute(recipientId);
            }
        }
        return null;
    }
}
