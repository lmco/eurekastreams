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
package org.eurekastreams.server.action.request.transformer.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;

/**
 * Transformer which prepares the request for setting the current user's notification preferences.
 */
public class SetCurrentUserNotificationPreferencesTransformer implements RequestTransformer
{
    /** List of notifiers which can be disabled. */
    private final Map<String, String> notifierTypes;

    /** List of allowed notification categories. */
    private final Set<String> categories;

    /**
     * Constructor.
     *
     * @param inNotifierTypes
     *            List of notifiers which can be disabled.
     * @param inCategories
     *            List of allowed notification categories.
     */
    public SetCurrentUserNotificationPreferencesTransformer(final Map<String, String> inNotifierTypes,
            final Set<String> inCategories)
    {
        notifierTypes = inNotifierTypes;
        categories = inCategories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable transform(final ActionContext inActionContext)
    {
        long userId = ((PrincipalActionContext) inActionContext).getPrincipal().getId();

        // discard any entries which specify unrecognized notifiers or categories
        Collection<NotificationFilterPreferenceDTO> list = new ArrayList<NotificationFilterPreferenceDTO>();
        for (NotificationFilterPreferenceDTO dto : (Collection<NotificationFilterPreferenceDTO>) inActionContext
                .getParams())
        {
            if (categories.contains(dto.getNotificationCategory()) && notifierTypes.containsKey(dto.getNotifierType()))
            {
                // Note: This code is ok because the mapper uses the person ID in the request and ignores the person ID
                // in the DTO. Otherwise there would be the risk that a request could suppress notifications for someone
                // else.
                list.add(dto);
            }
        }

        return new SetUserNotificationFilterPreferencesRequest(userId, list);
    }
}
