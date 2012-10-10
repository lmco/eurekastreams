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

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ParseLinkEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Add link panel.
 */
public class AddLinkComposite extends FlowPanel
{
    /** Add link button. */
    private final Label addLink = new Label("attach link");

    /** Add link button. */
    private final Label fetchLink = new Label("");

    /** The add link panel. */
    private final FlowPanel addPanel = new FlowPanel();

    /** The URL to the link. */
    private final LabeledTextBox linkUrl = new LabeledTextBox("http://");

    /** The display link panel. */
    private final FlowPanel displayPanel = new FlowPanel();

    /** Thumbnail selector. */
    private final ThumbnailSelectorComposite selector = new ThumbnailSelectorComposite();

    /** Link URL. */
    private final Label linkUrlDisplay = new Label();

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

    /**
     * Link error panel.
     */
    private Label linkError = new Label("You must supply a valid url (example: http://www.example.com)");

    /**
     * Get the text to be attached,
     * as a link, to the post.
     *
     * @return link text
     */
    public final String getLinkText()
    {
        return linkUrl.getText();
    }
    
    /**
     * Constructor.
     */
    public AddLinkComposite()
    {
        linkError.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formErrorBox());
        linkError.addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLinkErrorBox());
        linkError.setVisible(false);
        // add the three main widgets
        add(linkError);
        add(addLink);
        add(displayPanel);
        add(addPanel);

        addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLinkContainer());

        // only one is visible at a time, so hide the others
        addPanel.setVisible(false);
        displayPanel.setVisible(false);

        addLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().showAttachLinkPanel());

        // -- Setup the add link panel (field to enter URL) --

        addPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLink());
        closeAddButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().close());
        addPanel.add(closeAddButton);

        Label addLinkLabel = new Label("Add Link");
        addLinkLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        addPanel.add(addLinkLabel);

        SimplePanel boxWrapper = new SimplePanel();
        boxWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().boxWrapper());
        boxWrapper.add(linkUrl);

        addPanel.add(fetchLink);
        addPanel.add(boxWrapper);
        fetchLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addButtonSubmit());

        // -- Setup the link display panel (thumbnail selector, field to update title) --

        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLinkTitleEntry());
        linkUrlDisplay.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
        linkDesc.addStyleName(StaticResourceBundle.INSTANCE.coreCss().metaDescription());

        FlowPanel linkInfoPanel = new FlowPanel();
        linkInfoPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageLink());
        linkInfoPanel.add(title);
        linkInfoPanel.add(linkUrlDisplay);
        linkInfoPanel.add(linkDesc);

        closeDisplayButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().close());

        displayPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkPanel());
        displayPanel.add(closeDisplayButton);
        displayPanel.add(selector);
        displayPanel.add(linkInfoPanel);
        displayPanel.add(selector.getPagingControls());

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
     * @return if the widget is in add mode.
     */
    public boolean inAddMode()
    {
        return (addPanel.isVisible());
    }

    /**
     * Close the link.
     */
    public void close()
    {
        fetchedLink = "";
        linkError.setVisible(false);
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
        // make sure the link hasn't changed - this prevents a slow site returning after user changed the link to a
        // faster one
        if (((fetchedLink == null || fetchedLink == "") && link == null) || fetchedLink == link.getUrl())
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

            if (!addedLink.getImageUrls().isEmpty())
            {
                displayPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hasThumbnail());
            }

            linkUrlDisplay.setText("source: " + addedLink.getSource());

            title.setVisibleLength(MAX_LENGTH);
            title.setValue(addedLink.getTitle());
            title.addBlurHandler(new BlurHandler()
            {
                public void onBlur(final BlurEvent event)
                {
                    // This check is a workaround for the real problem, which is that the blur handler is getting wired
                    // up
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
    }

    /**
     * Fetch link.
     * 
     * @param inLinkUrl
     *            link url.
     */
    public void fetchLink(final String inLinkUrl)
    {
    	linkError.setVisible(false);

        // very basic url validation
        final EventBus eventBus = Session.getInstance().getEventBus();
        if (inLinkUrl == null || inLinkUrl.isEmpty() || !inLinkUrl.contains("://"))
        {
            linkError.setVisible(true);
            fetchLink.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().verifyingLink());
        }
        else if (inLinkUrl != fetchedLink)
        {
            Session.getInstance().getActionProcessor()
                    .makeRequest("getParsedLinkInformation", inLinkUrl, new AsyncCallback<LinkInformation>()
                    {
                        /* implement the async call back methods */
                        public void onFailure(final Throwable caught)
                        {
                            LinkInformation linkInformation = new LinkInformation();
                            linkInformation.setTitle(inLinkUrl);
                            linkInformation.setUrl(inLinkUrl);

                            MessageAttachmentChangedEvent event = new MessageAttachmentChangedEvent(new Bookmark(
                                    linkInformation));
                            eventBus.notifyObservers(event);

                            eventBus.notifyObservers(new
                            // line break.
                            ShowNotificationEvent(new Notification(UNVERIFIED_URL_MESSAGE)));
                        }

                        public void onSuccess(final LinkInformation result)
                        {
                            MessageAttachmentChangedEvent event = new MessageAttachmentChangedEvent(new Bookmark(
                                    result));

                            boolean titleBlank = result.getTitle() == null || result.getTitle().isEmpty();
                            if (titleBlank)
                            {
                                result.setTitle(result.getUrl());
                            }

                            eventBus.notifyObservers(event);
                        }
                    });
        }

        fetchedLink = inLinkUrl;
    }
}
