/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.support;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget with a text box used to initiate a stream search. NOTE: This widget is only used with the Eureka Connect
 * widgets.
 */
public class StreamSearchBoxWidget extends Composite
{
    /** The search button. */
    Label searchGo = new Label("Search");

    /** The text box for entering the search term. */
    LabeledTextBox searchTerm = new LabeledTextBox("search all activity");


    /**
     * Constructor.
     */
    public StreamSearchBoxWidget()
    {
        FlowPanel mainPanel = new FlowPanel();

        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchBox());
        mainPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchList());

        searchTerm.setTitle("search this stream");
        mainPanel.add(searchTerm);

        searchGo.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchListButton());
        mainPanel.add(searchGo);

        initWidget(mainPanel);

        searchGo.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                onSearch();
            }
        });

        searchTerm.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown()
                        && searchTerm.getText().length() > 0)
                {
                    onSearch();
                }
            }
        });

        EventBus.getInstance().addObserver(StreamSearchBeginEvent.class, new Observer<StreamSearchBeginEvent>()
        {
            public void update(final StreamSearchBeginEvent ev)
            {
                if (ev.getSearchText() == null)
                {
                    onSearchCanceled();
                }
            }
        });
    }

    /**
     * When the search potentially changes.
     */
    private void onSearch()
    {
        EventBus.getInstance().notifyObservers(new StreamSearchBeginEvent(searchTerm.getText()));
    }

    /**
     * Update search widget.
     *
     * @param inSearchTerm
     *            the search term.
     */
    public void setSearchTerm(final String inSearchTerm)
    {
        searchTerm.setText(inSearchTerm);
        searchTerm.checkBox();
    }

    /**
     * Called when a search is canceled.
     */
    public void onSearchCanceled()
    {
        searchTerm.setText("");
        searchTerm.checkBox();
    }

    /**
     * Sets if the search can be changed.
     *
     * @param canChange
     *            if the search can be changed.
     */
    public void setCanChange(final boolean canChange)
    {
        searchTerm.setVisible(canChange);
        searchGo.setVisible(canChange);
    }
}
