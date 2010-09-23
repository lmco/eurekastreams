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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import java.util.List;

import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.GotStreamPopularHashTagsEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.model.PopularHashTagsModel;
import org.eurekastreams.web.client.ui.common.stream.transformers.HashtagLinkTransformer;
import org.eurekastreams.web.client.ui.common.stream.transformers.StreamSearchLinkBuilder;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays popular hash tags.
 */
public class PopularHashtagsPanel extends FlowPanel
{
    /**
     * Link transformer.
     */
    private HashtagLinkTransformer linkTransformer = new HashtagLinkTransformer(new StreamSearchLinkBuilder());

    /**
     * Constructor.
     * 
     * @param scopeType
     *            scope type.
     * @param uniqueId
     *            unique id.
     */
    public PopularHashtagsPanel(final ScopeType scopeType, final String uniqueId)
    {

        PopularHashTagsModel.getInstance().fetch(new StreamPopularHashTagsRequest(scopeType, uniqueId), true);

        final PopularHashtagsPanel widget = this;

        EventBus.getInstance().addObserver(GotStreamPopularHashTagsEvent.class,
                new Observer<GotStreamPopularHashTagsEvent>()
                {

                    public void update(final GotStreamPopularHashTagsEvent response)
                    {
                        final List<String> hashTags = response.getPopularHashTags();

                        final Label title = new Label("Popular Hashtags");
                        title.addStyleName("profile-subheader");

                        widget.add(title);
                        widget.addStyleName("popular-hashtags");

                        if (hashTags.size() > 0)
                        {

                            for (String hashTag : hashTags)
                            {
                                widget.add(new HTML(linkTransformer.transform(hashTag)));
                            }
                        }
                        else
                        {
                            widget.setVisible(false);
                        }
                    }
                });

    }
}
