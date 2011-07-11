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
package org.eurekastreams.web.client.ui.common.stream.comments;

import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.web.client.events.CommentAddedEvent;
import org.eurekastreams.web.client.events.CommentDeletedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.utility.BaseActivityLinkBuilder;
import org.eurekastreams.web.client.utility.LinkBuilderHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Shows a list of comments.
 *
 */
public class CommentsListPanel extends FlowPanel
{
    /**
     * Effects facade for fading.
     */
    private final EffectsFacade effects = new EffectsFacade();

    /**
     * Whether or not the post comment is shown.
     */
    private boolean postCommentShown = false;

    /**
     * Only show 20 comments inline max.
     */
    private static final int MAX_INLINE_COMMENTS = 20;

    /**
     * The post comment panel.
     */
    private final PostCommentPanel postCommentPanel;

    /**
     * Number of Comments for this activity.
     */
    private Integer commentCount;

    /**
     * if to allow additional comments.
     */
    private final boolean allowAdditionalComments;

    /**
     * Anchor with test to view all comments.
     */
    private Anchor showAllComments;

    /** For building links to activities. (Static just to save overhead since it's stateless.) */
    private static LinkBuilderHelper activityPermalinkBuilder = new LinkBuilderHelper();

    /**
     * Default constructor.
     *
     * @param firstComment
     *            the first comment.
     * @param lastComment
     *            the last comment.
     * @param inCommentCount
     *            the comment count.
     * @param messageId
     *            the message id.
     * @param inAllowAdditionalComments
     *            if the commentsListpanel Should enable more commenting.
     * @param streamType
     *            Type of entity in whose stream the activity appears (for building an appropriate permalink URL).
     *            Optional.
     * @param streamUniqueId
     *            Shortname of the stream in which the activity appears (for building an appropriate permalink URL).
     *            Optional.
     * @param activityLinkBuilder
     *            For building the link to view the activity by itself with all comments.
     */
    public CommentsListPanel(final CommentDTO firstComment, final CommentDTO lastComment,
            final Integer inCommentCount, final Long messageId, final boolean inAllowAdditionalComments,
            final EntityType streamType, final String streamUniqueId, final BaseActivityLinkBuilder activityLinkBuilder)
    {
        commentCount = inCommentCount;
        allowAdditionalComments = inAllowAdditionalComments;
        postCommentPanel = new PostCommentPanel(messageId, inCommentCount == 0);
        final CommentsListPanel thisBuffered = this;
        if (commentCount > 0)
        {
            this.add(new CommentPanel(firstComment));
            if (commentCount > MAX_INLINE_COMMENTS)
            {
                String url = activityLinkBuilder.buildActivityPermalink(messageId, streamType, streamUniqueId);

                showAllComments = new Anchor("View all " + commentCount.toString() + " comments", "#" + url);

                showAllComments.addStyleName(StaticResourceBundle.INSTANCE.coreCss().showAllComments());
                this.add(showAllComments);
            }
            else if (commentCount > 2)
            {
                showAllComments = new Anchor("View all " + commentCount.toString() + " comments");
                showAllComments.addStyleName(StaticResourceBundle.INSTANCE.coreCss().showAllComments());

                showAllComments.addClickHandler(new ClickHandler()
                {
                    public void onClick(final ClickEvent event)
                    {
                        // TODO: refactor to use new simplified model design
                        Session.getInstance().getActionProcessor()
                                .makeRequest("getActivityById", messageId, new AsyncCallback<ActivityDTO>()
                                {
                                    public void onFailure(final Throwable caught)
                                    {
                                    }

                                    public void onSuccess(final ActivityDTO result)
                                    {
                                        renderAllComments(result.getComments());
                                    }
                                });
                    }
                });
                this.add(showAllComments);
            }
            if (commentCount > 1)
            {
                this.add(new CommentPanel(lastComment));
            }

            addPostCommentPanel();
        }

        Session.getInstance().getEventBus().addObserver(CommentAddedEvent.class, new Observer<CommentAddedEvent>()
        {
            public void update(final CommentAddedEvent arg1)
            {
                if (messageId.equals(arg1.getMessageId()))
                {
                    CommentPanel commentPanel = new CommentPanel(arg1.getComment());

                    int index = thisBuffered.getWidgetCount() - 1;

                    if (index < 0)
                    {
                        index = 0;
                    }
                    thisBuffered.insert(commentPanel, index);

                    effects.fadeIn(commentPanel.getElement(), true);
                    commentCount++;
                    if (showAllComments != null)
                    {
                        showAllComments.setHTML("View all " + commentCount.toString() + " comments");
                    }
                }
            }
        });

        Session.getInstance().getEventBus().addObserver(CommentDeletedEvent.class, new Observer<CommentDeletedEvent>()
        {
            public void update(final CommentDeletedEvent arg1)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new ShowNotificationEvent(new Notification("Comment has been deleted")));

                if (messageId.equals(arg1.getMessageId()))
                {
                    commentCount--;
                    if (commentCount <= 2)
                    {
                        // TODO: refactor to use new simplified model design
                        Session.getInstance().getActionProcessor()
                                .makeRequest("getActivityById", messageId, new AsyncCallback<ActivityDTO>()
                                {
                                    public void onFailure(final Throwable caught)
                                    {
                                    }

                                    public void onSuccess(final ActivityDTO result)
                                    {
                                        renderAllComments(result.getComments());
                                    }
                                });
                    }
                    else if (showAllComments != null)
                    {
                        showAllComments.setHTML("View all " + commentCount.toString() + " comments");
                    }
                }
            }
        });

    }

    /**
     * Activate the post comment.
     */
    public void activatePostComment()
    {
        if (!postCommentShown)
        {
            addPostCommentPanel();
        }
        postCommentPanel.activate();
    }

    /**
     * Add the post comment panel.
     */
    private void addPostCommentPanel()
    {
        if (allowAdditionalComments)
        {
            this.add(postCommentPanel);
            postCommentShown = true;
        }
    }

    /**
     * Render all the comments.
     *
     * @param comments
     *            the comments.
     */
    public void renderAllComments(final List<CommentDTO> comments)
    {
        this.clear();
        for (CommentDTO comment : comments)
        {
            this.add(new CommentPanel(comment));
        }
        addPostCommentPanel();
    }
}
