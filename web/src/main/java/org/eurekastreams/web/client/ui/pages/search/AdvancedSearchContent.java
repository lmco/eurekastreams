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
package org.eurekastreams.web.client.ui.pages.search;

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamListPanel;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Page to test advanced search features.
 */
public class AdvancedSearchContent extends FlowPanel
{
    /**
     * Initialize page.
     */
    public AdvancedSearchContent()
    {
        RootPanel.get().addStyleName("advanced-search");
        
        final TextArea json = new TextArea();
        final Anchor search = new Anchor("search");
        final Label error = new Label("");
        final StreamListPanel stream = new StreamListPanel(new StreamMessageItemRenderer(true));
        stream.addStyleName("stream");
        stream.setVisible(false);
        
        this.add(json);
        this.add(search);
        this.add(error);
        this.add(stream);

        search.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                Session.getInstance().getActionProcessor().makeRequest(
                        new ActionRequestImpl<PagedSet<ActivityDTO>>("getActivitiesByRequest", json.getText()),
                        new AsyncCallback<PagedSet<ActivityDTO>>()
                        {
                            public void onFailure(final Throwable err)
                            {
                                error.setText(err.toString());
                                stream.setVisible(false);
                            }

                            public void onSuccess(final PagedSet<ActivityDTO> activity)
                            {
                                error.setText("");
                                EventBus.getInstance().notifyObservers(new MessageStreamUpdateEvent(activity));
                                stream.setVisible(true);
                            }
                        });
            }
        });
    }
}
