/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.commons.messaging;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskHandler;

/**
 * This class takes a message off the queue, gets the request out of the message, gets the action, and calls the action,
 * passing the request to it.
 */
public class AsyncActionProcessorMDB implements MessageListener
{
    /** The logger. */
    private final Logger log = Logger.getLogger(AsyncActionProcessorMDB.class);

    /** The task handler which will execute the action. */
    private final TaskHandler taskHandler;

    /**
     * Constructor.
     *
     * @param inTaskHandler
     *            The task handler which will execute the action.
     */
    public AsyncActionProcessorMDB(final TaskHandler inTaskHandler)
    {
        taskHandler = inTaskHandler;
    }

    /**
     * Receives a request off the queue and hands it to an action.
     *
     * @param message
     *            the message containing the request
     */
    @Override
    public void onMessage(final Message message)
    {
        try
        {
            if (message instanceof ObjectMessage)
            {
                log.debug("message received is of ObjectMessage type.");

                // get the message containing the request off the queue
                ObjectMessage objectMessage = (ObjectMessage) message;

                // pull the request out of the message
                UserActionRequest userActionRequest = (UserActionRequest) objectMessage.getObject();

                log.debug("found action " + userActionRequest.getActionKey());

                // run the action.
                taskHandler.handleTask(userActionRequest);
            }
            else
            {
                log.debug("message received is not of ObjectMessage type.");
            }
        }
        catch (Exception e)
        {
            log.error(e);
        }
    }
}
