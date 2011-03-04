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
package org.eurekastreams.web.client.ui.pages.profile.settings;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.GroupStreamPluginSubscriptionModel;
import org.eurekastreams.web.client.ui.common.SettingsPanel;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.profile.settings.stream.StreamPluginsSettingsTabContent;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A panel for changing the settings of an group.
 */
public class GroupProfileSettingsPanel extends SettingsPanel
{

    /**
     * The panel.
     */
    static FlowPanel panel = new FlowPanel();

    /**
     * The delete-group button.
     */
    Anchor deleteButton = new Anchor("");

    /**
     * The processing spinner.
     */
    Label processingSpinny = new Label("Processing...");

    /**
     * Constructor.
     *
     * @param groupName
     *            group name.
     */
    public GroupProfileSettingsPanel(final String groupName)
    {
        super(panel, "Configure Profile");
        this.clearContentPanel();
        
        this.setPreviousPage(new CreateUrlRequest(Page.GROUPS, groupName), "< Return to Profile");
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().groupProfileSettingsPanel());

        FlowPanel portalPageContainer = new FlowPanel();
        TabContainerPanel portalPage = new TabContainerPanel();

        GroupProfileSettingsTabContent profileTabContent = new GroupProfileSettingsTabContent(groupName);
        profileTabContent.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSettingsTabContent());

        StreamPluginsSettingsTabContent streamSettingsTabContent = new StreamPluginsSettingsTabContent(
                GroupStreamPluginSubscriptionModel.getInstance());
        streamSettingsTabContent.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSettingsTabContent());

        portalPage.addTab(new SimpleTab("Basic Info", profileTabContent));
        portalPage.addTab(new SimpleTab("Stream Plugins", streamSettingsTabContent));
        portalPage.init();

        portalPageContainer.add(portalPage);
        portalPageContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSettingsTabContainer());

        panel.add(portalPageContainer);
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profilePage());
    }

}
