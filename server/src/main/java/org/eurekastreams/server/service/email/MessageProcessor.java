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
package org.eurekastreams.server.service.email;

import java.util.Map.Entry;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Responsible for all processing of a single received email message.
 */
public class MessageProcessor
{
    /** For validating/authenticating messages. */
    private final MessageAuthenticator messageAuthenticator;

    /**
     * Constructor.
     * 
     * @param inMessageAuthenticator
     *            For validating/authenticating messages.
     */
    public MessageProcessor(final MessageAuthenticator inMessageAuthenticator)
    {
        messageAuthenticator = inMessageAuthenticator;
    }

    /**
     * Processes the message.
     *
     * @param message
     *            Message to process.
     * @throws MessagingException
     *             On error.
     */
    public void execute(final Message message) throws MessagingException
    {
        // validate/authenticate
        MessageAuthenticationResult msgAuthData = messageAuthenticator.authenticate(message);
        String statusReplyTo = msgAuthData.getSenderAddress();

        // ###
        System.out.println("--- Successful email validation ---");
        System.out.printf("Sender: %s%n", statusReplyTo);
        for (Entry<String, Long> e : msgAuthData.getMetadata().entrySet())
        {
            System.out.printf("Metadata:  %s=%d%n", e.getKey(), e.getValue());
        }

        boolean success = false;
        try
        {
            // extract content
            // TODO

            // perform action
            // TODO

            success = true;
        }
        finally
        {
            // notify user on failure (so long as user was validated)
            if (!success && statusReplyTo != null)
            {
                // TODO
                int makeCheckstyleShutUpUntilIveWrittenTheCode = 1;
            }
        }
    }
}

// snippets for unit test:
//
// /** Test data. */
// private static final String SENDER_ADDRESS = "john.doe@eurekastreams.org";
//
// final MessageAuthenticationResult authData = new MessageAuthenticationResult(new HashMap<String, Long>(),
// SENDER_ADDRESS);
//
// oneOf(messageAuthenticator).authenticate(message);
// will(returnValue(authData));

