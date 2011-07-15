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

import javax.jms.Destination;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.QueueTaskHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * Test for QueueTaskHandler class.
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
     * {@link JmsTemplate}.
     */
    private JmsTemplate jmsTemplateMock = context.mock(JmsTemplate.class);

    /**
     * {@link Destination}.
     */
    private Destination queueMock = context.mock(Destination.class);

    /**
     * {@link UserActionRequest}.
     */
    private UserActionRequest userActionRequestMock = context.mock(UserActionRequest.class);

    /**
     * {@link QueueTaskHandler}.
     */
    private QueueTaskHandler sut = null;

    /**
     * Test.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullJmsTemplate()
    {
        sut = new QueueTaskHandler(null);
    }

    /**
     * Test.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testJmsTemplateWithNullDefaultDestination()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(jmsTemplateMock).getDefaultDestination();
                will(returnValue(null));
            }
        });

        sut = new QueueTaskHandler(jmsTemplateMock);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testHandleTask()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(jmsTemplateMock).getDefaultDestination();
                will(returnValue(queueMock));

                oneOf(jmsTemplateMock).send(with(any(MessageCreator.class)));
            }
        });

        sut = new QueueTaskHandler(jmsTemplateMock);
        sut.handleTask(userActionRequestMock);

        context.assertIsSatisfied();
    }

}
