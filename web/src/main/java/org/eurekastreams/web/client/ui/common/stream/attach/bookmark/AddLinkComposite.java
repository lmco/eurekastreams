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
package org.eurekastreams.web.client.ui.common.stream.attach.bookmark;

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ParseLinkEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.thumbnail.ThumbnailSelectorComposite;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

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
public class AddLinkComposite extends FlowPanel
{
    /** Add link button. */
    private final Label addLink = new Label("attach link");

    /** Add link button. */
    private final Label fetchLink = new Label("Go");

    /** The add link panel. */
    private final FlowPanel addPanel = new FlowPanel();

    /** The URL to the link. */
    private final LabeledTextBox linkUrl = new LabeledTextBox("http://");

    /** The display link panel. */
    private final FlowPanel displayPanel = new FlowPanel();

    /** The link panel. */
    private final FlowPanel linkPanel = new FlowPanel();

    /** Thumbnail selector. */
    private final ThumbnailSelectorComposite selector = new ThumbnailSelectorComposite();

    /** Link URL. */
    private final Label linkUrlDisplay = new Label();

    /** The title link. */
    private final FlowPanel titleLink = new FlowPanel();

    /** Link Description. */
    private final Label linkDesc = new Label();

    /** The close button. */
    private final Label closeDisplayButton = new Label(StaticResourceBundle.INSTANCE.coreCss().close());

    /** The close button. */
    private final Label closeAddButton = new Label(StaticResourceBundle.INSTANCE.coreCss().close());

    /** Text box. */
    private final TextBox title = new TextBox();

    /** The last fetched links url. */
    private String fetchedLink = "";

    /** Max length of title. */
    private static final int MAX_LENGTH = 50;

    /** Message for URLs which fail on getting parsed link information. */
    private static final String UNVERIFIED_URL_MESSAGE = "URL may be invalid.  "
            + "Please confirm it was entered correctly.";

    /** Message for URLs which returned missing link information. */
    private static final String INCOMPLETE_INFO_URL_MESSAGE = "Details about URL could not be retrieved.  "
            + "Please confirm it was entered correctly.";

    /**
     * Constructor.
     */
    public AddLinkComposite()
    {
        /*
         * Add the widgets.
         */
        add(addLink);
        add(displayPanel);
        add(addPanel);

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLinkContainer());

        /*
         * Hide the other panels.
         */
        addPanel.setVisible(false);
        displayPanel.setVisible(false);

        addLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().showAttachLinkPanel());

        /**
         * Setup the add panel.
         */
        addPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLink());
        closeAddButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().close());
        addPanel.add(closeAddButton);

        Label addLinkLabel = new Label("Add Link");
        addLinkLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        addPanel.add(addLinkLabel);

        addPanel.add(linkUrl);
        addPanel.add(fetchLink);
        fetchLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addButtonSubmit());

        /*
         * Setup the display pane.
         */
        closeDisplayButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().close());
        displayPanel.add(closeDisplayButton);
        displayPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkPanel());
        linkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageLink());
        linkUrlDisplay.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
        linkDesc.addStyleName(StaticResourceBundle.INSTANCE.coreCss().metaDescription());
        linkPanel.add(selector);
        linkPanel.add(titleLink);
        linkPanel.add(linkUrlDisplay);
        linkPanel.add(linkDesc);
        linkPanel.add(selector.getPagingControlls());
        displayPanel.add(linkPanel);
        titleLink.add(title);

        titleLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLinkTitleEntry());

        EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(ParseLinkEvent.getEvent(), new Observer<ParseLinkEvent>()
        {
            public void update(final ParseLinkEvent event)
            {
                onAddLinkClicked();
                linkUrl.setText(event.getUrl());
                fetchLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().verifyingLink());
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
                        fetchLink.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().verifyingLink());
                    }
                });

        fetchLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                fetchLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().verifyingLink());
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
        Session.getInstance().getEventBus().notifyObservers(new MessageAttachmentChangedEvent(null));
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
        fetchLink.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().verifyingLink());
        displayPanel.setVisible(null != link);
        addLink.setVisible(null == link);

        selector.setLink(addedLink);

        if (addedLink.getImageUrls().size() > 0)
        {
            linkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hasThumbnail());
        }

        linkUrlDisplay.setText("source: " + addedLink.getSource());

        title.setVisibleLength(MAX_LENGTH);
        title.setValue(addedLink.getTitle());
        title.addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent event)
            {
                // This check is a workaround for the real problem, which is that the blur handler is getting wired up
                // multiple times (once on the first time the user clicks 'add link' and once when the activity is
                // posted and everything is being cleared out). Maybe this control will get redesigned when
                // PostToStreamComposite gets refactored from MVC to the current design.
                if (link != null)
                {
                    link.setTitle(title.getValue());
                }
            }
        });

        linkDesc.setText(addedLink.getDescription());
    }

    /**
     * Fetch link.
     * 
     * @param inLinkUrl
     *            link url.
     */
    public void fetchLink(final String inLinkUrl)
    {
        // very basic url validation
        final EventBus eventBus = Session.getInstance().getEventBus();
        if (inLinkUrl == null || inLinkUrl.isEmpty() || !inLinkUrl.contains("."))
        {
            ErrorPostingMessageToNullScopeEvent error = new ErrorPostingMessageToNullScopeEvent();
            error.setErrorMsg("You must supply a valid url (example: http://www.example.com)");
            eventBus.notifyObservers(error);
        }
        else if (inLinkUrl != fetchedLink)
        {
            Session.getInstance()
                    .getActionProcessor()
                    .makeRequest(new ActionRequestImpl<LinkInformation>("getParsedLinkInformation", inLinkUrl),
                            new AsyncCallback<LinkInformation>()
                            {
                                /* implement the async call back methods */
                                public void onFailure(final Throwable caught)
                                {
                                    LinkInformation linkInformation = new LinkInformation();
                                    linkInformation.setTitle(inLinkUrl);
                                    linkInformation.setUrl(inLinkUrl);

                                    MessageAttachmentChangedEvent event = new MessageAttachmentChangedEvent(
                                            new Bookmark(linkInformation));
                                    eventBus.notifyObservers(event);

                                    eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                                            UNVERIFIED_URL_MESSAGE)));
                                }

                                public void onSuccess(final LinkInformation result)
                                {
                                    MessageAttachmentChangedEvent event = new MessageAttachmentChangedEvent(
                                            new Bookmark(result));

                                    boolean titleBlank = result.getTitle() == null || result.getTitle().isEmpty();
                                    if (titleBlank)
                                    {
                                        result.setTitle(result.getUrl());
                                    }
                                    eventBus.notifyObservers(event);

                                    // no reason to show any errors
                                    // if (titleBlank
                                    // && (result.getDescription() == null || result.getDescription().isEmpty())
                                    // && result.getImageUrls().isEmpty())
                                    // {
                                    // eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                                    // INCOMPLETE_INFO_URL_MESSAGE)));
                                    // }
                                }
                            });
        }

        fetchedLink = inLinkUrl;
    }
}
