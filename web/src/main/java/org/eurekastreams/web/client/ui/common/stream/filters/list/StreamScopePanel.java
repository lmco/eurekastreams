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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.StreamScopeDeletedEvent;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel for 1 stream scope.
 *
 */
public class StreamScopePanel extends FlowPanel
{
    /**
     * Default constructor.
     * @param scope the scope.
     */
    public StreamScopePanel(final StreamScope scope)
    {

        this.addStyleName("stream-scope");
        if (scope.getDisplayName() == null)
        {
            this.add(new Label(scope.getUniqueKey()));
            this.addStyleName("deleted");
        }
        else
        {
            this.add(new Label(scope.getDisplayName()));
        }

        FlowPanel anchorContainer = new FlowPanel();
        anchorContainer.addStyleName("gwt-Hyperlink");
        Anchor close = new Anchor("X");
        close.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                EventBus.getInstance().notifyObservers(new StreamScopeDeletedEvent(scope));
            }
        });
        anchorContainer.add(close);
        this.add(anchorContainer);
    }
}
