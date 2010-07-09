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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;

import org.eurekastreams.server.domain.NotificationDTO;

/**
 * Request to build and send an individual notification (via email or other).
 */
public class SendNotificationRequest implements Serializable
{
    /** For serialization. */
    private static final long serialVersionUID = -7794431834735168388L;

    /** Notification to send. */
    private NotificationDTO notification;

    /**
     * Constructor.
     *
     * @param inNotification
     *            Notification to send.
     */
    public SendNotificationRequest(final NotificationDTO inNotification)
    {
        notification = inNotification;
    }

    /**
     * @return Notification to send.
     */
    public NotificationDTO getNotification()
    {
        return notification;
    }

    /**
     * @param inNotification
     *            Notification to send.
     */
    public void setNotification(final NotificationDTO inNotification)
    {
        notification = inNotification;
    }
}
