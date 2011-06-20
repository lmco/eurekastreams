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
import org.eurekastreams.commons.actions.context.ClientPrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Validates requests (from the external API) to send notifications.
 */
public class SendPrebuiltNotificationValidation implements ValidationStrategy<ClientPrincipalActionContext>
{
    /** Max length of message. */
    private final int maxMessageLength;

    /** Max length of attached link. */
    private final int maxUrlLength;

    /** Mapper to get recipient id. */
    private final DomainMapper<String, PersonModelView> personMapper;

    /**
     * Constructor.
     *
     * @param inPersonMapper
     *            Person mapper.
     * @param inMaxMessageLength
     *            Max length of message.
     * @param inMaxUrlLength
     *            Max length of attached link
     */
    public SendPrebuiltNotificationValidation(final DomainMapper<String, PersonModelView> inPersonMapper,
            final int inMaxMessageLength, final int inMaxUrlLength)
    {
        personMapper = inPersonMapper;
        maxMessageLength = inMaxMessageLength;
        maxUrlLength = inMaxUrlLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final ClientPrincipalActionContext inActionContext) throws ValidationException
    {
        SendPrebuiltNotificationRequest params = (SendPrebuiltNotificationRequest) inActionContext.getParams();
        ValidationException ve = new ValidationException();

        // insure valid recipient
        PersonModelView recipient = personMapper.execute(params.getRecipientAccountId());
        if (recipient == null)
        {
            ve.addError("recipientAccountId", "Unknown or missing recipient account id.");
        }
        else if (recipient.isAccountLocked())
        {
            ve.addError("recipientAccountId", "Cannot send notifications to locked users.");
        }
        else
        {
            inActionContext.getState().put("recipient", recipient);
        }

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
