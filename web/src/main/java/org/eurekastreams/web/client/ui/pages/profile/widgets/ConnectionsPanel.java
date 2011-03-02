/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import java.util.HashMap;

import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Shows a person's connections as part of his or her profile.
 */
public class ConnectionsPanel extends FlowPanel
{
    /**
     * Connection count panels.
     */
    private final HashMap<String, ConnectionCountPanel> countPanels = new HashMap<String, ConnectionCountPanel>();
    /**
     * The contents.
     */
    private final FlowPanel contents = new FlowPanel();

    /**
     * Constructor. Pass null to prevent a value from being shown.
     *
     */
    public ConnectionsPanel()
    {
        Label subheader = new Label("Connections");
        subheader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSubheader());
        this.add(subheader);

        this.add(contents);

        SimplePanel simple = new SimplePanel();
        simple.addStyleName(StaticResourceBundle.INSTANCE.coreCss().clear());
        this.add(simple);
    }

    /**
     * Add a connection panel.
     *
     * @param name
     *            the name.
     * @param sortKey
     *            the sort key,
     * @param number
     *            the number.
     */
    public void addConnection(final String name, final String sortKey, final int number)
    {
        addConnection(name, sortKey, number, "");
    }

    /**
     * Add a connection panel.
     *
     * @param name
     *            the name.
     * @param sortKey
     *            the sort key,
     * @param number
     *            the number.
     * @param style
     *            the custom css style.
     */
    public void addConnection(final String name, final String sortKey, final int number, final String style)
    {
        ConnectionCountPanel connCount = new ConnectionCountPanel(name, number, style);
        contents.add(connCount);
        countPanels.put(name, connCount);
        connCount.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(StaticResourceBundle.INSTANCE.coreCss().tab(), "Connections");
                params.put(PagedListPanel.URL_PARAM_LIST_ID, "connections");
                params.put(PagedListPanel.URL_PARAM_FILTER, name);
                if (sortKey != null)
                {
                    params.put(PagedListPanel.URL_PARAM_SORT, sortKey);
                }
                Session.getInstance().getEventBus()
                        .notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(params, true)));
            }
        });
    }

    /**
     * Update the count of a panel.
     *
     * @param name
     *            the name.
     * @param number
     *            the number.
     */
    public void updateCount(final String name, final int number)
    {
        countPanels.get(name).updateCount(number);
    }
}
