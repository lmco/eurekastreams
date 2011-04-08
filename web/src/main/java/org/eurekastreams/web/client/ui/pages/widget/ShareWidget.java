/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.widget;

import java.util.HashSet;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.server.domain.DomainConversionUtility;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PostReadyEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.model.ActivityModel;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The Eureka Connect share dialog widget.
 */
public class ShareWidget extends Composite
{

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

    /** Text box. */
    private final TextBox title = new TextBox();

    /** Max length of title. */
    private static final int MAX_LENGTH = 50;

    /** Activity Populator. */
    private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

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

        StreamScope defaultStreamScope = new StreamScope(person.getDisplayName(), ScopeType.PERSON, person
                .getAccountId(), person.getStreamId());

        final PostToPanel postToPanel = new PostToPanel(defaultStreamScope);
        widget.add(postToPanel);

        displayPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkPanel());

        linkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageLink());

        linkPanel.add(selector);
        linkPanel.add(titleLink);
        linkPanel.add(linkUrlDisplay);
        linkPanel.add(linkDesc);
        linkPanel.add(selector.getPagingControlls());
        displayPanel.add(linkPanel);
        titleLink.add(title);

        displayPanel.add(linkPanel);
        widget.add(displayPanel);

        final LinkInformation link = new LinkInformation();
        link.setDescription(desc);
        link.setTitle(inTitle);
        link.setUrl(resourceUrl);
        link.setSource("http://www.eurekastreams.org");

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

        Label postButton = new Label("Post");

        FlowPanel postInfoContainer = new FlowPanel();
        postInfoContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postInfoContainer());

        postButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postButton());
        postInfoContainer.add(postButton);

        final PostToStreamTextboxPanel message = new PostToStreamTextboxPanel();
        message.setText("Add comment to Post");

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

        AvatarWidget avatar = new AvatarWidget(person, EntityType.PERSON, Size.VerySmall);
        avatar.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postEntryAvatar());

        Panel entryPanel = new FlowPanel();
        entryPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postEntryPanel());
        entryPanel.add(avatar);
        entryPanel.add(message);
        entryPanel.add(postInfoContainer);

        postContainer.add(entryPanel);

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

                BookmarkPopulator objectStrat = new BookmarkPopulator();
                objectStrat.setLinkInformation(link);
                ActivityDTO activity = activityPopulator.getActivityDTO(message.getText(), recipientType, scope
                        .getUniqueKey(), new PostPopulator(), objectStrat);
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
     * Close the window.
     */
    public static native void closeWindow()
    /*-{
        $wnd.opener.location.href = $wnd.opener.location.href;
        $wnd.close();
    }-*/;
}
