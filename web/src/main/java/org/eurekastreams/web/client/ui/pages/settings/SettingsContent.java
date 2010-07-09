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
package org.eurekastreams.web.client.ui.pages.settings;


import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A directory of item.
 */
public class SettingsContent extends Composite implements Bindable
{

    /**
     * Holds the tab section of the settings display.
     */
    TabContainerPanel tabContainer;

    /**
     * Personal Panel.
     */
    NotificationsSettingsPanelComposite personalPanel;

    /**
     * System Panel.
     */
    SystemSettingsPanelComposite systemPanel;

    /**
     * processor.
     */
    ActionProcessor processor;

    /**
     * The root panel.
     */
    Panel rootPanel;

    /**
     * Constructor.
     *
     * @param inProcessor
     *            for contacting the server.
     */
    public SettingsContent(final ActionProcessor inProcessor)
    {
        FlowPanel panel = new FlowPanel();
        processor = inProcessor;

        rootPanel = RootPanel.get();
        rootPanel.addStyleName("settings");

        personalPanel = new NotificationsSettingsPanelComposite();
        personalPanel.addStyleName("settings-tab");
        systemPanel = new SystemSettingsPanelComposite(processor);
        systemPanel.addStyleName("settings-tab");

        tabContainer = new TabContainerPanel();

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.ROOT_ORG_COORDINATOR))
        {
            tabContainer.addTab(new SimpleTab("System", systemPanel));
            tabContainer.addTab(new SimpleTab("Plugins", new StreamPluginsPanel()));
        }

        tabContainer.addTab(new SimpleTab("Notifications", personalPanel));
        
        panel.add(tabContainer);
        tabContainer.init();
        initWidget(panel);
    }
}
