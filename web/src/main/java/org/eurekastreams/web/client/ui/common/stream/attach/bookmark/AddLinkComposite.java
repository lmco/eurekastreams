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
package org.eurekastreams.web.client.ui.common.stream.attach.bookmark;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ParseLinkEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.stream.thumbnail.ThumbnailSelectorComposite;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Add link panel.
 */
public class AddLinkComposite extends FlowPanel implements Bindable
{
    /**
     * Add link button.
     */
    Label addLink = new Label("attach link");

    /**
     * Add link button.
     */
    Label fetchLink = new Label("Go");

    /**
     * The add link panel.
     */
    FlowPanel addPanel = new FlowPanel();

    /**
     * The URL to the link.
     */
    LabeledTextBox linkUrl = new LabeledTextBox("http://");

    /**
     * The display link panel.
     */
    FlowPanel displayPanel = new FlowPanel();

    /**
     * The link panel.
     */
    FlowPanel linkPanel = new FlowPanel();

    /**
     * Thumbnail selector.
     */
    ThumbnailSelectorComposite selector = new ThumbnailSelectorComposite();

    /**
     * Link URL.
     */
    Label linkUrlDisplay = new Label();

    /**
     * The title link.
     */
    FlowPanel titleLink = new FlowPanel();

    /**
     * Link Description.
     */
    Label linkDesc = new Label();

    /**
     * The close button.
     */
    Label closeDisplayButton = new Label("close");

    /**
     * The close button.
     */
    Label closeAddButton = new Label("close");

    /**
     * Text box.
     */
    TextBox title = new TextBox();


    /**
     * The last fetched links url.
     */
    private String fetchedLink = "";

    /**
     * Max length of title.
     */
    private static final int MAX_LENGTH = 50;


    /**
     * Constructor.
     *
     * @param inProcessor
     *            the action processor.
     */
    public AddLinkComposite(final ActionProcessor inProcessor)
    {
        /*
         * Add the widgets.
         */
        add(addLink);
        add(displayPanel);
        add(addPanel);

        this.addStyleName("attach-link-container");

        /*
         * Hide the other panels.
         */
        addPanel.setVisible(false);
        displayPanel.setVisible(false);

        addLink.addStyleName("show-attach-link-panel");

        /**
         * Setup the add panel.
         */
        addPanel.addStyleName("attach-link");
        closeAddButton.addStyleName("close");
        addPanel.add(closeAddButton);

        Label addLinkLabel = new Label("Add Link");
        addLinkLabel.addStyleName("title");
        addPanel.add(addLinkLabel);

        addPanel.add(linkUrl);
        addPanel.add(fetchLink);
        fetchLink.addStyleName("add-button-submit");

        /*
         * Setup the display pane.
         */
        closeDisplayButton.addStyleName("close");
        displayPanel.add(closeDisplayButton);
        displayPanel.addStyleName("link-panel");
        linkPanel.addStyleName("message-link");
        linkUrlDisplay.addStyleName("url");
        linkDesc.addStyleName("meta-description");
        linkPanel.add(selector);
        linkPanel.add(titleLink);
        linkPanel.add(linkUrlDisplay);
        linkPanel.add(linkDesc);
        linkPanel.add(selector.getPagingControlls());
        displayPanel.add(linkPanel);
        titleLink.add(title);

        titleLink.addStyleName("attach-link-title-entry");


        EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(ParseLinkEvent.getEvent(), new Observer<ParseLinkEvent>()
        {
            public void update(final ParseLinkEvent event)
            {
                onAddLinkClicked();
                linkUrl.setText(event.getUrl());
                fetchLink.addStyleName("verifying-link");
                fetchLink(event.getUrl());
            }
        });

        eventBus.addObserver(MessageAttachmentChangedEvent.class, new Observer<MessageAttachmentChangedEvent>()
        {
            public void update(final MessageAttachmentChangedEvent evt)
            {
                if (evt.getAttachment() != null && evt.getAttachment() instanceof Bookmark)
                {
                    onLinkAdded(((Bookmark) evt.getAttachment()).getLinkInformation());
                }
                else
                {
                    onLinkAdded(null);
                }
            }
        });

        eventBus.addObserver(new ErrorPostingMessageToNullScopeEvent(),
                new Observer<ErrorPostingMessageToNullScopeEvent>()
                {
                    public void update(final ErrorPostingMessageToNullScopeEvent event)
                    {
                        fetchLink.removeStyleName("verifying-link");
                    }
                });

        fetchLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                fetchLink.addStyleName("verifying-link");
                fetchLink(linkUrl.getText());
            }
        });

        addLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                onAddLinkClicked();
            }
        });

        closeDisplayButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                close();
            }
        });

        closeAddButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                close();
            }
        });
    }

    /**
     * Shows the add link form.
     */
    public void onAddLinkClicked()
    {
        addLink.setVisible(false);
        addPanel.setVisible(true);
        linkUrl.reset();
    }

    /**
     * @return true if a link is attached.
     */
    public boolean hasAttachment()
    {
        return displayPanel.isVisible();
    }

    /**
     * Close the link.
     */
    public void close()
    {
        fetchedLink = "";
        MessageAttachmentChangedEvent event = new MessageAttachmentChangedEvent(null);
        Session.getInstance().getEventBus().notifyObservers(event);
    }

    /**
     * Called when a link is added to the message.
     *
     * @param link
     *            the link that was added.
     */
    public void onLinkAdded(final LinkInformation link)
    {
        linkUrl.setText("");
        linkUrl.checkBox();
        LinkInformation addedLink = link;

        if (null == addedLink)
        {
            addedLink = new LinkInformation();
        }

        addPanel.setVisible(false);
        fetchLink.removeStyleName("verifying-link");
        displayPanel.setVisible(null != link);
        addLink.setVisible(null == link);

        selector.setLink(addedLink);

        if (addedLink.getImageUrls().size() > 0)
        {
            linkPanel.addStyleName("has-thumbnail");
        }

        linkUrlDisplay.setText("source: " + addedLink.getSource());

        title.setVisibleLength(MAX_LENGTH);
        title.setValue(addedLink.getTitle());
        title.addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent event)
            {
                link.setTitle(title.getValue());
            }
        });

        linkDesc.setText(addedLink.getDescription());

    }

    /**
     * Fetch link.
     * @param inLinkUrl link url.
     */
    public void fetchLink(final String inLinkUrl)
    {
        // very basic url validation
        if (inLinkUrl == null || inLinkUrl.isEmpty() || !inLinkUrl.startsWith("http") || !inLinkUrl.contains("."))
        {
            ErrorPostingMessageToNullScopeEvent error = new ErrorPostingMessageToNullScopeEvent();
            error.setErrorMsg("You must supply a valid url (example: http://www.example.com)");
            Session.getInstance().getEventBus().notifyObservers(error);
        }
        else if (inLinkUrl != fetchedLink)
        {
            Session.getInstance().getActionProcessor().makeRequest(
                    new ActionRequestImpl<LinkInformation>("getParsedLinkInformation", inLinkUrl),
                    new AsyncCallback<LinkInformation>()
                    {
                        /* implement the async call back methods */
                        public void onFailure(final Throwable caught)
                        {
                            LinkInformation linkInformation = new LinkInformation();
                            linkInformation.setTitle(inLinkUrl);
                            linkInformation.setUrl(inLinkUrl);

                            MessageAttachmentChangedEvent event = new MessageAttachmentChangedEvent(new Bookmark(
                                    linkInformation));
                            Session.getInstance().getEventBus().notifyObservers(event);
                        }

                        public void onSuccess(final LinkInformation result)
                        {
                            MessageAttachmentChangedEvent event = new MessageAttachmentChangedEvent(
                                    new Bookmark(result));
                            Session.getInstance().getEventBus().notifyObservers(event);
                        }
                    });
        }

        fetchedLink = inLinkUrl;
    }

}
