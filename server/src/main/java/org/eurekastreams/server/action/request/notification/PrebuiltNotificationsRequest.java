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
package org.eurekastreams.server.action.request.notification;

/**
 * Request to send a pre-built notification. This request is sent internally to the notification engine.
 */
public class PrebuiltNotificationsRequest extends CreateNotificationsRequest
{
    /** Fingerprint. */
    private static final long serialVersionUID = -8520072342860286458L;

    /** If high priority. */
    private final boolean highPriority;

    /** ID of client (application, not person) who requested the notification. */
    private final String clientId;

    /** Person to receive notification. */
    private final long recipientId;

    /** The message. */
    private final String message;

    /** Associated URL. */
    private final String url;

    /**
     * Constructor.
     *
     * @param inType
     *            Type of event.
     * @param inHighPriority
     *            If high priority.
     * @param inClientId
     *            ID of client (application, not person) who requested the notification.
     * @param inRecipientId
     *            Person to receive notification.
     * @param inMessage
     *            The message.
     * @param inUrl
     *            Associated URL.
     */
    public PrebuiltNotificationsRequest(final RequestType inType, final boolean inHighPriority,
            final String inClientId, final long inRecipientId, final String inMessage, final String inUrl)
    {
        super(inType, 0L);
        highPriority = inHighPriority;
        clientId = inClientId;
        recipientId = inRecipientId;
        message = inMessage;
        url = inUrl;
    }

    /**
     * @return the highPriority
     */
    public boolean isHighPriority()
    {
        return highPriority;
    }

    /**
     * @return the clientId
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @return the recipientId
     */
    public long getRecipientId()
    {
        return recipientId;
    }

}
