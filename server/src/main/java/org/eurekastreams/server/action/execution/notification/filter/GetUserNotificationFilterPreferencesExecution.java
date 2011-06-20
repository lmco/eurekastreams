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
package org.eurekastreams.server.action.execution.notification.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.response.notification.GetUserNotificationFilterPreferencesResponse;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Returns all data needed for the notification filters configuration screen.
 */
public class GetUserNotificationFilterPreferencesExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Mapper. */
    private final DomainMapper<Long, Collection<NotificationFilterPreferenceDTO>> mapper;

    /** List of notifiers which can be disabled. */
    private final Map<String, String> notifierTypes;

    /** List of allowed notification categories. */
    private final Set<String> categories;

    /**
     * Constructor.
     *
     * @param inMapper
     *            Mapper.
     * @param inNotifierTypes
     *            List of notifiers which can be disabled.
     * @param inNotificationCategories
     *            List of allowed notification categories.
     */
    public GetUserNotificationFilterPreferencesExecution(
            final DomainMapper<Long, Collection<NotificationFilterPreferenceDTO>> inMapper,
            final Map<String, String> inNotifierTypes, final Set<String> inNotificationCategories)
    {
        mapper = inMapper;
        notifierTypes = inNotifierTypes;
        categories = inNotificationCategories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        long userId = inActionContext.getPrincipal().getId();

        // fetch current preferences and filter by valid filterable notifiers
        // (just in case the list in the Spring config changed since the user last updated preferences in the db)
        List<NotificationFilterPreferenceDTO> list = new ArrayList<NotificationFilterPreferenceDTO>();
        for (NotificationFilterPreferenceDTO dto : mapper.execute(userId))
        {
            if (notifierTypes.containsKey(dto.getNotifierType()) && categories.contains(dto.getNotificationCategory()))
            {
                list.add(dto);
            }
        }

        return new GetUserNotificationFilterPreferencesResponse(list, notifierTypes);
    }
}
