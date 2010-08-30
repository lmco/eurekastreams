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

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Widget for liking activity.
 */
public class LikeWidget extends Composite
{
    /** Current state of activity. */
    private Boolean liked;

    /**
     * Number of times the activity has been liked.
     */
    private Long likeCount;
    
    /**
     * The widget.
     */
    private FlowPanel widget = new FlowPanel();
    
    /**
     * The like link.
     */
    private Anchor likeLink = new Anchor();
    
    /**
     * The like count link.
     */
    private Anchor likeCountLink = new Anchor();


    /**
     * Default constructor.
     * 
     * @param isLiked
     *            whether its liked by the current user.
     * @param inLikeCount
     *            the number of times the activity has been liked.
     * @param activityId
     *            the activity ID.
     */
    public LikeWidget(final Boolean isLiked, final Long inLikeCount, final Long activityId)
    {
        widget.addStyleName("like-wrapper");
        liked = isLiked;
        likeCount = inLikeCount;
        
        likeLink.addStyleName("linked-label like");
        setText();
        
        widget.add(likeLink);
        widget.add(likeCountLink);

        likeLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                SetActivityLikeRequest request = new SetActivityLikeRequest(
                        activityId, liked ? LikeActionType.REMOVE_LIKE
                                : LikeActionType.ADD_LIKE);

                Session.getInstance().getActionProcessor().makeRequest(
                        new ActionRequestImpl<Boolean>("setActivityLiked",
                                request), new AsyncCallback<Boolean>()
                        {
                            /* implement the async call back methods */
                            public void onFailure(final Throwable caught)
                            {
                                // TODO handle error.
                            }

                            public void onSuccess(final Boolean result)
                            {
                                liked = !liked;
                                
                                if (liked)
                                {
                                    likeCount += 1;
                                }
                                else
                                {
                                    likeCount -= 1;
                                }
                                
                                setText();
                            }
                        });
            }

        });

        initWidget(widget);
    }
    
    /**
     * Sets the text based on the current state.
     */
    private void setText()
    {
        likeLink.setText(liked ? "Unlike" : "Like");
        likeCountLink.setText(likeCount.toString());
    }
}
