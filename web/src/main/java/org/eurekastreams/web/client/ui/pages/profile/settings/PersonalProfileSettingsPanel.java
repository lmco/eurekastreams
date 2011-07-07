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
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.SettingsPanel;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Represents the settings view of a person's profile.
 */
public class PersonalProfileSettingsPanel extends SettingsPanel
{
    /**
     * The panel.
     */
    static FlowPanel panel = new FlowPanel();

    /**
     * Constructor.
     */
    public PersonalProfileSettingsPanel()
    {
        super(panel, "Configure Profile");

        this.setPreviousPage(
                new CreateUrlRequest(Page.PEOPLE, Session.getInstance().getCurrentPerson().getAccountId()),
                "< Return to Profile");

        this.clearContentPanel();

        FlowPanel portalPageContainer = new FlowPanel();
        TabContainerPanel portalPage = new TabContainerPanel();

        PersonalProfileSettingsTabContent profileTabContent = new PersonalProfileSettingsTabContent();
        profileTabContent.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSettingsTabContent());

        PersonalStreamSettingsTabContent personalStreamSettingsTabContent = new PersonalStreamSettingsTabContent();
        personalStreamSettingsTabContent.addStyleName(StaticResourceBundle.INSTANCE.coreCss()
                .profileSettingsTabContent());

        portalPage.addTab(new SimpleTab("Basic Info", profileTabContent));
        portalPage.addTab(new SimpleTab("Stream Plugins", personalStreamSettingsTabContent));
        portalPage.init();

        portalPageContainer.add(portalPage);
        portalPageContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSettingsTabContainer());

        panel.add(portalPageContainer);
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profilePage());

    }
}
