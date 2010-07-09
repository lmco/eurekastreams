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
package org.eurekastreams.web.client.ui.common.stream;

import junit.framework.Assert;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageTextAreaChangedEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Test the post to stream model.
 */
public class PostToStreamModelTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The system under test.
     */
    private PostToStreamModel sut;

    /**
     * Stream scope.
     */
    private StreamScope streamScope = new StreamScope(ScopeType.PERSON, "foodId");

    /**
     * The maximum message length.
     */
    public static final int MAX_MESSAGE_LENGTH = 250;

    /**
     * Mock action processor.
     */
    private ActionProcessor processorMock = context.mock(ActionProcessor.class);

    /**
     * Mock.
     */
    private PostToPanel postTo;

    /**
     * Mock event bus.
     */
    private EventBus eventBusMock = context.mock(EventBus.class);

    /**
     * Setup the fixtures.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        postTo = context.mock(PostToPanel.class);
        sut = new PostToStreamModel(eventBusMock, processorMock, postTo);

    }

    /**
     * Tests the message property.
     */
    @Test
    public final void setMessageTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(eventBusMock).notifyObservers(with(any(MessageTextAreaChangedEvent.class)));
            }
        });

        sut.setMessage("the message");
        Assert.assertEquals("the message", sut.getMessage());

        context.assertIsSatisfied();
    }

    /**
     * Tests the remaining characters.
     */
    @Test
    public final void getRemainingMessageCharactersTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(eventBusMock).notifyObservers(with(any(MessageTextAreaChangedEvent.class)));
            }
        });

        Assert.assertEquals(MAX_MESSAGE_LENGTH, sut.getRemainingMessageCharacters());

        String theMessage = "this is some message";

        sut.setMessage(theMessage);

        Assert.assertEquals(MAX_MESSAGE_LENGTH - theMessage.length(), sut.getRemainingMessageCharacters());

        context.assertIsSatisfied();
    }

    /**
     * Tests the message length exceeded property.
     */
    @Test
    public final void canPostMessageTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(eventBusMock).notifyObservers(with(any(MessageTextAreaChangedEvent.class)));
            }
        });

        StringBuffer message = new StringBuffer();
        // make a message with length that is just acceptable:
        for (int i = 0; i < MAX_MESSAGE_LENGTH; i++)
        {
            message.append('a');
        }
        sut.setMessage(message.toString());
        Assert.assertTrue(sut.isMessageLengthAcceptable());

        sut.setMessage("");
        Assert.assertFalse(sut.isMessageLengthAcceptable());

        // make the message too long:
        message.append('a');

        Assert.assertFalse(sut.isMessageLengthAcceptable());

        context.assertIsSatisfied();
    }

    /**
     * Tests posting a message.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void postMessageTest()
    {
        final AnonymousClassInterceptor<AsyncCallback> cbInt = new AnonymousClassInterceptor<AsyncCallback>();
        final StreamScope scope = new StreamScope(ScopeType.PERSON, "username1");
        context.checking(new Expectations()
        {
            {
                oneOf(postTo).getPostScope();
                will(returnValue(scope));

                oneOf(processorMock).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBusMock).notifyObservers(with(any(MessageStreamAppendEvent.class)));
            }
        });

        sut.postMessage();

        cbInt.getObject().onSuccess(null);
        cbInt.getObject().onFailure(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests posting a message.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void postMessageTestWithFailure()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(postTo).getPostScope();
                will(returnValue(null));

                oneOf(eventBusMock).notifyObservers(with(any(ErrorPostingMessageToNullScopeEvent.class)));

            }
        });

        sut.postMessage();

        context.assertIsSatisfied();
    }

}
