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

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotResourceActorsEvent;
import org.eurekastreams.web.client.model.GetResourceActorsModel;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.ResourceCountWidget.CountType;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The Eureka Connect actor list widget.
 */
public class ActorListWidget extends Composite
{
    /**
     * Constructor.
     * 
     * @param type
     *            Type of actors.
     * @param resourceUrl
     *            the resoure url.
     */
    public ActorListWidget(final CountType type, final String resourceUrl)
    {
        final FlowPanel widget = new FlowPanel();
        initWidget(widget);

        EventBus.getInstance().addObserver(GotResourceActorsEvent.class, new Observer<GotResourceActorsEvent>()
        {
            public void update(final GotResourceActorsEvent event)
            {
                PersonRenderer renderer = new PersonRenderer(false);

                FlowPanel scrollable = new FlowPanel();
                scrollable.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likersContent());
                widget.add(scrollable);

                for (PersonModelView person : event.getResponse())
                {
                    scrollable.add(renderer.render(person));
                }
            }
        });

        HashMap<String, Serializable> req = new HashMap<String, Serializable>();
        req.put("type", type);
        req.put("resourceUrl", resourceUrl);

        GetResourceActorsModel.getInstance().fetch(req, false);

    }
}
