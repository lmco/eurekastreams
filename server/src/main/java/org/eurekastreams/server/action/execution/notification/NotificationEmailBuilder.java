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

import javax.mail.internet.MimeMessage;

import org.eurekastreams.server.domain.NotificationDTO;

/**
 * Creates the contents of the email for a notification.
 */
public interface NotificationEmailBuilder
{
    /**
     * Builds the content and inserts it into the message.
     * 
     * @param notification
     *            Notification to describe.
     * @param message
     *            Message to populate.
     * @throws Exception
     *             On error building message.
     */
    void build(NotificationDTO notification, MimeMessage message) throws Exception;
}
