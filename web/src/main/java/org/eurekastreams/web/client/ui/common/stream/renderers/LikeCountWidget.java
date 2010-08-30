/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class LikeCountWidget extends Composite
{
    private FlowPanel widget = new FlowPanel();

    private FlowPanel usersWhoLikedPanel = new FlowPanel();

    private Long likeCount = 0L;

    public LikeCountWidget(final Long inLikeCount)
    {
        widget.addStyleName("like-count-widget");
        likeCount = inLikeCount;
        initWidget(widget);
        
        final FlowPanel likeCountPanel = new FlowPanel();
        likeCountPanel.addStyleName("like-count");
        
        final Anchor likeCountLink = new Anchor(likeCount.toString());
        likeCountPanel.add(likeCountLink);
        
        widget.add(likeCountPanel);
        
        usersWhoLikedPanel.addStyleName("users-who-liked-activity");

        FlowPanel userLikedHeader = new FlowPanel();
        userLikedHeader.addStyleName("users-who-liked-activity-header");
        usersWhoLikedPanel.add(userLikedHeader);

        FlowPanel userLikedBody = new FlowPanel();
        userLikedBody.addStyleName("users-who-liked-activity-body");
        usersWhoLikedPanel.add(userLikedBody);
        
        FlowPanel userLikedFooter = new FlowPanel();
        userLikedFooter.addStyleName("users-who-liked-activity-footer");
        usersWhoLikedPanel.add(userLikedFooter);
        
        userLikedBody.add(new Label(likeCount + " people liked this"));

        widget.add(usersWhoLikedPanel);
    }

    public void addLike()
    {
        likeCount += 1L;
    }

    public void removeLike()
    {
        likeCount -= 1L;
    }
}
