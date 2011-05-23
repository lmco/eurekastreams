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

import java.io.Serializable;

/**
 * Request to send a pre-built notification. This request is received from external sources and goes to the
 * SendPrebuiltNotification action.
 */
public class SendPrebuiltNotificationRequest implements Serializable
{
    /** If high priority. */
    private boolean highPriority;

    /** Person to receive notification. */
    private String recipientAccountId;

    /** The message. */
    private String message;

    /** Associated URL. */
    private String url;

    /**
     * Constructor for JSON creation.
     */
    public SendPrebuiltNotificationRequest()
    {
    }

    /**
     * Constructor.
     *
     * @param inHighPriority
     *            If high priority.
     * @param inRecipientAccountId
     *            Person to receive notification.
     * @param inMessage
     *            The message.
     * @param inUrl
     *            Associated URL.
     */
    public SendPrebuiltNotificationRequest(final boolean inHighPriority, final String inRecipientAccountId,
            final String inMessage, final String inUrl)
    {
        highPriority = inHighPriority;
        recipientAccountId = inRecipientAccountId;
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
     * @return the recipientAccountId
     */
    public String getRecipientAccountId()
    {
        return recipientAccountId;
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
     * @param inHighPriority
     *            the highPriority to set
     */
    public void setHighPriority(final boolean inHighPriority)
    {
        highPriority = inHighPriority;
    }

    /**
     * @param inRecipientAccountId
     *            the recipientAccountId to set
     */
    public void setRecipientAccountId(final String inRecipientAccountId)
    {
        recipientAccountId = inRecipientAccountId;
    }

    /**
     * @param inMessage
     *            the message to set
     */
    public void setMessage(final String inMessage)
    {
        message = inMessage;
    }

    /**
     * @param inUrl
     *            the url to set
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }
}
