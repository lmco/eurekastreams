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
package org.eurekastreams.web.client.ui.pages.start.preferences;

import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PreferencePaneInactivateEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel containing portal preferences.
 */
public class PortalPreferencePanel extends FlowPanel
{

    /**
     * The close button.
     */
    private Anchor closeButton = new Anchor("close panel");

    /**
     * Container for the preferences.
     */
    private FlowPanel prefPaneContainer = new FlowPanel();

    /**
     * Inner container.
     */
    private FlowPanel innerPanel = new FlowPanel();

    /**
     * Title of the preference panel.
     */
    private FlowPanel titlePanel = new FlowPanel();

    /**
     * Container for title bar.
     */
    private FlowPanel titleBarWidgetContainer = new FlowPanel();

    /**
     * The name.
     */
    private Label name = new Label();

    /**
     * Default constructory.
     */
    public PortalPreferencePanel()
    {
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().portalPrefPane());
        titleBarWidgetContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().titleBarWidget());
        innerPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().inner());
        titlePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        titlePanel.add(name);
        titlePanel.add(titleBarWidgetContainer);
        titlePanel.add(closeButton);
        innerPanel.add(titlePanel);
        innerPanel.add(prefPaneContainer);
        this.add(innerPanel);


        Session.getInstance().getEventBus().addObserver(PreferencePaneInactivateEvent.class,
                new Observer<PreferencePaneInactivateEvent>()
                {
                    public void update(final PreferencePaneInactivateEvent arg1)
                    {
                        hidePanel();
                    }
                });

        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                Session.getInstance().getEventBus().notifyObservers(new PreferencePaneInactivateEvent());
            }
        });

        hidePanel();
    }

    /**
     * Hide the preference panel.
     */
    public void hidePanel()
    {
        this.setVisible(false);
        RootPanel.get().removeStyleName(StaticResourceBundle.INSTANCE.coreCss().prefPanelVisible());
    }

    /**
     * Show the preference panel.
     */
    public void showPanel()
    {
        RootPanel.get().addStyleName(StaticResourceBundle.INSTANCE.coreCss().prefPanelVisible());
        this.setVisible(true);
    }

    /**
     * Set the widget to be used for preferences.
     *
     * @param prefPane
     *            the widget.
     */
    public void setPreferenceWidget(final PortalPreferenceFacade prefPane)
    {
        name.setText(prefPane.getTitle());

        prefPaneContainer.clear();
        prefPaneContainer.add((Widget) prefPane);
        if (prefPane.getTitleBarWidget() != null)
        {
            titleBarWidgetContainer.clear();
            prefPaneContainer.add(prefPane.getTitleBarWidget());
        }
        showPanel();
    }
}
