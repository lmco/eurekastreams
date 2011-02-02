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

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.NotificationDTO;

/**
 * Notifier to process the notification asynchronously in a separate action.
 */
public class AsyncNotifier implements Notifier
{
    /** Action to use to process the notification. */
    private final String actionName;

    /**
     * Constructor.
     *
     * @param inActionName
     *            Action to use to process the notification.
     */
    public AsyncNotifier(final String inActionName)
    {
        actionName = inActionName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserActionRequest notify(final NotificationDTO inNotification) throws Exception
    {
        return new UserActionRequest(actionName, null, inNotification);
    }
}
