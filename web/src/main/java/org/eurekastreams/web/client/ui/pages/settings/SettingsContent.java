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
package org.eurekastreams.web.client.ui.pages.settings;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.Pager;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A directory of item.
 */
public class SettingsContent extends Composite
{
    /**
     * Holds the tab section of the settings display.
     */
    private final TabContainerPanel tabContainer;

    /**
     * Personal Panel.
     */
    private final NotificationsSettingsPanelComposite personalPanel;

    /**
     * System Panel.
     */
    private final SystemSettingsPanelComposite systemPanel;

    /**
     * processor.
     */
    private final ActionProcessor processor;

    /**
     * The root panel.
     */
    private final Panel rootPanel;

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
        rootPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().settings());

        personalPanel = new NotificationsSettingsPanelComposite();
        personalPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().settingsTab());
        systemPanel = new SystemSettingsPanelComposite(processor);
        systemPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().settingsTab());

        tabContainer = new TabContainerPanel();

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            tabContainer.addTab(new SimpleTab("System", systemPanel));

            SimpleTab pluginsTab = new SimpleTab("Plugins", new StreamPluginsPanel());
            pluginsTab.setParamsToClear(PagedListPanel.URL_PARAM_LIST_ID, PagedListPanel.URL_PARAM_FILTER,
                    PagedListPanel.URL_PARAM_SORT, Pager.URL_PARAM_START_INDEX, Pager.URL_PARAM_END_INDEX);

            tabContainer.addTab(pluginsTab);
            tabContainer.addTab(new PendingGroupsAndFlaggedActivitiesPanelComposite("Pending"));
        }

        tabContainer.addTab(new SimpleTab("Notifications", personalPanel));

        panel.add(tabContainer);
        tabContainer.init();
        initWidget(panel);
    }
}
