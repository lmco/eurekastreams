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

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotResourceDTOResponseEvent;
import org.eurekastreams.web.client.model.LikeResourceModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.renderers.ResourceCountWidget;
import org.eurekastreams.web.client.ui.common.stream.renderers.ResourceCountWidget.CountType;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The Eureka Connect like and share eureka connect widget.
 */
public class LikeShareWidget extends Composite
{
    /**
     * Constructor.
     * 
     * @param resourceUrl
     *            resource url.
     * @param title
     *            the title.
     * @param desc
     *            the description.
     * @param thumbs
     *            the thumbnails.
     */
    public LikeShareWidget(final String resourceUrl, final String title, final String desc, final String[] thumbs)
    {
        final FlowPanel widget = new FlowPanel();
        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikeShareContainer());
        initWidget(widget);

        EventBus.getInstance().addObserver(GotResourceDTOResponseEvent.class,
                new Observer<GotResourceDTOResponseEvent>()
                {
                    public void update(final GotResourceDTOResponseEvent event)
                    {
                        widget.add(new ResourceCountWidget(CountType.LIKES, resourceUrl, title, desc, thumbs, event
                                .getResponse().getLikeCount(), event.getResponse().getLikersSample(), event
                                .getResponse().isLiked()));
                        widget.add(new ResourceCountWidget(CountType.SHARES, resourceUrl, title, desc, thumbs, event
                                .getResponse().getShareCount(), event.getResponse().getSharersSample(), event
                                .getResponse().isLiked()));
                    }
                });
        LikeResourceModel.getInstance().fetch(
                new SharedResourceRequest(resourceUrl, Session.getInstance().getCurrentPerson().getEntityId()), false);

    }
}
