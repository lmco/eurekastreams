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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageTextAreaChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ParseLinkEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.AddLinkComposite;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.Bookmark;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Post a message to the stream.
 */
public class PostToStreamComposite extends FlowPanel
{
    /** Maximum length of a message. */
    private static final int MAX_MESSAGE_LENGTH = 250;

    /** The refresh interval in milliseconds. */
    private static final int REFRESH_INTERVAL = 250;

    /** The number of characters remaining. */
    private Label charsRemaining;

    /** The post button. */
    private Label postButton;

    /** The message. */
    private PostToStreamTextboxPanel message;

    /** The list of links in the message. */
    private AddLinkComposite links;

    /** The post to panel. */
    private PostToPanel postToPanel;

    /** Container for content warning. */
    private final FlowPanel contentWarningContainer = new FlowPanel();

    /** The content warning. */
    private Label contentWarning;

    /** error label. */
    private final Label errorMsg = new Label();

    /** Current text in the message box. */
    private String messageText = "";

    /** The link information. */
    private Attachment attachment;

    /** The last fetched link. */
    private String lastFetched = "";

    /** The link text. */
    private String linkText = "";

    /** Activity Populator. */
    private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

    /**
     * Constructor.
     * 
     * @param inScope
     *            the scope.
     */
    public PostToStreamComposite(final StreamScope inScope)
    {
        setupWidgets(inScope);
        setupEvents();

        SystemSettingsModel.getInstance().fetch(null, true);
        onRemainingCharactersChanged();
    }

    /**
     * Builds the UI.
     * 
     * @param inScope
     *            the scope.
     */
    private void setupWidgets(final StreamScope inScope)
    {
        this.getElement().setAttribute("id", "post-to-stream");
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().small());

        charsRemaining = new Label();
        postButton = new Label("Post");
        message = new PostToStreamTextboxPanel();
        message.setText("Something to share?");
        message.setVisible(false); // Hide until post ready event.

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postToStream());
        errorMsg.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formErrorBox());
        errorMsg.setVisible(false);
        this.add(errorMsg);

        FlowPanel postInfoContainer = new FlowPanel();
        postInfoContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postInfoContainer());

        postButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postButton());
        postInfoContainer.add(postButton);

        charsRemaining.addStyleName(StaticResourceBundle.INSTANCE.coreCss().charactersRemaining());
        postInfoContainer.add(charsRemaining);

        Panel entryPanel = new FlowPanel();
        entryPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postEntryPanel());
        entryPanel.add(message);
        entryPanel.add(postInfoContainer);
        add(entryPanel);

        // below text area: links and post to on one line, then content warning below
        Panel expandedPanel = new FlowPanel();
        expandedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postExpandedPanel());

        postToPanel = new PostToPanel(inScope);
        expandedPanel.add(postToPanel);
        links = new AddLinkComposite();
        expandedPanel.add(links);

        contentWarning = new Label();
        contentWarningContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().contentWarning());
        contentWarningContainer.add(contentWarning);
        expandedPanel.add(contentWarningContainer);

        add(expandedPanel);

        setVisible(false);

        setVisible(Session.getInstance().getCurrentPerson() != null);
    }

    /**
     * Wires up events.
     */
    private void setupEvents()
    {
        // user typed in message text box
        message.addKeystrokeHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent inArg0)
            {
                checkMessageTextChanged();
            }
        });

        // user clicked on post button
        postButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent ev)
            {
                checkMessageTextChanged();

                if (!postButton.getStyleName().contains(StaticResourceBundle.INSTANCE.coreCss().inactive())
                        && messageText.length() <= MAX_MESSAGE_LENGTH && (!messageText.isEmpty() || attachment != null))
                {
                    hidePostButton();
                    postMessage();
                    lastFetched = "";
                }
            }
        });

        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent event)
            {
                addStyleName(StaticResourceBundle.INSTANCE.coreCss().small());
                message.setText("Something to share?");
            }
        });

        eventBus.addObserver(GotSystemSettingsResponseEvent.class, new Observer<GotSystemSettingsResponseEvent>()
        {
            public void update(final GotSystemSettingsResponseEvent event)
            {
                String warning = event.getResponse().getContentWarningText();
                if (warning != null && !warning.isEmpty())
                {
                    contentWarning.setText(warning);
                }
                else
                {
                    contentWarning.setVisible(false);
                }
                message.setVisible(true);
            }
        });

        eventBus.addObserver(MessageTextAreaChangedEvent.getEvent(), new Observer<MessageTextAreaChangedEvent>()
        {
            public void update(final MessageTextAreaChangedEvent arg1)
            {
                onRemainingCharactersChanged();
            }
        });

        eventBus.addObserver(MessageAttachmentChangedEvent.class, new Observer<MessageAttachmentChangedEvent>()
        {
            public void update(final MessageAttachmentChangedEvent evt)
            {
                errorMsg.setVisible(false);
                attachment = evt.getAttachment();
                if (attachment == null && messageText.isEmpty())
                {
                    hidePostButton();
                }
                else
                {
                    showPostButton();
                }
            }
        });

        eventBus.addObserver(new ErrorPostingMessageToNullScopeEvent(),
                new Observer<ErrorPostingMessageToNullScopeEvent>()
                {
                    public void update(final ErrorPostingMessageToNullScopeEvent event)
                    {
                        errorMsg.setText(event.getErrorMsg());
                        errorMsg.setVisible(true);
                        showPostButton();
                    }
                });

        eventBus.addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent evt)
            {
                hidePostButton();
                errorMsg.setVisible(false);
                message.setText("");
                links.close();
                checkMessageTextChanged();
            }
        });

        // Poll for links. This needs to be done because onChange in a text area in Firefox is only called onBlur.
        Timer timer = new Timer()
        {
            @Override
            public void run()
            {
                if (!getStyleName().contains(StaticResourceBundle.INSTANCE.coreCss().small()))
                {
                    checkForLinks();
                }
            }
        };
        timer.scheduleRepeating(REFRESH_INTERVAL);
    }

    /**
     * Sets up the magic show/hide for the publisher.
     */
    public static native void setUpMinimizer()
    /*-{
           $wnd.overPoster = false;
           $doc.onmousedown = function() {
               if(!$wnd.overPoster && $wnd.jQuery(".post-button").is(".inactive"))
               {
                   setTimeout("$wnd.jQuery('#post-to-stream').addClass('small');",500);
                   setTimeout("$wnd.jQuery('#post-to-stream textarea').val('Something to share?');",500);
               }
               else if($wnd.overPoster && $wnd.jQuery("#post-to-stream").is(".small"))
               {
                   $wnd.jQuery('#post-to-stream textarea').focus();
               }

           };

               $wnd.jQuery("#post-to-stream textarea").focus(function() {
                   if($wnd.jQuery("#post-to-stream").is(".small")) {
                       $wnd.jQuery("#post-to-stream").removeClass("small");
                       $wnd.jQuery("#post-to-stream textarea").val("");
                   }
               });

               $wnd.jQuery("#post-to-stream").hover(
                   function() { $wnd.overPoster = true; },
                   function() { $wnd.overPoster = false; });
       }-*/;

    /**
     * Set the scope.
     * 
     * @param inScope
     *            the scope.
     */
    public void setScope(final StreamScope inScope)
    {
        postToPanel.setPostScope(inScope);
    }

    /**
     * Determine if message text changed and handle appropriately.
     */
    private void checkMessageTextChanged()
    {
        String newText = message.getText();
        if (!newText.equals(messageText))
        {
            messageText = newText;

            Session.getInstance().getEventBus().notifyObservers(MessageTextAreaChangedEvent.getEvent());
        }
    }

    /**
     * Checks for links and sets the model message.
     */
    private void checkForLinks()
    {
        if (!getStyleName().contains(StaticResourceBundle.INSTANCE.coreCss().small()))
        {
            if (attachment == null || !(attachment instanceof Bookmark))
            {
                checkMessageTextChanged();

                String tmpLinkText = "";
                String[] words = messageText.split("\\s+");
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
                    Session.getInstance().getEventBus().notifyObservers(new ParseLinkEvent(linkText));
                }

                linkText = tmpLinkText;
            }
        }
    }

    /**
     * Sets the remaining number of characters.
     */
    private void onRemainingCharactersChanged()
    {
        final int textLength = messageText.length();

        charsRemaining.setText(Integer.toString(MAX_MESSAGE_LENGTH - textLength));

        if (textLength <= MAX_MESSAGE_LENGTH && (!messageText.isEmpty() || links.hasAttachment()))
        {
            showPostButton();
        }
        else
        {
            hidePostButton();
        }

        if (textLength > MAX_MESSAGE_LENGTH)
        {
            charsRemaining.addStyleName(StaticResourceBundle.INSTANCE.coreCss().overCharacterLimit());
        }
        else
        {
            charsRemaining.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().overCharacterLimit());
        }
    }

    /**
     * Hides the post button.
     */
    private void hidePostButton()
    {
        postButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().inactive());
    }

    /**
     * Displays the post button.
     */
    private void showPostButton()
    {
        postButton.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().inactive());
    }

    /**
     * Posts the message.
     */
    public void postMessage()
    {
        StreamScope scope = postToPanel.getPostScope();
        if (scope == null)
        {
            ErrorPostingMessageToNullScopeEvent error = new ErrorPostingMessageToNullScopeEvent();
            error.setErrorMsg("The stream name you entered could not be found");
            Session.getInstance().getEventBus().notifyObservers(error);
            return;
        }

        EntityType recipientType = ScopeType.PERSON.equals(scope.getScopeType()) ? EntityType.PERSON : EntityType.GROUP;

        ActivityDTOPopulatorStrategy objectStrat = attachment != null ? attachment.getPopulator() : new NotePopulator();

        PostActivityRequest postRequest = new PostActivityRequest(activityPopulator.getActivityDTO(messageText,
                recipientType, scope.getUniqueKey(), new PostPopulator(), objectStrat));

        ActivityModel.getInstance().insert(postRequest);
    }
}
