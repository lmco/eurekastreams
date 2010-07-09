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

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageTextAreaChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PostReadyEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.Bookmark;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Test the post to stream controller.
 */
public class PostToStreamControllerTest
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
    private PostToStreamController sut;

    /**
     * The model mock.
     */
    private PostToStreamModel modelMock = context.mock(PostToStreamModel.class);

    /**
     * The view mock.
     */
    private PostToStreamView viewMock = context.mock(PostToStreamView.class);

    /**
     * Post click anonymous class intercepter.
     */
    private AnonymousClassInterceptor<ClickListener> postClickInt = new AnonymousClassInterceptor<ClickListener>();

    /**
     * Sequence.
     */
    private Sequence sequence = context.sequence("expected");

    /**
     * Keyboard click interceptor.
     */
    private AnonymousClassInterceptor<KeyboardListener> keyboardInt = new AnonymousClassInterceptor<KeyboardListener>();

    /**
     * Mock event bus.
     */
    private EventBus eventBusMock = context.mock(EventBus.class);

    /**
     * Mock widget.
     */
    private PostToStreamComposite widgetMock = null;

    /**
     * Sender of the keyboard click event.
     */
    private TextArea eventSender;

    /**
     * Intercepts MessageTextAreaChangedEvent.
     */
    private AnonymousClassInterceptor<Observer<MessageTextAreaChangedEvent>>
    intMessageTextAreaChangedEvent = new AnonymousClassInterceptor<Observer<MessageTextAreaChangedEvent>>();

    /**
     * Intercepts MessageLinkChangedEvent.
     */
    private AnonymousClassInterceptor<Observer<MessageAttachmentChangedEvent>>
    intMessageLinkChangedEvent = new AnonymousClassInterceptor<Observer<MessageAttachmentChangedEvent>>();

    /**
     * Intercepts ErrorPostingMessageToNullScopeEvent.
     */
    private AnonymousClassInterceptor<Observer<ErrorPostingMessageToNullScopeEvent>>
    intErrorPostingMessageToNullScopeEvent =
        new AnonymousClassInterceptor<Observer<ErrorPostingMessageToNullScopeEvent>>();

    /**
     * Intercepts MessageStreamAppendEvent.
     */
    private AnonymousClassInterceptor<Observer<MessageStreamAppendEvent>>
    intMessageStreamAppendEvent = new AnonymousClassInterceptor<Observer<MessageStreamAppendEvent>>();

    /**
     * Intercepts PostReadyEvent.
     */
    private AnonymousClassInterceptor<Observer<PostReadyEvent>>
    intPostReadyEvent = new AnonymousClassInterceptor<Observer<PostReadyEvent>>();

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        widgetMock = context.mock(PostToStreamComposite.class);
        eventSender = context.mock(TextArea.class);
        sut = new PostToStreamController(eventBusMock, viewMock, modelMock,
                widgetMock);

        context.checking(new Expectations()
        {
            {
                oneOf(viewMock).onRemainingCharactersChanged();
                inSequence(sequence);

                oneOf(viewMock).addMessageKeyboardListener(
                        with(any(KeyboardListener.class)));
                will(keyboardInt);
                inSequence(sequence);

                oneOf(viewMock).addPostClickListener(
                        with(any(ClickListener.class)));
                will(postClickInt);

                oneOf(eventBusMock).addObserver(
                        with(any(MessageTextAreaChangedEvent.class)),
                        with(any(Observer.class)));
                will(intMessageTextAreaChangedEvent);

                oneOf(eventBusMock).addObserver(
                        with(equal(MessageAttachmentChangedEvent.class)),
                        with(any(Observer.class)));
                will(intMessageLinkChangedEvent);

                oneOf(eventBusMock).addObserver(
                        with(any(ErrorPostingMessageToNullScopeEvent.class)),
                        with(any(Observer.class)));
                will(intErrorPostingMessageToNullScopeEvent);

                oneOf(eventBusMock).addObserver(
                        with(equal(MessageStreamAppendEvent.class)),
                        with(any(Observer.class)));
                will(intMessageStreamAppendEvent);

                oneOf(eventBusMock).addObserver(
                        with(equal(PostReadyEvent.class)),
                        with(any(Observer.class)));
                will(intPostReadyEvent);


            }
        });

        sut.init();
    }

    /**
     * Tests the message text area changed event.
     */
    @Test
    public final void intMessageTextAreaChangedEventTest()
    {
        final MessageTextAreaChangedEvent event = MessageTextAreaChangedEvent
                .getEvent();

        context.checking(new Expectations()
        {
            {
                oneOf(viewMock).onRemainingCharactersChanged();
            }
        });

        intMessageTextAreaChangedEvent.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the message link changed event.
     */
    @Test
    public final void intMessageLinkChangedEventTest()
    {
        final MessageAttachmentChangedEvent event = context
                .mock(MessageAttachmentChangedEvent.class);

        context.checking(new Expectations()
        {
            {
                final Attachment attachment = context.mock(Attachment.class);
                oneOf(event).getAttachment();
                will(returnValue(attachment));
                oneOf(viewMock).hideError();
                oneOf(modelMock).setAttachment(attachment);
                oneOf(viewMock).showPostButton();

            }
        });

        intMessageLinkChangedEvent.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the error posting message event.
     */
    @Test
    public final void intErrorPostingMessageToNullScopeEventTest()
    {
        final ErrorPostingMessageToNullScopeEvent event = context
                .mock(ErrorPostingMessageToNullScopeEvent.class);

        context.checking(new Expectations()
        {
            {
                final String errMsg = "some error";

                oneOf(event).getErrorMsg();
                will(returnValue(errMsg));

                oneOf(viewMock).showError(errMsg);
            }
        });

        intErrorPostingMessageToNullScopeEvent.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Test appending a message to the stream event.
     */
    @Test
    public final void intMessageStreamAppendEventTest()
    {
        final MessageStreamAppendEvent event = context
                .mock(MessageStreamAppendEvent.class);

        context.checking(new Expectations()
        {
            {
                oneOf(viewMock).hidePostButton();
                oneOf(viewMock).hideError();
                oneOf(viewMock).clearMessage();
                oneOf(modelMock).setMessage("");
            }
        });

        intMessageStreamAppendEvent.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the post message ready event.
     */
    @Test
    public final void intPostReadyEventTest()
    {
        final PostReadyEvent event = context.mock(PostReadyEvent.class);

        context.checking(new Expectations()
        {
            {
                final String contentWarning = "Be mindful of what you post!";

                oneOf(event).getContentWarning();
                will(returnValue(contentWarning));

                oneOf(viewMock).onPostReady(contentWarning);
            }
        });

        intPostReadyEvent.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the init method.
     */
    @Test
    public final void initTest()
    {
        context.assertIsSatisfied();
    }

    /**
     * Tests check for links method.
     */
    @Test
    public final void checkForLinksTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(widgetMock).getStyleName();
                will(returnValue(""));

                oneOf(modelMock).getAttachment();
            }
        });

        sut.checkForLinks();

        context.assertIsSatisfied();
    }

    /**
     * Tests check for links method when the widget is "disabled".
     */
    @Test
    public final void checkForLinksDisabledTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(widgetMock).getStyleName();
                will(returnValue("small"));

                never(viewMock).onRemainingCharactersChanged();
                never(modelMock).getAttachment();
            }
        });

        sut.checkForLinks();

        context.assertIsSatisfied();
    }

    /**
     * Tests the keyboard events.
     */
    @Test
    public final void keyboardEventTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(eventSender).getText();
                will(returnValue("Something"));
                inSequence(sequence);

                oneOf(modelMock).setMessage("Something");
                inSequence(sequence);
            }
        });

        keyboardInt.getObject().onKeyUp(eventSender, 'a', 0);

        // These do nothing, needed for 100% test coverage.
        keyboardInt.getObject().onKeyDown(eventSender, 'a', 0);
        keyboardInt.getObject().onKeyPress(eventSender, 'a', 0);

        context.assertIsSatisfied();
    }

    /**
     * Tests clicking the post button.
     */
    @Test
    public final void postClickGoodMessageTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(true));
                inSequence(sequence);
                
                oneOf(viewMock).canPost();
                will(returnValue(true));

                oneOf(viewMock).hidePostButton();
                oneOf(modelMock).postMessage();
                inSequence(sequence);
            }
        });

        postClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests clicking the post button with an unacceptable message length and no
     * link.
     */
    @Test
    public final void postClickBadMessageTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(false));

                oneOf(modelMock).getAttachment();
                will(returnValue(null));
            }
        });

        postClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests clicking the post button with an unacceptable message length and
     * link over the char limit.
     */
    @Test
    public final void postClickBadMessageWithLinkOverLimitTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(false));

                oneOf(modelMock).getAttachment();
                will(returnValue(new Bookmark(new LinkInformation())));

                oneOf(modelMock).getRemainingMessageCharacters();
                will(returnValue(-1));

            }
        });

        postClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests clicking the post button with an unacceptable message length and
     * link under the char limit.
     */
    @Test
    public final void postClickBadMessageWithLinkUnderLimitTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(false));

                oneOf(modelMock).getAttachment();
                will(returnValue(new Bookmark(new LinkInformation())));
                
                oneOf(viewMock).canPost();
                will(returnValue(true));

                oneOf(modelMock).getRemainingMessageCharacters();
                will(returnValue(0));
                
                oneOf(viewMock).hidePostButton();

                oneOf(modelMock).postMessage();
            }
        });

        postClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }
}
