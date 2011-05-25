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
import java.util.Map.Entry;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.notification.filter.RecipientFilter;
import org.eurekastreams.server.action.execution.notification.notifier.Notifier;
import org.eurekastreams.server.action.execution.notification.translator.NotificationTranslator;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Property;
import org.eurekastreams.server.domain.PropertyHashMap;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.persistence.LazyLoadPropertiesMap;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetNotificationFilterPreferencesByPeopleIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Async action to generate notifications.
 */
public class CreateNotificationsExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /** Local logger instance. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** Map of valid translators. */
    private final Map<RequestType, NotificationTranslator> translators;

    /** List of notifiers that should be executed. */
    private final Map<String, Notifier> notifiers;

    /** Mapper to filter out unwanted notifications per recipient. */
    private final GetNotificationFilterPreferencesByPeopleIds preferencesMapper;

    /** Mapper to get people for filtering (determining locked users, etc.). */
    private final DomainMapper<Long, PersonModelView> personMapper;

    /** Mapper to get people for filtering (determining locked users, etc.). */
    private DomainMapper<List<Long>, List<PersonModelView>> bulkPersonMapper;

    /** Provides the category for each notification type. */
    private final Map<NotificationType, Category> notificationTypeToCategory;

    /** Recipient filter strategies per notifier type. */
    private final Map<String, Iterable<RecipientFilter>> recipientFilters;

    /** Mappers for loading notification properties. */
    private final Map<Class, DomainMapper<Serializable, Object>> propertyLoadMappers;

    /** Properties provided to all notifications. */
    private final Map<String, Property<Object>> defaultProperties;

    /**
     * Constructor.
     *
     * @param inTranslators
     *            map of translators to set.
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
     * @param inDefaultProperties
     *            Properties provided to all notifications.
     * @param inPropertyLoadMappers
     *            Mappers for loading notification properties.
     */
    public CreateNotificationsExecution(final Map<RequestType, NotificationTranslator> inTranslators,
            final Map<String, Notifier> inNotifiers,
            final GetNotificationFilterPreferencesByPeopleIds inPreferencesMapper,
            final DomainMapper<Long, PersonModelView> inPersonMapper,
            final Map<NotificationType, Category> inNotificationTypeCategories,
            final Map<String, Iterable<RecipientFilter>> inRecipientFilters,
            final Map<String, Property<Object>> inDefaultProperties,
            final Map<Class, DomainMapper<Serializable, Object>> inPropertyLoadMappers)
    {
        translators = inTranslators;
        notifiers = inNotifiers;
        preferencesMapper = inPreferencesMapper;
        personMapper = inPersonMapper;
        notificationTypeToCategory = inNotificationTypeCategories;
        recipientFilters = inRecipientFilters;
        defaultProperties = inDefaultProperties;
        propertyLoadMappers = inPropertyLoadMappers;
    }

    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
            throws ExecutionException
    {
        CreateNotificationsRequest currentRequest = (CreateNotificationsRequest) inActionContext.getActionContext()
                .getParams();

        log.info("Generating notifications for {}", currentRequest.getType());

        NotificationTranslator translator = translators.get(currentRequest.getType());
        if (translator == null)
        {
            // exit if notification request type is disabled
            return Boolean.FALSE;
        }
        NotificationBatch batch = translator.translate(currentRequest);
        if (batch == null || batch.getRecipients().isEmpty())
        {
            return Boolean.TRUE;
        }

        // Gets all notification recipients so their preferences can be retrieved using the mapper
        List<Long> allRecipientIds = new ArrayList<Long>();
        for (Collection<Long> recipientIds : batch.getRecipients().values())
        {
            allRecipientIds.addAll(recipientIds);
        }
        List<NotificationFilterPreferenceDTO> recipientFilterPreferences = preferencesMapper.execute(allRecipientIds);

        PropertyMap<Object> propertyList = new PropertyHashMap<Object>();
        propertyList.putAll(defaultProperties);
        propertyList.putAll(batch.getProperties());
        Map<String, Object> properties = new LazyLoadPropertiesMap<Object>(propertyList, propertyLoadMappers);

        List<UserActionRequest> asyncRequests = inActionContext.getUserActionRequests();
        for (Entry<NotificationType, Collection<Long>> notification : batch.getRecipients().entrySet())
        {
            NotificationType type = notification.getKey();
            Collection<Long> recipientIds = notification.getValue();

            for (String notifierKey : notifiers.keySet())
            {
                if (log.isInfoEnabled())
                {
                    log.info("Filtering " + type + " recipients for notifier " + notifierKey + "from this list: "
                            + recipientIds);
                }

                List<Long> filteredRecipients = filterRecipients(type, recipientIds, properties,
                        recipientFilterPreferences, notifierKey);
                if (filteredRecipients.isEmpty())
                {
                    continue;
                }

                try
                {
                    if (log.isInfoEnabled())
                    {
                        log.info("Sending notification " + type + " via " + notifierKey + " to " + filteredRecipients);
                    }

                    UserActionRequest actionRequest = notifiers.get(notifierKey).notify(type, filteredRecipients,
                            properties);
                    if (actionRequest != null)
                    {
                        asyncRequests.add(actionRequest);
                    }
                }
                catch (Exception ex)
                {
                    log.error("Failed to send notifications from " + notifierKey + " for " + type, ex);
                }
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Filters out notification recipients based on per-recipient settings.
     *
     * @param type
     *            Type of notification.
     * @param unfilteredRecipients
     *            the list of all recipient ids for the notification, unfiltered.
     * @param properties
     *            Notification details.
     * @param preferences
     *            the list of all notification preferences for users in the the allRecipient list.
     * @param notifierType
     *            the key string for the notifier itself.
     * @return the filtered list of recipient ids.
     */
    private List<Long> filterRecipients(final NotificationType type, final Collection<Long> unfilteredRecipients,
            final Map<String, Object> properties, final List<NotificationFilterPreferenceDTO> preferences,
            final String notifierType)
    {
        Category category = notificationTypeToCategory.get(type);
        List<Long> recipientIds = new ArrayList<Long>(unfilteredRecipients);

        // remove any users who opted out of the notification (for the given transport)
        for (NotificationFilterPreferenceDTO preference : preferences)
        {
            if (preference.getNotifierType().equals(notifierType)
                    && preference.getNotificationCategory().equals(category))
            {
                recipientIds.remove(preference.getPersonId());
            }
        }

        // filter list further by configurable criteria
        Iterable<RecipientFilter> filters = recipientFilters.get(notifierType);
        if (filters == null)
        {
            return recipientIds;
        }
        else
        {
            List<Long> finalRecipients = new ArrayList<Long>();
            eachRecipient: for (Long recipientId : recipientIds)
            {
                // unfortunately, users (who have not opted out of a given notifier) will be retrieved once PER notifier
                // (vs. once for the whole action)
                PersonModelView recipient = personMapper.execute(recipientId);

                for (RecipientFilter filter : filters)
                {
                    if (filter.shouldFilter(type, recipient, properties, notifierType))
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
