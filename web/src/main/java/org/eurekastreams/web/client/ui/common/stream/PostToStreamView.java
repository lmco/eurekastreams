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

import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.AddLinkComposite;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;

/**
 * Post to stream view.
 */
public class PostToStreamView implements Bindable
{
    /**
     * Error message.
     */
    Label errorMsg;
    /**
     * The number of characters remaining.
     */
    Label charsRemaining;

    /**
     * The post button.
     */
    Label postButton;

    /**
     * The message.
     */
    PostToStreamTextboxPanel message;

    /**
     * The content warning.
     */
    Label contentWarning;

    /**
     * The list of links in the message.
     */
    AddLinkComposite links;

    /**
     * The model.
     */
    private PostToStreamModel model;

    /**
     * Constructor.
     *
     * @param inModel
     *            the model.
     */
    public PostToStreamView(final PostToStreamModel inModel)
    {
        model = inModel;
    }

    /**
     * Adds a message keyboard listener.
     *
     * @param listener
     *            the change listener.
     */
    public void addMessageKeyboardListener(final KeyboardListener listener)
    {
        message.addKeyboardListener(listener);
    }

    /**
     * Sets the remaining number of characters.
     */
    public void onRemainingCharactersChanged()
    {
        charsRemaining.setText(Integer.toString(model.getRemainingMessageCharacters()));

        if (model.isMessageLengthAcceptable() || (links.hasAttachment() && model.getRemainingMessageCharacters() >= 0))
        {
            showPostButton();
        }
        else
        {
            hidePostButton();
        }

        if (model.getRemainingMessageCharacters() < 0)
        {
            charsRemaining.addStyleName("over-character-limit");
        }
        else
        {
            charsRemaining.removeStyleName("over-character-limit");
        }
    }

    /**
     * Hides the post button.
     */
    public void hidePostButton()
    {
        postButton.addStyleName("inactive");
    }

    /**
     * Displays the post button.
     */
    public void showPostButton()
    {
        postButton.removeStyleName("inactive");
    }

    /**
     * Calculates if the UI is in a state that it can post. This helps eliminates the ability for a double post
     * of an activity.
     * @return false if the post button is inactive, or true otherwise.
     */
    public boolean canPost()
    {
        return !postButton.getStyleName().contains("inactive");
    }

    /**
     * Adds a listener to the post button.
     *
     * @param listener
     *            the listener.
     */
    public void addPostClickListener(final ClickListener listener)
    {
        postButton.addClickListener(listener);
    }

    /**
     * Clears the message box.
     */
    public void clearMessage()
    {
        message.setText("");
        links.close();
    }

    /**
     * Shows the error.
     * @param errorMsgString the error.
     */
    public void showError(final String errorMsgString)
    {
        errorMsg.setText(errorMsgString);
        errorMsg.setVisible(true);
        showPostButton();
    }

    /**
     * Hides the error.
     */
    public void hideError()
    {
        errorMsg.setVisible(false);
    }

    /**
     * @return the message text.
     */
    public String getMessage()
    {
        return message.getText();
    }

    /**
     * Called when posting is ready.
     * @param inContentWarning the warning.
     */
    public void onPostReady(final String inContentWarning)
    {
        if (null != inContentWarning && inContentWarning.length() > 0)
        {
            contentWarning.setText(inContentWarning);
        }
        else
        {
            contentWarning.setVisible(false);
        }

        message.setVisible(true);
    }
}
