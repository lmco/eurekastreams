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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Property;
import org.eurekastreams.server.domain.PropertyHashMap;
import org.eurekastreams.server.domain.PropertyMap;
import org.eurekastreams.server.persistence.LazyLoadPropertiesMap;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.notification.GetNotificationFilterPreferenceRequest;
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
    private final DomainMapper<GetNotificationFilterPreferenceRequest, List<NotificationFilterPreferenceDTO>> // \n
    preferencesMapper;

    /** Mapper to get people for filtering (determining locked users, etc.). */
    private final DomainMapper<List<Long>, List<PersonModelView>> personsMapper;

    /** Provides the category for each notification type. */
    private final Map<NotificationType, String> notificationTypeToCategory;

    /** Recipient-based filter strategies per notifier type. */
    private final Map<String, Collection<RecipientFilter>> recipientFilters;

    /** Recipient-independent filter strategies per notifier type. */
    private final Map<String, Collection<RecipientFilter>> bulkFilters;

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
     * @param inPersonsMapper
     *            Mapper to get people for filtering.
     * @param inNotificationTypeCategories
     *            Map providing the category for each notification type.
     * @param inBulkFilters
     *            Bulk filter strategies per notifier type.
     * @param inRecipientFilters
     *            Recipient filter strategies per notifier type.
     * @param inDefaultProperties
     *            Properties provided to all notifications.
     * @param inPropertyLoadMappers
     *            Mappers for loading notification properties.
     */
    public CreateNotificationsExecution(
            final Map<RequestType, NotificationTranslator> inTranslators,
            final Map<String, Notifier> inNotifiers,
            final DomainMapper<GetNotificationFilterPreferenceRequest, List<NotificationFilterPreferenceDTO>> // \n
            inPreferencesMapper, final DomainMapper<List<Long>, List<PersonModelView>> inPersonsMapper,
            final Map<NotificationType, String> inNotificationTypeCategories,
            final Map<String, Collection<RecipientFilter>> inBulkFilters,
            final Map<String, Collection<RecipientFilter>> inRecipientFilters,
            final Map<String, Property<Object>> inDefaultProperties,
            final Map<Class, DomainMapper<Serializable, Object>> inPropertyLoadMappers)
    {
        translators = inTranslators;
        notifiers = inNotifiers;
        preferencesMapper = inPreferencesMapper;
        personsMapper = inPersonsMapper;
        notificationTypeToCategory = inNotificationTypeCategories;
        bulkFilters = inBulkFilters;
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

        // ---- translate event to notifications ----

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

        // ---- prepare for filtering ----
        List<NotificationFilterPreferenceDTO> recipientFilterPreferences = null;

        // build a list of all recipients
        Map<Long, PersonModelView> recipientIndex = buildRecipientIndex(batch);

        // build a list of categories from the notifications. only preference-filterable notifications have a category
        Set<String> categories = new HashSet<String>();
        for (NotificationType type : batch.getRecipients().keySet())
        {
            String category = notificationTypeToCategory.get(type);
            if (category != null)
            {
                categories.add(category);
            }
        }
        // if the list is not empty, fetch the preferences
        if (!categories.isEmpty())
        {
            recipientFilterPreferences = preferencesMapper.execute(new GetNotificationFilterPreferenceRequest(
                    recipientIndex.keySet(), categories));
        }

        // build the map containing the properties of the notification batch
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
                log.debug("Filtering {} recipients for notifier {} from this list: {}", new Object[] { type,
                        notifierKey, recipientIds });

                // filter
                Collection<Long> filteredRecipients = filterRecipients(recipientIds, type, properties, notifierKey,
                        recipientFilterPreferences, recipientIndex);
                if (!filteredRecipients.isEmpty())
                {
                    try
                    {
                        log.info("Sending notification {} via {} to {}", new Object[] { type, notifierKey,
                                filteredRecipients });

                        // send
                        Collection<UserActionRequest> actionRequests = notifiers.get(notifierKey).notify(type,
                                filteredRecipients, properties, recipientIndex);
                        if (actionRequests != null && !actionRequests.isEmpty())
                        {

                            asyncRequests.addAll(actionRequests);
                        }
                    }
                    catch (Exception ex)
                    {
                        log.error("Failed to send notifications from " + notifierKey + " for " + type, ex);
                    }
                }
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Creates a map of all recipient persons for the entire notification batch. This could return a lazy-loading map
     * without the outer code realizing the difference. My current thinking is that most persons will be referenced
     * somewhere along the way, either in the filtering or in the notifying, plus it is more efficient to ask for them
     * in bulk than one at a time, so get them all up front. The truly massive case is someone posting to a stream that
     * many people have subscribed to; this involves sending email, and the email notifier references the
     * PersonModelView, so the lookup will not go to waste.
     * 
     * @param batch
     *            Notification batch.
     * @return Map of person ID to PersonModelView of all recipients.
     */
    private Map<Long, PersonModelView> buildRecipientIndex(final NotificationBatch batch)
    {
        List<Long> allRecipientIds = new ArrayList<Long>();
        for (Collection<Long> recipientIds : batch.getRecipients().values())
        {
            allRecipientIds.addAll(recipientIds);
        }

        Map<Long, PersonModelView> recipientIndex = new HashMap<Long, PersonModelView>();
        for (PersonModelView person : personsMapper.execute(allRecipientIds))
        {
            recipientIndex.put(person.getId(), person);
        }

        return recipientIndex;
    }

    /**
     * Filters out notification recipients based on per-recipient settings.
     * 
     * @param unfilteredRecipients
     *            the list of all recipient ids for the notification, unfiltered.
     * @param type
     *            Type of notification.
     * @param properties
     *            Notification details.
     * @param notifierType
     *            the key string for the notifier itself.
     * @param preferences
     *            the list of all notification preferences for users in the the allRecipient list.
     * @param recipientIndex
     *            Index of all recipients for looking up PersonModelViews.
     * 
     * @return the filtered list of recipient ids.
     */
    private Collection<Long> filterRecipients(final Collection<Long> unfilteredRecipients,
            final NotificationType type, final Map<String, Object> properties, final String notifierType,
            final List<NotificationFilterPreferenceDTO> preferences, final Map<Long, PersonModelView> recipientIndex)
    {
        // apply bulk filters first
        Collection<RecipientFilter> filters = bulkFilters.get(notifierType);
        if (filters != null)
        {
            for (RecipientFilter filter : filters)
            {
                if (filter.shouldFilter(type, null, properties, notifierType))
                {
                    // rejection by a bulk filter means the notification should not be sent to any recipients
                    return Collections.EMPTY_LIST;
                }
            }
        }

        // optimization check (avoid copying collections)
        filters = recipientFilters.get(notifierType);
        String category = notificationTypeToCategory.get(type);
        if ((filters == null || filters.isEmpty())
                && (category == null || preferences == null || preferences.isEmpty()))
        {
            return unfilteredRecipients;
        }

        List<Long> filteredRecipients = new ArrayList<Long>(unfilteredRecipients);

        // preference filtering: remove any users who opted out of the notification (for the given transport)
        if (category != null && preferences != null)
        {
            for (NotificationFilterPreferenceDTO preference : preferences)
            {
                if (preference.getNotifierType().equals(notifierType)
                        && preference.getNotificationCategory().equals(category))
                {
                    filteredRecipients.remove(preference.getPersonId());
                }
            }
        }

        // strategy filtering: apply each strategy to each recipient, remove rejected recipients
        if (filters != null && !filters.isEmpty() && !filteredRecipients.isEmpty())
        {
            Iterator<Long> iter = filteredRecipients.iterator();
            eachRecipient: while (iter.hasNext())
            {
                Long recipientId = iter.next();
                PersonModelView recipient = recipientIndex.get(recipientId);

                for (RecipientFilter filter : filters)
                {
                    if (filter.shouldFilter(type, recipient, properties, notifierType))
                    {
                        iter.remove();
                        continue eachRecipient;
                    }
                }
            }
        }

        return filteredRecipients;
    }
}
