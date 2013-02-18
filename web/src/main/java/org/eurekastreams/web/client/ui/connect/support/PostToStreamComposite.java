/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.support;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.DomainConversionUtility;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageTextAreaChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.stream.PostToPanel;
import org.eurekastreams.web.client.ui.common.stream.PostToStreamTextboxPanel;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.AddLinkComposite;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Post a message to the stream.
 */
public class PostToStreamComposite extends FlowPanel
{
    /** Default text to show when post box is empty. */
    private static final String DEFAULT_POST_BOX_DEFAULT_TEXT = "Something to share?";

    /** Text to show when post box is empty. */
    private final String postBoxDefaultText;

    /** Maximum length of a message. */
    private static final int MAX_MESSAGE_LENGTH = 250;

    /** The refresh interval in milliseconds. */
    private static final int REFRESH_INTERVAL = 250;

    /** The number of characters remaining. */
    private Label charsRemaining;

    /** The post button. */
    private Hyperlink postButton;

    /** The message. */
    private PostToStreamTextboxPanel message;

    /** The list of links in the message. */
    private AddLinkComposite links;

    /** The post to panel. */
    private PostToPanel postToPanel;

    /** Container for content warning. */
    private final FlowPanel contentWarningContainer = new FlowPanel();

    /** The content warning. */
    private FlowPanel contentWarning;

    /** error label. */
    private final Label errorMsg = new Label();

    /** panel holding controls below the post box. */
    private final FlowPanel expandedPanel = new FlowPanel();

    /** Current text in the message box. */
    private String messageText = "";

    /** The link information. */
    private Attachment attachment;

    /** The last fetched link. */
    private String lastFetched = "";

    /** The link text. */
    private final String linkText = "";

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
        this(inScope, DEFAULT_POST_BOX_DEFAULT_TEXT);
    }

    /**
     * Constructor.
     * 
     * @param inScope
     *            the scope.
     * @param inPostBoxDefaultText
     *            Text to show when box is empty.
     */
    public PostToStreamComposite(final StreamScope inScope, final String inPostBoxDefaultText)
    {
        postBoxDefaultText = inPostBoxDefaultText;
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
        postButton = new Hyperlink("", History.getToken());
        postButton.getElement().setAttribute("tabindex", "2");
        message = new PostToStreamTextboxPanel();
        message.setText(postBoxDefaultText);
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

        AvatarWidget avatar = new AvatarWidget(Session.getInstance().getCurrentPerson(), EntityType.PERSON,
                Size.VerySmall);
        avatar.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postEntryAvatar());

        Panel entryPanel = new FlowPanel();
        entryPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postEntryPanel());
        entryPanel.add(avatar);
        entryPanel.add(postInfoContainer);
        entryPanel.add(message);
        SimplePanel breakPanel = new SimplePanel();
        breakPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().breakClass());
        entryPanel.add(breakPanel);
        add(entryPanel);

        // below text area: links and post to on one line, then content warning below

        expandedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postExpandedPanel());

        postToPanel = new PostToPanel(inScope);
        expandedPanel.add(postToPanel);
        links = new AddLinkComposite();
        expandedPanel.add(links);

        contentWarning = new FlowPanel();
        contentWarningContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().contentWarning());
        contentWarningContainer.add(new SimplePanel());
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
        // user clicked in message text box
        message.addFocusHandler(new FocusHandler()
        {
            public void onFocus(final FocusEvent inEvent)
            {
                if ((" " + getStyleName() + " ").contains(StaticResourceBundle.INSTANCE.coreCss().small()))
                {
                    removeStyleName(StaticResourceBundle.INSTANCE.coreCss().small());
                    onExpand();
                }
            }
        });

        message.addValueChangedHandler(new ValueChangeHandler<String>()
        {
            public void onValueChange(final ValueChangeEvent<String> newValue)
            {
                checkMessageTextChanged();
            }
        });

        // user typed in message text box
        message.addKeystrokeHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && ev.isControlKeyDown()
                        && message.getText().length() > 0)
                {
                    checkMessageTextChanged();
                    handlePostMessage();
                }
                else
                {
                    checkMessageTextChanged();
                }
            }
        });

        // user clicked on post button
        postButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent ev)
            {
                checkMessageTextChanged();
                handlePostMessage();
            }
        });

        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent event)
            {
                errorMsg.setVisible(false);
                addStyleName(StaticResourceBundle.INSTANCE.coreCss().small());
                messageText = "";
                message.setText(postBoxDefaultText);
                onRemainingCharactersChanged();
                links.close();
            }
        });

        eventBus.addObserver(GotSystemSettingsResponseEvent.class, new Observer<GotSystemSettingsResponseEvent>()
        {
            public void update(final GotSystemSettingsResponseEvent event)
            {
                String warning = event.getResponse().getContentWarningText();
                if (warning != null && !warning.isEmpty())
                {
                    contentWarning.getElement().setInnerHTML(warning);
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
                // the value changed - make sure we're not stuck in the disabled, non-editable mode
                if ((" " + getStyleName() + " ").contains(StaticResourceBundle.INSTANCE.coreCss().small()))
                {
                    removeStyleName(StaticResourceBundle.INSTANCE.coreCss().small());
                }
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
    }

    /**
     * Invoked when the composite is expanded (due to user clicking on the box).
     */
    protected void onExpand()
    {
        message.setText("");
    }

    /**
     * Sets up the magic show/hide for the publisher.
     */
    public void setUpMinimizer()
    {
        nativeSetUpMinimizer(postBoxDefaultText);
    }

    /**
     * Sets up the magic show/hide for the publisher.
     * 
     * @param inDefaultMessage
     *            Message to display when box is empty.
     */
    private static native void nativeSetUpMinimizer(final String inDefaultMessage)
    /*-{
           $wnd.defaultMessage = inDefaultMessage;
           $wnd.overPoster = false;
           $doc.onmousedown = function() {
               if(!$wnd.overPoster && $wnd.jQuery(".post-button").is(".inactive"))
               {
                   setTimeout(function() { $wnd.jQuery('#post-to-stream').addClass('small'); },500);
                   setTimeout(function() { $wnd.jQuery('#post-to-stream textarea').val($wnd.defaultMessage); },500);
               }
               else if($wnd.overPoster && $wnd.jQuery("#post-to-stream").is(".small"))
               {
                   $wnd.jQuery('#post-to-stream textarea').focus();
               }

           };

           $wnd.jQuery("#post-to-stream").hover(
               function() { $wnd.overPoster = true; },
               function() { $wnd.overPoster = false; });
       }-*/;

    /**
     * Handle the post activity action - triggered by CONTROL-ENTER or clicking the Post button.
     */
    private void handlePostMessage()
    {
        if (!postButton.getStyleName().contains("inactive") && messageText.length() <= MAX_MESSAGE_LENGTH
                && (!messageText.isEmpty() || attachment != null))
        {
            hidePostButton();
            postMessage();
            lastFetched = "";
        }
    }

    /**
     * Get the scope.
     * 
     * @return the scope.
     */
    public StreamScope getScope()
    {
        return postToPanel.getPostScope();
    }

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
     * @return The user-entered text.
     */
    protected String getMesssageText()
    {
        return messageText;
    }

    /**
     * @return The panel below the text entry box.
     */
    protected FlowPanel getSubTextboxPanel()
    {
        return expandedPanel;
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

        EntityType recipientType = DomainConversionUtility.convertToEntityType(scope.getScopeType());
        if (EntityType.NOTSET.equals(recipientType))
        {
            recipientType = EntityType.GROUP;
        }

        ActivityDTOPopulatorStrategy objectStrat = attachment != null ? attachment.getPopulator() : new NotePopulator();
        ActivityDTO activity = activityPopulator.getActivityDTO(messageText, recipientType, scope.getUniqueKey(),
                new PostPopulator(), objectStrat);
        PostActivityRequest postRequest = new PostActivityRequest(activity);

        ActivityModel.getInstance().insert(postRequest);
    }
}
