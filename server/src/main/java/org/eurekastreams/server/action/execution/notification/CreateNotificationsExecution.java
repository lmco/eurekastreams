/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.notification.translator.NotificationTranslator;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetNotificationFilterPreferencesByPeopleIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Async action to generate notifications.
 *
 */
public class CreateNotificationsExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /** Local logger instance. */
    private final Log logger = LogFactory.make();

    /** Map of valid translators. */
    private final Map<RequestType, NotificationTranslator> translators;

    /**
     * Populates a notification with any details not provided by the translator. Right now there is one, but eventually
     * there may be a list or map of them.
     */
    private final NotificationPopulator populator;

    /** List of notifiers that should be executed. */
    private final Map<String, Notifier> notifiers;

    /** Mapper to filter out unwanted notifications per recipient. */
    private final GetNotificationFilterPreferencesByPeopleIds preferencesMapper;

    /** Mapper to get people for filtering (determining locked users, etc.). */
    private final DomainMapper<Long, PersonModelView> personMapper;

    /** Provides the category for each notification type. */
    private final Map<NotificationType, Category> notificationTypeToCategory;

    /** Recipient filter strategies per notifier type. */
    private final Map<String, Iterable<RecipientFilter>> recipientFilters;

    /**
     * Constructor.
     *
     * @param inTranslators
     *            map of translators to set.
     * @param inPopulator
     *            notification populator.
     * @param inNotifiers
     *            list of notifiers to set.
     * @param inPreferencesMapper
     *            preferences mapper to set.
     * @param inPersonMapper
     *            Mapper to get people for filtering.
     * @param inNotificationTypeCategories
     *            Map providing the category for each notification type.
     * @param inRecipientFilters
     *            Recipient filter strategies per notifier type.
     */
    public CreateNotificationsExecution(final Map<RequestType, NotificationTranslator> inTranslators,
            final NotificationPopulator inPopulator, final Map<String, Notifier> inNotifiers,
            final GetNotificationFilterPreferencesByPeopleIds inPreferencesMapper,
            final DomainMapper<Long, PersonModelView> inPersonMapper,
            final Map<NotificationType, Category> inNotificationTypeCategories,
            final Map<String, Iterable<RecipientFilter>> inRecipientFilters)
    {
        translators = inTranslators;
        populator = inPopulator;
        notifiers = inNotifiers;
        preferencesMapper = inPreferencesMapper;
        personMapper = inPersonMapper;
        notificationTypeToCategory = inNotificationTypeCategories;
        recipientFilters = inRecipientFilters;
    }

    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
            throws ExecutionException
    {
        List<UserActionRequest> asyncRequests = new ArrayList<UserActionRequest>();
        CreateNotificationsRequest currentRequest = (CreateNotificationsRequest) inActionContext.getActionContext()
                .getParams();

        if (logger.isInfoEnabled())
        {
            logger.info("Generating notifications for " + currentRequest.getType());
        }
        NotificationTranslator translator = translators.get(currentRequest.getType());
        if (translator == null)
        {
            // exit if notification request type is disabled
            return Boolean.FALSE;
        }
        Collection<NotificationDTO> notifications = translator.translate(currentRequest.getActorId(),
                currentRequest.getDestinationId(), currentRequest.getActivityId());

        // Gets all notification recipients so their preferences can be retrieved using the mapper
        List<Long> allRecipients = new ArrayList<Long>();
        for (NotificationDTO dto : notifications)
        {
            allRecipients.addAll(dto.getRecipientIds());
        }
        List<NotificationFilterPreferenceDTO> recipientFilterPreferences = preferencesMapper.execute(allRecipients);

        for (NotificationDTO notification : notifications)
        {
            boolean populated = false;
            for (String notifierKey : notifiers.keySet())
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("Filtering " + notification.getType() + " recipients for notifier " + notifierKey
                            + "from this list: " + notification.getRecipientIds());
                }

                List<Long> filteredRecipients = filterRecipients(notification.getRecipientIds(), notification,
                        recipientFilterPreferences, notifierKey);
                if (!filteredRecipients.isEmpty())
                {
                    // "populate" the notification with any additional data not set by the translator
                    // (Do it here so that if the notification gets completely filtered out, we don't do the work
                    if (!populated)
                    {
                        populator.populate(notification);
                        populated = true;
                    }

                    try
                    {
                        if (logger.isInfoEnabled())
                        {
                            logger.info("Sending notification " + notification.getType() + " via " + notifierKey
                                    + " to " + filteredRecipients + " destinationType of "
                                    + notification.getDestinationType());
                        }

                        // clone notification and set recipients
                        NotificationDTO clonedNotification = new NotificationDTO(notification);
                        clonedNotification.setRecipientIds(filteredRecipients);

                        UserActionRequest actionRequest = notifiers.get(notifierKey).notify(clonedNotification);
                        if (actionRequest != null)
                        {
                            asyncRequests.add(actionRequest);
                        }
                    }
                    catch (Exception ex)
                    {
                        logger.error(
                                "Failed to send notifications from " + notifierKey + " for " + notification.getType(),
                                ex);
                    }
                }
            }
        }

        inActionContext.getUserActionRequests().addAll(asyncRequests);
        return Boolean.TRUE;
    }

    /**
     * Filters out notification recipients based on per-recipient settings.
     *
     * @param notificationRecipients
     *            the list of all recipient ids for the notification, unfiltered.
     * @param notification
     *            the notification.
     * @param preferences
     *            the list of all notification preferences for users in the the allRecipient list.
     * @param notifierType
     *            the key string for the notifier itself.
     * @return the filtered list of recipient ids.
     */
    private List<Long> filterRecipients(final List<Long> notificationRecipients, final NotificationDTO notification,
            final List<NotificationFilterPreferenceDTO> preferences, final String notifierType)
    {
        Category category = notificationTypeToCategory.get(notification.getType());
        List<Long> recipients = new ArrayList<Long>(notificationRecipients);

        // remove any users who opted out of the notification (for the given transport)
        for (NotificationFilterPreferenceDTO preference : preferences)
        {
            if (preference.getNotifierType().equals(notifierType)
                    && preference.getNotificationCategory().equals(category))
            {
                recipients.remove(preference.getPersonId());
            }
        }

        // filter list further by configurable criteria
        Iterable<RecipientFilter> filters = recipientFilters.get(notifierType);
        if (filters == null)
        {
            return recipients;
        }
        else
        {
            List<Long> finalRecipients = new ArrayList<Long>();
            eachRecipient: for (Long recipientId : recipients)
            {
                // unfortunately, users (who have not opted out of a given notifier) will be retrieved once PER notifier
                // (vs. once for the whole action)
                PersonModelView recipient = personMapper.execute(recipientId);

                for (RecipientFilter filter : filters)
                {
                    if (filter.shouldFilter(recipient, notification, notifierType))
                    {
                        continue eachRecipient;
                    }
                }
                finalRecipients.add(recipientId);
            }

            return finalRecipients;
        }
    }
}
