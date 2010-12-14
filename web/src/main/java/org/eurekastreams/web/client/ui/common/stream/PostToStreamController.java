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

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageTextAreaChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ParseLinkEvent;
import org.eurekastreams.web.client.events.PostReadyEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.Bookmark;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * Post to stream controller.
 */
public class PostToStreamController
{
    /**
     * The view.
     */
    private PostToStreamView view;

    /**
     * The model.
     */
    private PostToStreamModel model;

    /**
     * The last fetched link.
     */
    private String lastFetched = "";

    /**
     * The link text.
     */
    private String linkText = "";

    /**
     * The widget.
     */
    private PostToStreamComposite widget;

    /**
     * The event bus.
     */
    private EventBus eventBus;

    /**
     * Constructor.
     * 
     * @param inEventBus
     *            the event bus.
     * @param inView
     *            the view.
     * @param inModel
     *            the model.
     * @param inWidget
     *            the composite widget.
     */
    public PostToStreamController(final EventBus inEventBus, final PostToStreamView inView,
            final PostToStreamModel inModel, final PostToStreamComposite inWidget)
    {
        eventBus = inEventBus;
        view = inView;
        model = inModel;
        widget = inWidget;
    }

    /**
     * Initialize the events.
     */
    @SuppressWarnings("deprecation")
    public void init()
    {
        view.onRemainingCharactersChanged();

        view.addMessageKeyboardListener(new KeyboardListener()
        {

            public void onKeyDown(final Widget sender, final char keyCode, final int modifiers)
            {
                // Purposely left blank.
            }

            public void onKeyPress(final Widget sender, final char keyCode, final int modifiers)
            {
                // Purposely left blank.
            }

            public void onKeyUp(final Widget sender, final char keyCode, final int modifiers)
            {
                model.setMessage(((TextArea) sender).getText());
            }
        });

        view.addPostClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                if ((model.isMessageLengthAcceptable() || (model.getAttachment() != null && model
                        .getRemainingMessageCharacters() >= 0))
                        && view.canPost())
                {
                    model.setMessage(view.getMessage());
                    view.hidePostButton();
                    model.postMessage();
                    lastFetched = "";
                }
            }
        });

        eventBus.addObserver(MessageTextAreaChangedEvent.getEvent(), new Observer<MessageTextAreaChangedEvent>()
        {
            public void update(final MessageTextAreaChangedEvent arg1)
            {
                view.onRemainingCharactersChanged();
            }
        });

        eventBus.addObserver(MessageAttachmentChangedEvent.class, new Observer<MessageAttachmentChangedEvent>()
        {
            public void update(final MessageAttachmentChangedEvent evt)
            {
                view.hideError();
                Attachment attachment = evt.getAttachment();
                model.setAttachment(attachment);
                if (attachment == null && model.getMessage().length() == 0)
                {
                    view.hidePostButton();
                }
                else
                {
                    view.showPostButton();
                }
            }
        });

        eventBus.addObserver(new ErrorPostingMessageToNullScopeEvent(),
                new Observer<ErrorPostingMessageToNullScopeEvent>()
                {
                    public void update(final ErrorPostingMessageToNullScopeEvent event)
                    {
                        view.showError(event.getErrorMsg());
                    }
                });

        eventBus.addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent evt)
            {
                view.hidePostButton();
                view.hideError();
                view.clearMessage();
                model.setMessage("");
            }
        });

        eventBus.addObserver(PostReadyEvent.class, new Observer<PostReadyEvent>()
        {

            public void update(final PostReadyEvent event)
            {
                view.onPostReady(event.getContentWarning());
            }
        });
    }

    /**
     * Checks for links and sets the model message.
     */
    public void checkForLinks()
    {
        if (!widget.getStyleName().contains("small"))
        {
            if (null == model.getAttachment() && model.getAttachment() instanceof Bookmark)
            {
                /**
                 * Done to handle mouse based cut and paste.
                 */
                String message = view.getMessage();

                model.setMessage(message);
                String[] words = message.split("\\s+");

                String tmpLinkText = "";

                for (String word : words)
                {
                    if (word.startsWith("http://") || word.startsWith("https://"))
                    {
                        tmpLinkText = word;

                        // Break after the first link is found.
                        break;
                    }
                    else if (word.startsWith("www."))
                    {
                        tmpLinkText = "http://" + word;

                        // Break after the first link is found.
                        break;
                    }

                }

                if (lastFetched != linkText && tmpLinkText == linkText && tmpLinkText.length() > 7)
                {
                    lastFetched = linkText;
                    eventBus.notifyObservers(new ParseLinkEvent(linkText));
                }

                linkText = tmpLinkText;
            }
        }

    }
}
