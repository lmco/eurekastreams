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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.web.client.model.GroupStickyActivityModel;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.pagedlist.ItemRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer.State;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.BookmarkRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.FileRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.NoteRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.ObjectRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.VideoRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.verb.PostRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.verb.ShareRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.verb.VerbRenderer;
import org.eurekastreams.web.client.ui.common.stream.share.ShareMessageDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.utility.BaseActivityLinkBuilder;
import org.eurekastreams.web.client.utility.InContextActivityLinkBuilder;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders an activity for the sticky display at the top of the list.
 */
public class StickyActivityRenderer implements ItemRenderer<ActivityDTO>
{
    /** Verb dictionary. */
    private final Map<ActivityVerb, VerbRenderer> verbDictionary = new HashMap<ActivityVerb, VerbRenderer>();

    /** Object dictionary. */
    private final Map<BaseObjectType, ObjectRenderer> objectDictionary = new HashMap<BaseObjectType, ObjectRenderer>();

    /** For building links to activities. */
    private final BaseActivityLinkBuilder activityLinkBuilder = new InContextActivityLinkBuilder();

    /**
     * Constructor.
     */
    public StickyActivityRenderer()
    {
        verbDictionary.put(ActivityVerb.POST, new PostRenderer());
        verbDictionary.put(ActivityVerb.SHARE, new ShareRenderer());

        objectDictionary.put(BaseObjectType.BOOKMARK, new BookmarkRenderer());
        objectDictionary.put(BaseObjectType.NOTE, new NoteRenderer());
        objectDictionary.put(BaseObjectType.VIDEO, new VideoRenderer());
        objectDictionary.put(BaseObjectType.FILE, new FileRenderer());
    }

    /**
     * Render a message item.
     *
     * @param msg
     *            the message item.
     *
     * @return the rendered item as a FlowPanel.
     */
    public Panel render(final ActivityDTO msg)
    {
        Panel mainPanel = new FlowPanel();
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamMessageItem());
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        mainPanel.addStyleName(State.READONLY.toString());

        VerbRenderer verbRenderer = verbDictionary.get(msg.getVerb());
        verbRenderer.setup(objectDictionary, msg, State.DEFAULT, false);

        // left column items
        Panel leftColumn = null;

        // avatar
        Widget avatar = verbRenderer.getAvatar();
        if (avatar != null)
        {
            Panel parent = leftColumn == null ? mainPanel : leftColumn;
            parent.add(avatar);
        }

        FlowPanel msgContent = new FlowPanel();
        msgContent.addStyleName(StaticResourceBundle.INSTANCE.coreCss().description());
        mainPanel.add(msgContent);

        // row for who posted
        Panel sourceMetaData = new FlowPanel();
        sourceMetaData.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageMetadataSource());
        for (StatefulRenderer itemRenderer : verbRenderer.getSourceMetaDataItemRenderers())
        {
            Widget metaDataItem = itemRenderer.render();
            if (metaDataItem != null)
            {
                sourceMetaData.add(metaDataItem);
            }
        }
        msgContent.add(sourceMetaData);

        // content
        FlowPanel nonMetaData = new FlowPanel();
        nonMetaData.addStyleName(State.READONLY.toString());

        Widget content = verbRenderer.getContent();
        if (content != null)
        {
            nonMetaData.add(content);
            msgContent.add(nonMetaData);
        }

        // additional metadata
        FlowPanel metaData = new FlowPanel();
        metaData.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageMetadataAdditional());
        for (StatefulRenderer itemRenderer : verbRenderer.getMetaDataItemRenderers())
        {
            Widget metaDataItem = itemRenderer.render();
            if (metaDataItem != null)
            {
                metaData.add(metaDataItem);
            }
        }
        if (metaData.getWidgetCount() > 0)
        {
            msgContent.add(metaData);
        }

        msgContent.add(buildActionsLine(msg, verbRenderer));

        return mainPanel;
    }

    /**
     * Builds the action links line.
     *
     * @param msg
     *            The message.
     * @param verbRenderer
     *            Renderer for the message's verb.
     * @return The actions panel.
     */
    private Widget buildActionsLine(final ActivityDTO msg, final VerbRenderer verbRenderer)
    {
        StreamEntityDTO destinationStream = msg.getDestinationStream();

        // timestamp and actions
        Panel timestampActions = new FlowPanel();
        timestampActions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageTimestampActionsArea());

        // Hijack this property and use to show lock icon for private activity.
        if (!msg.isShareable())
        {
            Label lockIcon = new Label("");
            lockIcon.addStyleName(StaticResourceBundle.INSTANCE.coreCss().privateIcon());
            timestampActions.add(lockIcon);
        }

        // create timestamp as permalink
        String date = new DateFormatter(new Date()).timeAgo(msg.getPostedTime());
        Widget dateLink;
        String permalinkUrl = activityLinkBuilder.buildActivityPermalink(msg.getId(), destinationStream.getType(),
                destinationStream.getUniqueIdentifier());
        dateLink = new InlineHyperlink(date, permalinkUrl);
        dateLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageTimestampLink());
        timestampActions.add(dateLink);

        if (msg.getAppName() != null)
        {
            String appSource = msg.getAppSource();
            if (appSource != null)
            {
                FlowPanel viaPanel = new FlowPanel();
                viaPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().viaMetadata());
                viaPanel.add(new InlineLabel("via "));
                viaPanel.add(new Anchor(msg.getAppName(), appSource));
                timestampActions.add(viaPanel);
            }
            else
            {
                InlineLabel viaLine = new InlineLabel("via " + msg.getAppName());
                viaLine.addStyleName(StaticResourceBundle.INSTANCE.coreCss().viaMetadata());
                timestampActions.add(viaLine);
            }
            // TODO: If appSource is not supplied, the link should go to the respective galleries for apps and plugins.
            // However, the app galery requires knowing the start page tab id, and the worthwhile plugin gallery is only
            // available to coordinators.
        }

        ComplexPanel actionsPanel = new FlowPanel();
        actionsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageActionsArea());

        // Show comments
        InlineHyperlink showCommentsLink = new InlineHyperlink("Show Comments", permalinkUrl);
        actionsPanel.add(showCommentsLink);

        // Share
        if (verbRenderer.getAllowShare() && msg.isShareable())
        {
            insertActionSeparator(actionsPanel, null);
            Label shareLink = new InlineLabel("Share");
            shareLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
            actionsPanel.add(shareLink);

            shareLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    Dialog.showCentered(new ShareMessageDialogContent(msg));
                }
            });
        }

        // Unstick
        // Note: using the cheating way: always create the link, let CSS hide it unless the user is actually a
        // coordinator
        insertActionSeparator(actionsPanel, StaticResourceBundle.INSTANCE.coreCss().ownerOnlyInline());
        Label link = new InlineLabel("Unstick");
        link.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
        link.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ownerOnlyInline());
        actionsPanel.add(link);

        link.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                GroupStickyActivityModel.getInstance().delete(msg.getDestinationStream().getDestinationEntityId());
            }
        });

        timestampActions.add(actionsPanel);

        return timestampActions;
    }

    /**
     * Adds a separator (dot).
     *
     * @param panel
     *            Panel to put the separator in.
     * @param extraStyle
     *            Extra style to add.
     */
    private void insertActionSeparator(final ComplexPanel panel, final String extraStyle)
    {
        Label sep = new InlineLabel("\u2219");
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        if (extraStyle != null)
        {
            sep.addStyleName(extraStyle);
        }
        panel.add(sep);
    }
}
