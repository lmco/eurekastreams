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
package org.eurekastreams.commons.task;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.eurekastreams.commons.server.UserActionRequest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.Assert;

/**
 * This class puts a request into a message and places it on the queue.
 * 
 */
public class QueueTaskHandler implements TaskHandler
{

    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(QueueTaskHandler.class);

    /**
     * The JMS template used to place the request on the queue.
     */
    private JmsTemplate jmsTemplate;

    /**
     * Constructor.
     * 
     * @param inJmsTemplate
     *            The JMS template used to place the request on the queue.
     */
    public QueueTaskHandler(final JmsTemplate inJmsTemplate)
    {
        jmsTemplate = inJmsTemplate;
        Assert.notNull(jmsTemplate, "JMS template cannot be null");
        Assert.notNull(jmsTemplate.getDefaultDestination(), "JMS template must have default destination set.");
    }

    /**
     * Puts a request into a message and places it on the queue.
     * 
     * @param inUserActionRequest
     *            the request
     */
    public void handleTask(final UserActionRequest inUserActionRequest)
    {
        jmsTemplate.send(new MessageCreator()
        {
            public Message createMessage(final Session session) throws JMSException
            {
                ObjectMessage message = session.createObjectMessage(inUserActionRequest);
                return message;
            }
        });
        logger.debug("Message sent to message broker");
    }
}
