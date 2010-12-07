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
package org.eurekastreams.web.client.ui.common.stream.comments;

import java.util.Date;

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.web.client.events.CommentDeletedEvent;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Background;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.stream.renderers.MetadataLinkRenderer;
import org.eurekastreams.web.client.ui.common.stream.transformers.HashtagLinkTransformer;
import org.eurekastreams.web.client.ui.common.stream.transformers.HyperlinkTransformer;
import org.eurekastreams.web.client.ui.common.stream.transformers.StreamSearchLinkBuilder;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Comment panel.
 *
 */
public class CommentPanel extends Composite
{
    /**
     * JSNI Facade.
     */
    private WidgetJSNIFacadeImpl jSNIFacade = new WidgetJSNIFacadeImpl();
    /**
     * Effects facade.
     */
    private EffectsFacade effects = new EffectsFacade();
    
    /**
     * Default constructor.
     *
     * @param comment
     *            the comment.
     */
    public CommentPanel(final CommentDTO comment)
    {
        final FlowPanel commentContainer = new FlowPanel();
        commentContainer.addStyleName("message-comment");

        commentContainer.add(new AvatarLinkPanel(EntityType.PERSON, comment.getAuthorAccountId(), comment
                .getAuthorId(), comment.getAuthorAvatarId(), Size.VerySmall, Background.White));

        FlowPanel body = new FlowPanel();
        body.addStyleName("body");
        commentContainer.add(body);

        Widget author =
                new MetadataLinkRenderer("", comment.getAuthorAccountId(), comment.getAuthorDisplayName()).render();
        author.addStyleName("message-comment-author");
        body.add(author);

        // first transform links to hyperlinks
        String commentBody = comment.getBody();
        
        // Strip out any existing HTML.
        commentBody = jSNIFacade.escapeHtml(commentBody);
        commentBody = commentBody.replaceAll(" ", "&nbsp;");
        commentBody = commentBody.replaceAll("(\r\n|\n|\r)", "<br />");
        
        // transform links
        commentBody =new HyperlinkTransformer(jSNIFacade).transform(commentBody);

        // then transform hashtags to hyperlinks
        commentBody = new HashtagLinkTransformer(new StreamSearchLinkBuilder()).transform(commentBody);
        
        Widget text = new InlineHTML(commentBody);
        text.addStyleName("message-comment-text");
        body.add(text);

        // timestamp and actions
        Panel timestampActions = new FlowPanel();
        timestampActions.addStyleName("comment-timestamp-actions-area");
        body.add(timestampActions);

        DateFormatter dateFormatter = new DateFormatter(new Date());
        Label dateLink = new InlineLabel(dateFormatter.timeAgo(comment.getTimeSent()));
        dateLink.addStyleName("comment-timestamp");
        timestampActions.add(dateLink);

        Panel actionsPanel = new FlowPanel();
        actionsPanel.addStyleName("comment-actions-area");
        timestampActions.add(actionsPanel);

        if (comment.isDeletable())
        {
            Label sep = new InlineLabel("\u2219");
            sep.addStyleName("action-link-separator");
            actionsPanel.add(sep);

            Label deleteLink = new InlineLabel("Delete");
            deleteLink.addStyleName("delete action-link linked-label");
            actionsPanel.add(deleteLink);

            deleteLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (jSNIFacade.confirm("Are you sure you want to delete this comment?"))
                    {
                        Session.getInstance().getActionProcessor().makeRequest(
                                new ActionRequestImpl<Boolean>("deleteComment", comment.getId()),
                                new AsyncCallback<Boolean>()
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
                                        effects.fadeOut(commentContainer.getElement());
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new CommentDeletedEvent(comment.getActivityId()));
                                    }
                                });
                    }
                }
            });
        }

        initWidget(commentContainer);
    }

}
