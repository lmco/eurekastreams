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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;

/**
 * The Eureka Connect "comment widget" - displays a stream for a resource and allows posting.
 */
public class CommentWidget extends Composite
{
    /**
     * Constructor.
     *
     * @param view
     *            Unique ID of resource whose stream to display.
     */
    public CommentWidget(final String view)
    {
        final StreamPanel streamPanel = new StreamPanel(false);
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().embeddedWidget());
        initWidget(streamPanel);

        // TODO: use legitimate query data here

        String jsonRequest = StreamJsonRequestFactory.addRecipient(EntityType.PERSON, view,
                StreamJsonRequestFactory.getEmptyRequest()).toString();

        EventBus.getInstance().notifyObservers(new StreamRequestEvent("", jsonRequest));
        streamPanel.setStreamScope(new StreamScope(ScopeType.PERSON, view), true);

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                // hide everything but the post box if the stream is empty
                // but distinguish between an empty stream and no search results
                boolean emptyStream = Session.getInstance().getParameterValue("search") == null
                        && event.getStream().getPagedSet().isEmpty();
                if (emptyStream)
                {
                    streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyStream());
                }
                else
                {
                    streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyStream());
                }
            }
        });
    }
}
