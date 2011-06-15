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
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;

/**
 * This action updates the current user's preferences to disable a given notification category for all notifier types.
 * It could easily be updated to user parameter extractors to make it get the user from the parameter instead of the
 * context.
 */
public class DisableNotificationCategoryExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Mapper to update preferences. */
    private final DomainMapper<SetUserNotificationFilterPreferencesRequest, Void> prefsMapper;

    /** Notifiers which can be disabled. */
    private final Map<String, String> notifierTypes;

    /**
     * Constructor.
     *
     * @param inPrefsMapper
     *            Mapper to update preferences.
     * @param inNotifierTypes
     *            Notifiers which can be disabled.
     */
    public DisableNotificationCategoryExecution(
            final DomainMapper<SetUserNotificationFilterPreferencesRequest, Void> inPrefsMapper,
            final Map<String, String> inNotifierTypes)
    {
        prefsMapper = inPrefsMapper;
        notifierTypes = inNotifierTypes;
    }

    /**
     * {@inheritDoc} This method calls a database mapper to mark all application alerts as read for the user making the
     * action request. The method then makes a mapper call to sync the count of unread items for the current user with
     * the cached unread count.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        String category = (String) inActionContext.getParams();
        long userId = inActionContext.getPrincipal().getId();

        // build list of preferences to set: one for each disableable notifier
        List<NotificationFilterPreferenceDTO> dtos = new ArrayList<NotificationFilterPreferenceDTO>();
        for (String notifier : notifierTypes.keySet())
        {
            dtos.add(new NotificationFilterPreferenceDTO(notifier, category));
        }

        prefsMapper.execute(new SetUserNotificationFilterPreferencesRequest(userId, dtos));

        return null;
    }
}
