/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
import org.eurekastreams.commons.task.TaskExecutor;

/**
 * This class takes a message off the queue, gets the request out of the message, gets the action, and calls the action,
 * passing the request to it.
 * 
 */
public class AsyncActionProcessorMDB implements MessageListener
{
    /**
     * The logger.
     */
    private Logger log = Logger.getLogger(AsyncActionProcessorMDB.class);
    
    /**
     * The real time executer to run the action.
     */
    private TaskExecutor taskExecutor;

    /**
     * Setting for task executer.
     * 
     * @param inTaskExecuter
     * 			the task executer to use.
     */
    public void setTaskExecutor(final TaskExecutor inTaskExecuter)
    {
    	this.taskExecutor = inTaskExecuter;
    }
    
    /**
     * Receives a request off the queue and hands it to an action.
     * 
     * @param message
     *            the message containing the request
     */
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
                taskExecutor.execute(userActionRequest);
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
