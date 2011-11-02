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
import org.eurekastreams.commons.exceptions.OutOfDateObjectException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.domain.Identifiable;
import org.eurekastreams.server.domain.InAppNotificationEntity;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Notifier for in-app notifications. Builds the messages and stores them in the database.
 */
public class InAppNotificationNotifier implements Notifier
{
    /** Apache Velocity templating engine. */
    private final VelocityEngine velocityEngine;

    /**
     * Global context for Apache Velocity templating engine. (Holds system-wide properties.)
     */
    private final Context velocityGlobalContext;

    /** Message templates by notification type. */
    private final Map<NotificationType, String> templates;

    /** Aggregate message templates by notification type. */
    private final Map<NotificationType, String> aggregateTemplates;

    /** Mapper to persist the notification. */
    private final DomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> insertMapper;

    /** Mapper to update aggregate notifications. */
    private final DomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> updateMapper;

    /** Mapper to sync unread alert count in cache. */
    private final DomainMapper<Long, UnreadInAppNotificationCountDTO> syncMapper;

    /** Provides a dummy person object for persisting the in-app entity. */
    private final DomainMapper<Long, Person> placeholderPersonMapper;

    /** Looks up existing notifications for aggregation. */
    private final DomainMapper<InAppNotificationEntity, InAppNotificationEntity> existingNotificationMapper;

    /**
     * Constructor.
     * 
     * @param inVelocityEngine
     *            Apache Velocity templating engine.
     * @param inVelocityGlobalContext
     *            Global context for Apache Velocity templating engine.
     * @param inTemplates
     *            Message templates by notification type.
     * @param inAggregateTemplates
     *            Aggregate message templates by notification type.
     * @param inInsertMapper
     *            Mapper to persist the notification.
     * @param inUpdateMapper
     *            Mapper to update existing notifications.
     * @param inSyncMapper
     *            Mapper to sync unread alert count in cache.
     * @param inPlaceholderPersonMapper
     *            Provides a dummy person object for persisting the in-app entity.
     * @param inExistingNotificationMapper
     *            Mapper to search for existing notifications that can be aggregated with the current notification
     */
    public InAppNotificationNotifier(final VelocityEngine inVelocityEngine, final Context inVelocityGlobalContext,
            final Map<NotificationType, String> inTemplates, final Map<NotificationType, String> inAggregateTemplates,
            final DomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> inInsertMapper,
            final DomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> inUpdateMapper,
            final DomainMapper<Long, UnreadInAppNotificationCountDTO> inSyncMapper,
            final DomainMapper<Long, Person> inPlaceholderPersonMapper,
            final DomainMapper<InAppNotificationEntity, InAppNotificationEntity> inExistingNotificationMapper)
    {
        velocityEngine = inVelocityEngine;
        velocityGlobalContext = inVelocityGlobalContext;
        templates = inTemplates;
        aggregateTemplates = inAggregateTemplates;
        insertMapper = inInsertMapper;
        updateMapper = inUpdateMapper;
        syncMapper = inSyncMapper;
        placeholderPersonMapper = inPlaceholderPersonMapper;
        existingNotificationMapper = inExistingNotificationMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserActionRequest> notify(final NotificationType inType, final Collection<Long> inRecipients,
            final Map<String, Object> inProperties, final Map<Long, PersonModelView> inRecipientIndex)
            throws Exception
    {
        Context velocityContext = new VelocityContext(new VelocityContext(inProperties, velocityGlobalContext));
        velocityContext.put("context", velocityContext);
        velocityContext.put("type", inType);

        for (long recipientId : inRecipients)
        {
            Person recipient = placeholderPersonMapper.execute(recipientId);
            if (recipient == null)
            {
                continue;
            }

            if (aggregateTemplates.containsKey(inType))
            {
                updateAggregateNotification(recipient, inType, inProperties, velocityContext);
            }
            else
            {
                createNewNotification(recipient, inType, inProperties, velocityContext);
            }
        }
        return null;
    }

    /**
     * Handles aggregated notification types. Checks for an existing notification of the same type to the same
     * recipient. If it finds an existing notification, it increments its count. Otherwise, it creates a new
     * notification.
     * 
     * @param recipient
     *            The person to notify
     * @param inType
     *            The type of the notification
     * @param inProperties
     *            Additional info about the notification
     * @param velocityContext
     *            Velocity context used to generate notification message
     */
    private void updateAggregateNotification(final Person recipient, final NotificationType inType,
            final Map<String, Object> inProperties, final Context velocityContext)
    {
        InAppNotificationEntity searchCriteria = new InAppNotificationEntity();
        searchCriteria.setRecipient(recipient);
        searchCriteria.setNotificationType(inType);
        searchCriteria.setUrl((String) inProperties.get(NotificationPropertyKeys.URL));
        InAppNotificationEntity existingAggregateNotification = existingNotificationMapper.execute(searchCriteria);

        if (existingAggregateNotification == null)
        {
            createNewNotification(recipient, inType, inProperties, velocityContext);
            return;
        }

        int newAggregationCount = existingAggregateNotification.getAggregationCount() + 1;
        existingAggregateNotification.setAggregationCount(newAggregationCount);

        existingAggregateNotification.setNotificationDate(new Date());

        String template = aggregateTemplates.get(inType);
        if (template == null)
        {
            return;
        }

        velocityContext.put("recipient", recipient);
        velocityContext.put("aggregationCount", newAggregationCount);
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(velocityContext, writer, "InAppNotification-" + inType, template);

        String message = writer.toString();
        existingAggregateNotification.setMessage(message);

        try
        {
            updateMapper.execute(new PersistenceRequest<InAppNotificationEntity>(existingAggregateNotification));
        }
        catch (OutOfDateObjectException e)
        {
            updateAggregateNotification(recipient, inType, inProperties, velocityContext);
        }

        syncMapper.execute(recipient.getId());
    }

    /**
     * Creates a new notification.
     * 
     * @param recipient
     *            The person to notify
     * @param inType
     *            The type of the notification
     * @param inProperties
     *            Additional info about the notification
     * @param velocityContext
     *            Velocity context used to generate notification message
     */
    private void createNewNotification(final Person recipient, final NotificationType inType,
            final Map<String, Object> inProperties, final Context velocityContext)
    {
        InAppNotificationEntity dbNotif = new InAppNotificationEntity();
        dbNotif.setNotificationType(inType);
        dbNotif.setRecipient(recipient);
        dbNotif.setUrl((String) inProperties.get(NotificationPropertyKeys.URL));
        dbNotif.setHighPriority(Boolean.TRUE.equals(inProperties.get(NotificationPropertyKeys.HIGH_PRIORITY)));

        Object obj = inProperties.get(NotificationPropertyKeys.SOURCE);
        if (obj instanceof Identifiable)
        {
            Identifiable source = (Identifiable) obj;
            dbNotif.setSourceType(source.getEntityType());
            dbNotif.setSourceUniqueId(source.getUniqueId());
            dbNotif.setSourceName(source.getDisplayName());
        }
        obj = inProperties.get(NotificationPropertyKeys.ACTOR);
        if (obj instanceof Identifiable)
        {
            Identifiable actor = (Identifiable) obj;
            dbNotif.setAvatarOwnerType(actor.getEntityType());
            dbNotif.setAvatarOwnerUniqueId(actor.getUniqueId());
        }

        String template = templates.get(inType);
        if (template == null)
        {
            return;
        }

        velocityContext.put("recipient", recipient);
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(velocityContext, writer, "InAppNotification-" + inType, template);

        String message = writer.toString();
        dbNotif.setMessage(message);

        insertMapper.execute(new PersistenceRequest<InAppNotificationEntity>(dbNotif));

        syncMapper.execute(recipient.getId());
    }
}
