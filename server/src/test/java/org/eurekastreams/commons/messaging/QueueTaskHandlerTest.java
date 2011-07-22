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

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.QueueTaskHandler;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for MessageConsumer class.
 * 
 */
public class QueueTaskHandlerTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /**
     * Mock objects.
     */

    /**
     * The JMS template mock object.
     */
    private ConnectionFactory connectionFactoryMock = context.mock(ConnectionFactory.class);

    /**
     * The JMS queue mock object.
     */
    private Queue queueMock = context.mock(Queue.class);

    /**
     * The User Action Request mock.
     */
    private UserActionRequest userActionRequestMock = context.mock(UserActionRequest.class);

    /**
     * SUT.
     */
    private QueueTaskHandler sut = null;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new QueueTaskHandler();
        sut.setConnectionFactory(connectionFactoryMock);
        sut.setQueue(queueMock);
    }

    /**
     * Test onMessage(message).
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void testOnMessage() throws Exception
    {
        // TODO: Understand the best way to implement a test for this very simple class/method.
        // sut.execute(userActionRequestMock);
    }

}