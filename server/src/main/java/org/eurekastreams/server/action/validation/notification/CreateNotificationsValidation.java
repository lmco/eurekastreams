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
package org.eurekastreams.server.action.validation.notification;

import java.util.Map;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.execution.notification.translator.NotificationTranslator;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;

/**
 * This class validates that the parameters passed to the CreateNotifications action are correct.
 *
 */
public class CreateNotificationsValidation implements ValidationStrategy<ActionContext>
{
    /**
     * Map of valid translators.
     */
    private final Map<RequestType, NotificationTranslator> translators;

    /**
     * Constructor.
     * @param inTranslators - Map of translators available for creating notifications.
     */
    public CreateNotificationsValidation(final Map<RequestType, NotificationTranslator> inTranslators)
    {
        translators = inTranslators;
    }

    /**
     * {@inheritDoc}.
     * Validate that the requested type is supported by the translators available.
     */
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        CreateNotificationsRequest currentRequest = (CreateNotificationsRequest) inActionContext.getParams();
        NotificationTranslator translator = translators.get(currentRequest.getType());
        if (translator == null)
        {
            throw new ValidationException("Invalid notification type: " + currentRequest.getType());
        }
    }

}
