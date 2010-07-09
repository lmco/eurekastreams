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
import org.eurekastreams.server.persistence.mappers.db.GetNotificationFilterPreferencesByPeopleIds;

/**
 * Async action to generate notifications.
 *
 */
public class CreateNotificationsExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Local logger instance.
     */
    private Log logger = LogFactory.make();

    /**
     * Map of valid translators.
     */
    private final Map<RequestType, NotificationTranslator> translators;

    /**
     * Populates a notification with any details not provided by the translator. Right now there is one, but eventually
     * there may be a list or map of them.
     */
    private NotificationPopulator populator;

    /**
     * List of notifiers that should be executed.
     */
    private final Map<String, Notifier> notifiers;

    /**
     * Mapper to filter out unwanted notifications per recipient.
     */
    private final GetNotificationFilterPreferencesByPeopleIds preferencesMapper;

    /**
     * Provides the category for each notification type.
     */
    private Map<NotificationType, Category> notificationTypeToCategory;

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
     * @param inNotificationTypeCategories
     *            Map providing the category for each notification type.
     */
    public CreateNotificationsExecution(final Map<RequestType, NotificationTranslator> inTranslators,
            final NotificationPopulator inPopulator, final Map<String, Notifier> inNotifiers,
            final GetNotificationFilterPreferencesByPeopleIds inPreferencesMapper,
            final Map<NotificationType, Category> inNotificationTypeCategories)
    {
        translators = inTranslators;
        populator = inPopulator;
        notifiers = inNotifiers;
        preferencesMapper = inPreferencesMapper;
        notificationTypeToCategory = inNotificationTypeCategories;
    }

    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
            throws ExecutionException
    {
        List<UserActionRequest> asyncRequests = new ArrayList<UserActionRequest>();
        CreateNotificationsRequest currentRequest =
                (CreateNotificationsRequest) inActionContext.getActionContext().getParams();

        logger.info("Generating notifications for " + currentRequest.getType());
        NotificationTranslator translator = translators.get(currentRequest.getType());
        if (translator == null)
        {
            // exit if notification request type is disabled
            return Boolean.FALSE;
        }
        Collection<NotificationDTO> notifications =
                translator.translate(currentRequest.getActorId(), currentRequest.getDestinationId(), currentRequest
                        .getActivityId());
        for (NotificationDTO dto : notifications)
        {
            populator.populate(dto);
        }

        // Gets all notification recipients so their preferences can be retrieved using the mapper
        List<Long> allRecipients = new ArrayList<Long>();
        for (NotificationDTO dto : notifications)
        {
            allRecipients.addAll(dto.getRecipientIds());
        }

        List<NotificationFilterPreferenceDTO> recipientFilterPreferences = preferencesMapper.execute(allRecipients);

        for (NotificationDTO notification : notifications)
        {
            List<Long> fullRecipients = notification.getRecipientIds();

            for (String notifierKey : notifiers.keySet())
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("Filtering " + notification.getType() + " recipients for notifier " + notifierKey
                            + "from this list: " + fullRecipients);
                }

                List<Long> filteredRecipients =
                        filterRecipients(fullRecipients, notification.getType(), recipientFilterPreferences,
                                notifierKey);

                if (!filteredRecipients.isEmpty())
                {
                    notification.setRecipientIds(filteredRecipients);
                    try
                    {
                        if (logger.isInfoEnabled())
                        {
                            logger.info("Sending notification " + notification.getType() + " via " + notifierKey
                                    + " to " + filteredRecipients + " destinationType of "
                                    + notification.getDestinationType());
                        }
                        UserActionRequest actionRequest = notifiers.get(notifierKey).notify(notification);
                        if (actionRequest != null)
                        {
                            asyncRequests.add(actionRequest);
                        }
                    }
                    catch (Exception ex)
                    {
                        logger.error("Failed to send notifications from " + notifierKey + " for "
                                + notification.getType(), ex);
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
     * @param allRecipients
     *            the list of all recipient ids, unfiltered.
     * @param notificationType
     *            the notification type for this notification.
     * @param preferences
     *            the list of all notification preferences for users in the the allRecipient list.
     * @param notifierType
     *            the key string for the notifier itself.
     * @return the filtered list of recipient ids.
     */
    private List<Long> filterRecipients(final List<Long> allRecipients, final NotificationType notificationType,
            final List<NotificationFilterPreferenceDTO> preferences, final String notifierType)
    {
        Category category = notificationTypeToCategory.get(notificationType);
        List<Long> recipients = new ArrayList<Long>(allRecipients);

        for (NotificationFilterPreferenceDTO preference : preferences)
        {
            if (preference.getNotifierType().equals(notifierType)
                    && preference.getNotificationCategory().equals(category))
            {
                recipients.remove(preference.getPersonId());
            }
        }

        return recipients;
    }
}
