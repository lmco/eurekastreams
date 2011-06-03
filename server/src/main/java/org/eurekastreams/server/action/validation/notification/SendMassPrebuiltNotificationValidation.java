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
package org.eurekastreams.server.action.validation.notification;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;

/**
 * Validates requests to send mass notifications.
 */
public class SendMassPrebuiltNotificationValidation implements ValidationStrategy<ActionContext>
{
    /** Max length of message. */
    private final int maxMessageLength;

    /** Max length of attached link. */
    private final int maxUrlLength;

    /**
     * Constructor.
     *
     * @param inMaxMessageLength
     *            Max length of message.
     * @param inMaxUrlLength
     *            Max length of attached link
     */
    public SendMassPrebuiltNotificationValidation(final int inMaxMessageLength, final int inMaxUrlLength)
    {
        maxMessageLength = inMaxMessageLength;
        maxUrlLength = inMaxUrlLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        SendPrebuiltNotificationRequest params = (SendPrebuiltNotificationRequest) inActionContext.getParams();
        ValidationException ve = new ValidationException();

        // message
        if (params.getMessage() == null || params.getMessage().isEmpty())
        {
            ve.addError("message", "Message must be provided.");
        }
        else if (params.getMessage().length() > maxMessageLength)
        {
            ve.addError("message", "Message must be no more than " + maxMessageLength + " characters.");
        }
        // TODO: Should we check content of message?

        // URL
        if (params.getUrl() != null && params.getUrl().length() > maxUrlLength)
        {
            ve.addError("url", "URL must be no more than " + maxUrlLength + " characters.");
        }
        // TODO: Should we check format of URL?

        if (ve.hasErrors())
        {
            throw ve;
        }
    }

}
