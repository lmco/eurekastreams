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

import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.AddLinkComposite;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Test the post to stream view.
 */
public class PostToStreamViewTest
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
    private PostToStreamView sut;

    /**
     * The model mock.
     */
    private PostToStreamModel modelMock = context.mock(PostToStreamModel.class);

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new PostToStreamView(modelMock);
        GWTMockUtilities.disarm();

        sut.message = context.mock(TextArea.class);
        sut.charsRemaining = context.mock(Label.class, "charsRemaining");
        sut.postButton = context.mock(Label.class, "postButton");
        sut.links = context.mock(AddLinkComposite.class, "links");
        sut.errorMsg = context.mock(Label.class, "error");
    }

    /**
     * Tests adding a message keyboard listener.
     */
    @Test
    public final void addMessageKeyboardListenerTest()
    {
        final KeyboardListener listener = context.mock(KeyboardListener.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.message).addKeyboardListener(listener);
            }
        });

        sut.addMessageKeyboardListener(listener);

        context.assertIsSatisfied();
    }

    /**
     * Test when the remaining character limit is positive.
     */
    @Test
    public final void onRemainingCharactersChangedTest()
    {

        context.checking(new Expectations()
        {
            {
                allowing(modelMock).getRemainingMessageCharacters();
                will(returnValue(2));

                oneOf(sut.charsRemaining).setText("2");

                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(true));

                oneOf(sut.charsRemaining).removeStyleName("over-character-limit");
                oneOf(sut.postButton).removeStyleName("inactive");
            }
        });

        sut.onRemainingCharactersChanged();

        context.assertIsSatisfied();
    }

    /**
     * Test when the remaining character limit is negative but it has a link.
     */
    @Test
    public final void onRemainingCharactersChangedLengthExceededHasLinkTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(modelMock).getRemainingMessageCharacters();
                will(returnValue(-1));

                oneOf(sut.charsRemaining).setText("-1");

                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(false));

                oneOf(sut.links).hasAttachment();
                will(returnValue(true));

                oneOf(sut.charsRemaining).addStyleName("over-character-limit");
                oneOf(sut.postButton).addStyleName("inactive");
            }
        });

        sut.onRemainingCharactersChanged();

        context.assertIsSatisfied();
    }

    /**
     * Test when no characters are entered but it has a link.
     */
    @Test
    public final void onRemainingCharactersChangedNoCharHasLinkTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(modelMock).getRemainingMessageCharacters();
                will(returnValue(0));

                oneOf(sut.charsRemaining).setText("0");

                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(false));

                oneOf(sut.links).hasAttachment();
                will(returnValue(true));

                oneOf(sut.charsRemaining).removeStyleName("over-character-limit");
                oneOf(sut.postButton).removeStyleName("inactive");
            }
        });

        sut.onRemainingCharactersChanged();

        context.assertIsSatisfied();
    }

    /**
     * Test when the remaining character limit is negative.
     */
    @Test
    public final void onRemainingCharactersChangedLengthExceededTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(modelMock).getRemainingMessageCharacters();
                will(returnValue(-1));

                oneOf(sut.charsRemaining).setText("-1");

                oneOf(modelMock).isMessageLengthAcceptable();
                will(returnValue(false));

                oneOf(sut.links).hasAttachment();
                will(returnValue(false));

                oneOf(sut.charsRemaining).addStyleName("over-character-limit");
                oneOf(sut.postButton).addStyleName("inactive");
            }
        });

        sut.onRemainingCharactersChanged();

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public final void showError()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.errorMsg).setText("test");
                oneOf(sut.errorMsg).setVisible(true);
                oneOf(sut.postButton).removeStyleName("inactive");
            }
        });

        sut.showError("test");

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public final void hideError()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.errorMsg).setVisible(false);
            }
        });

        sut.hideError();

        context.assertIsSatisfied();
    }

}
