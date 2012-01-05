/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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

import java.util.Date;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.web.client.events.CommentDeletedEvent;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.stream.renderers.MetadataLinkRenderer;
import org.eurekastreams.web.client.ui.common.stream.transformers.CommentBodyTransformer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Comment panel.
 */
public class CommentPanel extends Composite
{
    /** Length above which comments qualify to be truncated. */
    private static final int COMMENT_LIMIT = 300;

    /** Length at which long comments are truncated. */
    private static final int TRUNCATE_LENGTH = 250;

    /** JSNI Facade. */
    private final WidgetJSNIFacadeImpl jsniFacade = new WidgetJSNIFacadeImpl();

    /** Effects facade. */
    private final EffectsFacade effects = new EffectsFacade();

    /** For converting to HTML. */
    private final CommentBodyTransformer commentBodyTransformer = new CommentBodyTransformer(jsniFacade);

    /**
     * Default constructor.
     * 
     * @param comment
     *            the comment.
     */
    public CommentPanel(final CommentDTO comment)
    {
        final FlowPanel commentContainer = new FlowPanel();
        commentContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageComment());

        commentContainer.add(new AvatarLinkPanel(EntityType.PERSON, comment.getAuthorAccountId(), comment
                .getAuthorAvatarId(), Size.VerySmall));

        FlowPanel body = new FlowPanel();
        body.addStyleName(StaticResourceBundle.INSTANCE.coreCss().body());
        commentContainer.add(body);

        Widget author = new MetadataLinkRenderer("", comment.getAuthorAccountId(), comment.getAuthorDisplayName())
        .render();
        author.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageCommentAuthor());
        body.add(author);

        // build/display the comment content, truncating if too long
        String rawCommentBody = comment.getBody().trim();
        final String fullCommentBody = commentBodyTransformer.transform(rawCommentBody);
        String initialCommentBody = fullCommentBody;

        boolean oversize = rawCommentBody.length() > COMMENT_LIMIT;
        if (oversize)
        {
            initialCommentBody = fullCommentBody.substring(0,
                    commentBodyTransformer.determineTruncationPoint(fullCommentBody, TRUNCATE_LENGTH))
                    + "...";
        }

        final HTML text = new InlineHTML(initialCommentBody);
        text.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageCommentText());
        body.add(text);

        if (oversize)
        {
            final InlineLabel more = new InlineLabel("show more");
            more.addStyleName(StaticResourceBundle.INSTANCE.coreCss().showMoreCommentLink());
            more.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
            more.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent inEvent)
                {
                    more.removeFromParent();
                    text.setHTML(fullCommentBody);
                }
            });
            body.add(more);
        }

        // timestamp and actions
        Panel timestampActions = new FlowPanel();
        timestampActions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().commentTimestamp());
        body.add(timestampActions);

        DateFormatter dateFormatter = new DateFormatter(new Date());
        Label dateLink = new InlineLabel(dateFormatter.timeAgo(comment.getTimeSent()));
        dateLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().commentTimestamp());
        timestampActions.add(dateLink);

        Panel actionsPanel = new FlowPanel();
        timestampActions.add(actionsPanel);

        if (comment.isDeletable())
        {
            Label sep = new InlineLabel("\u2219");
            sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
            actionsPanel.add(sep);

            Label deleteLink = new InlineLabel("Delete");
            deleteLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().delete());
            deleteLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());

            actionsPanel.add(deleteLink);

            deleteLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (jsniFacade.confirm("Are you sure you want to delete this comment?"))
                    {
                        Session.getInstance().getActionProcessor()
                        .makeRequest("deleteComment", comment.getId(), new AsyncCallback<Boolean>()
                                {
                            /*
                             * implement the async call back methods
                             */
                            public void onFailure(final Throwable caught)
                            {
                                // No failure state.
                            }

                            public void onSuccess(final Boolean result)
                            {
                                effects.fadeOut(commentContainer.getElement(), true);
                                Session.getInstance().getEventBus()
                                .notifyObservers(new CommentDeletedEvent(comment.getActivityId()));
                            }
                                });
                    }
                }
            });
        }

        initWidget(commentContainer);
    }
}
