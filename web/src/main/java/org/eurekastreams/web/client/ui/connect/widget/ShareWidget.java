/*
 * Copyright (c) 2011-2013 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.widget;

import java.util.HashSet;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.DomainConversionUtility;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PostReadyEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.stream.PostToPanel;
import org.eurekastreams.web.client.ui.common.stream.PostToStreamTextboxPanel;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.BookmarkPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.common.stream.thumbnail.ThumbnailSelectorComposite;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The Eureka Connect share dialog widget.
 */
public class ShareWidget extends Composite
{
    /** Max length. */
    private static final int MAXLENGTH = 250;

    /** The display link panel. */
    private final FlowPanel displayPanel = new FlowPanel();

    /** Thumbnail selector. */
    private final ThumbnailSelectorComposite selector = new ThumbnailSelectorComposite();

    /** Link URL. */
    private final Label linkUrlDisplay = new Label();

    /** The title link. */
    private final FlowPanel titleLink = new FlowPanel();

    /** Link Description. */
    private final Label linkDesc = new Label();

    /** Text box. */
    private final TextBox title = new TextBox();

    /** Max length of title. */
    private static final int MAX_LENGTH = 50;

    /** Activity Populator. */
    private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

    /**
     * Default text for postbox.
     */
    private final String defaultText = "Add comment to Post";

    /** The count down label. */
    private final Label countDown = new Label();

    /** If share button is inactive. */
    private boolean inactive = false;

    /** The post button. */
    private final Label postButton = new Label("Post");

    /** The message entry box. */
    private final PostToStreamTextboxPanel message = new PostToStreamTextboxPanel();

    /**
     * Constructor.
     * 
     * @param resourceUrl
     *            resource url.
     * @param inTitle
     *            the title.
     * @param desc
     *            the description.
     * @param thumbs
     *            the thumbnails.
     */
    public ShareWidget(final String resourceUrl, final String inTitle, final String desc, final String[] thumbs)
    {
        final FlowPanel widget = new FlowPanel();
        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectShareWidgetContainer());
        initWidget(widget);

        PersonModelView person = Session.getInstance().getCurrentPerson();

        StreamScope defaultStreamScope = new StreamScope(person.getDisplayName(), ScopeType.PERSON,
                person.getAccountId(), person.getStreamId());

        final PostToPanel postToPanel = new PostToPanel(defaultStreamScope);
        widget.add(postToPanel);

        // -- Setup the link display panel (thumbnail selector, field to update title) --

        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().attachLinkTitleEntry());
        linkUrlDisplay.addStyleName(StaticResourceBundle.INSTANCE.coreCss().url());
        linkDesc.addStyleName(StaticResourceBundle.INSTANCE.coreCss().metaDescription());

        FlowPanel linkInfoPanel = new FlowPanel();
        linkInfoPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageLink());
        linkInfoPanel.add(title);
        linkInfoPanel.add(linkUrlDisplay);
        linkInfoPanel.add(linkDesc);

        displayPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkPanel());
        displayPanel.add(selector);
        displayPanel.add(linkInfoPanel);
        displayPanel.add(selector.getPagingControls());

        widget.add(displayPanel);

        final LinkInformation link = new LinkInformation();
        link.setDescription(desc);
        link.setTitle(inTitle);
        link.setUrl(resourceUrl);
        try
        {
            link.setSource(resourceUrl.substring(0, resourceUrl.indexOf('/', 7)));
        }
        catch (Exception e)
        {
            link.setSource(resourceUrl);
        }

        if (thumbs != null)
        {
            HashSet<String> thumbsSet = new HashSet<String>();

            for (String thumb : thumbs)
            {
                thumbsSet.add(thumb);
            }

            link.setImageUrls(thumbsSet);

            link.setLargestImageUrl(thumbs[0]);
        }

        onLinkAdded(link);

        final FlowPanel postContainer = new FlowPanel();
        postContainer.getElement().setAttribute("id", "post-to-stream");
        postContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postToStream());
        postContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().small());
        postContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postToStreamContainer());

        FlowPanel postInfoContainer = new FlowPanel();
        postInfoContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postInfoContainer());

        postButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postButton());
        postInfoContainer.add(postButton);

        countDown.setText(Integer.toString(MAXLENGTH));
        countDown.addStyleName(StaticResourceBundle.INSTANCE.coreCss().charactersRemaining());
        postInfoContainer.add(countDown);

        message.setText(defaultText);

        // user clicked in message text box
        message.addFocusHandler(new FocusHandler()
        {
            public void onFocus(final FocusEvent inEvent)
            {
                if ((" " + postContainer.getStyleName() + " ")
                        .contains(StaticResourceBundle.INSTANCE.coreCss().small()))
                {
                    postContainer.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().small());
                    message.setText("");
                }
            }
        });

        // changes to the message for character countdown
        message.addKeystrokeHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent inEvent)
            {
                onCommentChanges();

            }
        });
        message.addValueChangedHandler(new ValueChangeHandler<String>()
        {
            public void onValueChange(final ValueChangeEvent<String> inEvent)
            {
                onCommentChanges();
            }
        });

        AvatarWidget avatar = new AvatarWidget(person, EntityType.PERSON, Size.VerySmall);
        avatar.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postEntryAvatar());

        Panel entryPanel = new FlowPanel();
        entryPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postEntryPanel());
        entryPanel.add(avatar);
        entryPanel.add(postInfoContainer);
        entryPanel.add(message);

        postContainer.add(entryPanel);

        FlowPanel contentWarningContainer = new FlowPanel();
        contentWarningContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().contentWarning());
        contentWarningContainer.add(new SimplePanel());
        final FlowPanel contentWarning = new FlowPanel();
        contentWarningContainer.add(contentWarning);
        postContainer.add(contentWarningContainer);
        Session.getInstance().getEventBus()
                .addObserver(GotSystemSettingsResponseEvent.class, new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        contentWarning.getElement().setInnerHTML(event.getResponse().getContentWarningText());
                    }
                });
        SystemSettingsModel.getInstance().fetch(null, true);

        widget.add(postContainer);

        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent arg1)
            {
                closeWindow();
            }
        });

        postButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
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

                String messageText = message.getText();

                if (messageText.equals(defaultText))
                {
                    messageText = "";
                }

                BookmarkPopulator objectStrat = new BookmarkPopulator();
                objectStrat.setLinkInformation(link);
                ActivityDTO activity = activityPopulator.getActivityDTO(messageText, recipientType,
                        scope.getUniqueKey(), new PostPopulator(), objectStrat);
                PostActivityRequest postRequest = new PostActivityRequest(activity);

                ActivityModel.getInstance().insert(postRequest);
            }
        });

        EventBus.getInstance().notifyObservers(new PostReadyEvent("Something"));
    }

    /**
     * Called when a link is added to the message.
     * 
     * @param link
     *            the link that was added.
     */
    public void onLinkAdded(final LinkInformation link)
    {
        LinkInformation addedLink = link;

        if (null == addedLink)
        {
            addedLink = new LinkInformation();
        }

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
     * Gets triggered whenever the comment changes.
     */
    private void onCommentChanges()
    {
        Integer charsRemaining = MAXLENGTH - message.getText().length();
        countDown.setText(charsRemaining.toString());
        if (charsRemaining >= 0)
        {
            countDown.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().overCharacterLimit());
            postButton.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().inactive());
            inactive = false;
        }
        else
        {
            countDown.addStyleName(StaticResourceBundle.INSTANCE.coreCss().overCharacterLimit());
            postButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().inactive());
            inactive = true;
        }
    }

    /**
     * Close the window.
     */
    public static native void closeWindow()
    /*-{
        try {
            $wnd.opener.location.href = $wnd.opener.location.href;
        }
        catch(e) {}
        $wnd.close();
    }-*/;
}
