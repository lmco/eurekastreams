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
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.web.client.events.ChangeShowStreamRecipientEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedActivityFlagResponseEvent;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.FlaggedActivityModel;
import org.eurekastreams.web.client.model.requests.UpdateActivityFlagRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.pagedlist.ItemRenderer;
import org.eurekastreams.web.client.ui.common.stream.comments.CommentsListPanel;
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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a message in the stream.
 */
public class StreamMessageItemRenderer implements ItemRenderer<ActivityDTO>
{
    /**
     * State.
     *
     */
    public enum State
    {
        /**
         * Default.
         */
        DEFAULT,
        /**
         * Read only.
         */
        READONLY
    }

    /**
     * Effects facade.
     */
    private final EffectsFacade effects = new EffectsFacade();

    /**
     * Show the recipient.
     */
    private ShowRecipient showRecipient;
    /**
     * Show the recipient.
     */
    private ShowRecipient showRecipientInStream;

    /** Show controls for managing flagged content. */
    private boolean showManageFlagged;

    /** Render specifically for a single-activity view. */
    private boolean singleView;

    /** If date should be a permalink. */
    private boolean createPermalink = true;

    /**
     * State.
     */
    private final State state;

    /**
     * Verb dictionary.
     */
    private final Map<ActivityVerb, VerbRenderer> verbDictionary = new HashMap<ActivityVerb, VerbRenderer>();

    /**
     * Object dictionary.
     */
    private final Map<BaseObjectType, ObjectRenderer> objectDictionary = new HashMap<BaseObjectType, ObjectRenderer>();

    /**
     * Flag to show new comment box in initial view.
     */
    private boolean showComment = false;

    /** For building links to activities. */
    private BaseActivityLinkBuilder activityLinkBuilder = new InContextActivityLinkBuilder();

    /**
     * Constructor.
     *
     * @param inShowRecipient
     *            show the recipient.
     */
    public StreamMessageItemRenderer(final ShowRecipient inShowRecipient)
    {
        this(inShowRecipient, State.DEFAULT);
    }

    /**
     * Constructor.
     *
     * @param inShowRecipient
     *            show the recipient.
     * @param inState
     *            state.
     */
    public StreamMessageItemRenderer(final ShowRecipient inShowRecipient, final State inState)
    {
        showRecipientInStream = inShowRecipient;
        state = inState;

        verbDictionary.put(ActivityVerb.POST, new PostRenderer());
        verbDictionary.put(ActivityVerb.SHARE, new ShareRenderer());

        objectDictionary.put(BaseObjectType.BOOKMARK, new BookmarkRenderer());
        objectDictionary.put(BaseObjectType.NOTE, new NoteRenderer());
        objectDictionary.put(BaseObjectType.VIDEO, new VideoRenderer());
        objectDictionary.put(BaseObjectType.FILE, new FileRenderer());

        Session.getInstance().getEventBus().addObserver(ChangeShowStreamRecipientEvent.class,
                new Observer<ChangeShowStreamRecipientEvent>()
                {
                    public void update(final ChangeShowStreamRecipientEvent event)
                    {
                        showRecipientInStream = event.getValue();
                    }
                });
    }

    /**
     * Sets showComment.
     *
     * @param inShowComment
     *            value to set.
     */
    public void setShowComment(final boolean inShowComment)
    {
        showComment = inShowComment;
    }

    /**
     * @param inShowManageFlagged
     *            If the controls for managing flagged content should be shown.
     */
    public void setShowManageFlagged(final boolean inShowManageFlagged)
    {
        showManageFlagged = inShowManageFlagged;
    }

    /**
     * @param inSingleView
     *            Render specifically for a single-activity view.
     */
    public void setSingleView(final boolean inSingleView)
    {
        singleView = inSingleView;
    }

    /**
     * @param inActivityLinkBuilder
     *            Builder to use for activity links.
     */
    public void setActivityLinkBuilder(final BaseActivityLinkBuilder inActivityLinkBuilder)
    {
        activityLinkBuilder = inActivityLinkBuilder;
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
        if (msg.getDestinationStream().getUniqueIdentifier().equals(msg.getActor().getUniqueIdentifier()))
        {
            showRecipient = ShowRecipient.FOREIGN_ONLY;
        }
        else
        {
            showRecipient = showRecipientInStream;
        }

        Panel mainPanel = new FlowPanel();
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamMessageItem());
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        mainPanel.addStyleName(state.toString());

        VerbRenderer verbRenderer = verbDictionary.get(msg.getVerb());
        verbRenderer.setup(objectDictionary, msg, state, showRecipient);

        boolean doManageFlagged = showManageFlagged && !state.equals(State.READONLY) && msg.isDeletable();

        // left column items
        Panel leftColumn = null;
        if (doManageFlagged)
        {
            leftColumn = new FlowPanel();
            leftColumn.addStyleName(StaticResourceBundle.INSTANCE.coreCss().leftColumn());
            mainPanel.add(leftColumn);
        }

        // avatar
        Widget avatar = verbRenderer.getAvatar();
        if (avatar != null)
        {
            Panel parent = leftColumn == null ? mainPanel : leftColumn;
            parent.add(avatar);
        }

        if (doManageFlagged)
        {
            leftColumn.add(buildManageFlaggedControls(msg, mainPanel));
        }

        FlowPanel msgContent = new FlowPanel();
        msgContent.addStyleName(StaticResourceBundle.INSTANCE.coreCss().description());
        mainPanel.add(msgContent);

        CommentsListPanel commentsPanel = null;
        if (!state.equals(State.READONLY))
        {
            commentsPanel = new CommentsListPanel(msg.getFirstComment(), msg.getLastComment(), msg.getCommentCount(),
                    msg.getEntityId(), msg.isCommentable(), msg.getDestinationStream().getType(), msg
                            .getDestinationStream().getUniqueIdentifier(), activityLinkBuilder);
        }

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
        nonMetaData.addStyleName(state.toString());

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

        // timestamp and actions
        Panel timestampActions = new FlowPanel();
        timestampActions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageTimestampActionsArea());


        String date = new DateFormatter(new Date()).timeAgo(msg.getPostedTime());
        Widget dateLink;
        if (createPermalink)
        {
            String permalinkUrl = activityLinkBuilder.buildActivityPermalink(msg.getId(), msg.getDestinationStream()
                    .getType(), msg.getDestinationStream().getUniqueIdentifier());
            dateLink = new InlineHyperlink(date, permalinkUrl);
        }
        else
        {
            dateLink = new InlineLabel(date);
        }
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

        if (verbRenderer.getAllowLike())
        {
            LikeCountWidget likeCount = new LikeCountWidget(msg.getEntityId(), msg.getLikeCount(), msg.getLikers(), msg
                    .isLiked());
            timestampActions.add(likeCount);
        }
        timestampActions.add(buildActions(msg, mainPanel, commentsPanel, verbRenderer));

        msgContent.add(timestampActions);

        // comments
        if (commentsPanel != null)
        {
            mainPanel.add(commentsPanel);
            if (msg.getComments() != null && !msg.getComments().isEmpty())
            {
                commentsPanel.renderAllComments(msg.getComments());
            }
            if (showComment)
            {
                commentsPanel.activatePostComment();
            }
        }

        return mainPanel;
    }

    /**
     * Builds the action links panel.
     *
     * @param msg
     *            The message.
     * @param mainPanel
     *            The overall panel for the message.
     * @param commentsPanel
     *            The comments panel.
     * @param verbRenderer
     *            Renderer for the message's verb.
     * @return The actions panel.
     */
    private Widget buildActions(final ActivityDTO msg, final Panel mainPanel, final CommentsListPanel commentsPanel,
            final VerbRenderer verbRenderer)
    {
        final EventBus eventBus = Session.getInstance().getEventBus();

        Panel actionsPanel = new FlowPanel();
        actionsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageActionsArea());

        // Comment
        // The verb is used for activities that are always now commentable. The msg.isCOmmentable is used for activities
        // that that normally are commentable but the user has turned off.
        if (commentsPanel != null && verbRenderer.getAllowComment() && msg.isCommentable())
        {
            Label commentLink = new InlineLabel("Comment");
            commentLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
            actionsPanel.add(commentLink);

            commentLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    commentsPanel.activatePostComment();
                }
            });
        }

        // Like
        if (verbRenderer.getAllowLike())
        {
            insertActionSeparator(actionsPanel);
            Widget like = new LikeWidget(msg.isLiked(), msg.getEntityId());
            actionsPanel.add(like);
        }

        // Share
        if (verbRenderer.getAllowShare() && msg.isShareable())
        {
            insertActionSeparator(actionsPanel);
            Label shareLink = new InlineLabel("Share");
            shareLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
            actionsPanel.add(shareLink);

            shareLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    onShare(msg);
                }
            });
        }

        // Flag (as inappropriate)
        if (!state.equals(State.READONLY) && !showManageFlagged)
        {
            insertActionSeparator(actionsPanel);
            Label link = new InlineLabel("Flag");
            link.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
            actionsPanel.add(link);

            link.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (new WidgetJSNIFacadeImpl()
                            .confirm("Flagged activities will be sent to the organization coordinator for review. "
                                    + "Are you sure you want to flag this activity as inappropriate?"))
                    {
                        eventBus.addObserver(UpdatedActivityFlagResponseEvent.class,
                                new Observer<UpdatedActivityFlagResponseEvent>()
                                {
                                    public void update(final UpdatedActivityFlagResponseEvent ev)
                                    {
                                        if (ev.getResponse() == msg.getId())
                                        {
                                            eventBus.removeObserver(ev, this);
                                            eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                                                    "Activity has been flagged")));
                                        }
                                    }
                                });
                        FlaggedActivityModel.getInstance().update(new UpdateActivityFlagRequest(msg.getId(), true));
                    }
                }
            });
        }

        // Delete
        if (!state.equals(State.READONLY) && msg.isDeletable())
        {
            insertActionSeparator(actionsPanel);
            Label deleteLink = new InlineLabel("Delete");
            deleteLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
            actionsPanel.add(deleteLink);

            setupDeleteClickHandler(deleteLink, msg, mainPanel);
        }

        // Save/Unsave
        if (verbRenderer.getAllowStar() && msg.isStarred() != null)
        {
            insertActionSeparator(actionsPanel);
            Widget star = new StarLinkWidget(msg.isStarred(), msg.getEntityId());
            actionsPanel.add(star);
        }

        return actionsPanel;
    }

    /**
     * Called when user requests to share the activity.
     *
     * @param msg
     *            Activity to share.
     */
    protected void onShare(final ActivityDTO msg)
    {
        DialogContent dialogContent = new ShareMessageDialogContent(msg);
        Dialog dialog = new Dialog(dialogContent);
        dialog.setBgVisible(true);
        dialog.center();
    }

    /**
     * Adds a separator (dot).
     *
     * @param panel
     *            Panel to put the separator in.
     */
    private void insertActionSeparator(final Panel panel)
    {
        Label sep = new InlineLabel("\u2219");
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }

    /**
     * Sets up the buttons to manage flagged content.
     *
     * @param msg
     *            The activity.
     * @param mainPanel
     *            The main activity panel.
     * @return Panel with the controls.
     */
    private Widget buildManageFlaggedControls(final ActivityDTO msg, final Panel mainPanel)
    {
        final Panel buttonsPanel = new FlowPanel();
        buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().flagControls());

        Label ignoreButton = new Label("Ignore");
        ignoreButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().flagIgnoreButton());
        buttonsPanel.add(ignoreButton);
        ignoreButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent ev)
            {
                buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitActive());
                if (singleView)
                {
                    Session.getInstance().getEventBus().addObserver(UpdatedActivityFlagResponseEvent.class,
                            new Observer<UpdatedActivityFlagResponseEvent>()
                            {
                                public void update(final UpdatedActivityFlagResponseEvent ev)
                                {
                                    if (ev.getResponse().equals(msg.getId()))
                                    {
                                        Session.getInstance().getEventBus().removeObserver(ev, this);
                                        buttonsPanel.removeFromParent();
                                    }
                                }
                            });
                }
                FlaggedActivityModel.getInstance().update(new UpdateActivityFlagRequest(msg.getId(), false));
            }
        });

        Label deleteButton = new Label("Delete");
        deleteButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().flagDeleteButton());
        buttonsPanel.add(deleteButton);
        deleteButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete this activity?"))
                {
                    buttonsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().waitActive());
                    setupDeleteFadeout(msg, mainPanel);
                    ActivityModel.getInstance().delete(msg.getId());
                }
            }
        });

        return buttonsPanel;
    }

    /**
     * Wires up the handler for clicking on a delete link/button.
     *
     * @param widget
     *            The delete link/button.
     * @param msg
     *            The activity.
     * @param mainPanel
     *            The main activity panel.
     */
    private void setupDeleteClickHandler(final HasClickHandlers widget, final ActivityDTO msg, final Panel mainPanel)
    {
        widget.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete this activity?"))
                {
                    setupDeleteFadeout(msg, mainPanel);
                    performDelete(msg);

                }
            }
        });
    }

    /**
     * Action to actually do the delete.
     *
     * @param msg
     *            The activity.
     */
    protected void performDelete(final ActivityDTO msg)
    {
        if (msg.getDestinationStream().getType() == EntityType.RESOURCE)
        {
            ActivityModel.getInstance().hide(msg.getId());
        }
        else
        {
            ActivityModel.getInstance().delete(msg.getId());
        }
    }

    /**
     * Sets up to remove the activity on deletion.
     *
     * @param msg
     *            The activity.
     * @param mainPanel
     *            The main activity panel.
     */
    private void setupDeleteFadeout(final ActivityDTO msg, final Panel mainPanel)
    {
        Session.getInstance().getEventBus().addObserver(DeletedActivityResponseEvent.class,
                new Observer<DeletedActivityResponseEvent>()
                {
                    public void update(final DeletedActivityResponseEvent ev)
                    {
                        if (ev.getResponse() == msg.getId())
                        {
                            effects.fadeOut(mainPanel.getElement(), true);
                            Session.getInstance().getEventBus().removeObserver(ev, this);
                        }
                    }
                });
    }

    /**
     * @param inCreatePermalink
     *            the createPermalink to set
     */
    public void setCreatePermalink(final boolean inCreatePermalink)
    {
        createPermalink = inCreatePermalink;
    }

    /**
     * @return the objectDictionary
     */
    protected Map<BaseObjectType, ObjectRenderer> getObjectDictionary()
    {
        return objectDictionary;
    }

    /**
     * @return the verbDictionary
     */
    protected Map<ActivityVerb, VerbRenderer> getVerbDictionary()
    {
        return verbDictionary;
    }
}
