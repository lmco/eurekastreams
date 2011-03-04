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

package org.eurekastreams.web.client.ui.pages.profile.settings;

import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.web.client.model.PersonStreamPluginSubscriptionModel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.profile.settings.stream.StreamPluginsSettingsTabContent;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Displays a users personal stream settings information.
 */
public class PersonalStreamSettingsTabContent extends FlowPanel
{
	/**
	 * The StreamPluginsSettingsTabContent we are utilizing via composition.
	 */
	StreamPluginsSettingsTabContent content = null;


    /**
     * The flow panel.
     */
    FlowPanel panel = new FlowPanel();

    /**
     * default constructor.
     */
    public PersonalStreamSettingsTabContent()
    {
        this.add(panel);
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamSettingsTabContent());
    	content = new StreamPluginsSettingsTabContent(PersonStreamPluginSubscriptionModel.getInstance());

        final FlowPanel pluginsContainer = new FlowPanel();
        panel.add(pluginsContainer);
        pluginsContainer.add(content);

    	final FlowPanel settingsContainer = new FlowPanel();
    	panel.add(settingsContainer);

    }

    /**
     * Get all available plugins and feed subscriptions.
     */
    public void getPluginsAndFeedSubscriptions()
    {
    	content.getPluginsAndFeedSubscriptions();
    }

    /**
     * Renders the plugins.
     */
    public void renderPlugins()
    {
    	content.renderPlugins();
    }

    /**
     * Renders the feed subscriptions.
     */
    public void renderFeedSubscriptions()
    {
    	content.renderFeedSubscriptions();
    }

    /**
     * Selects a plugin from the available plugins, allowing the user to add a new one.
     *
     * @param selectedMetaData
     *            MetaData of the selected plugin
     */
    public void selectPlugin(final GadgetMetaDataDTO selectedMetaData)
    {
    	content.selectPlugin(selectedMetaData);
    }

    /**
     * Edits a selected feed subscription.
     *
     * @param feedSubscription
     *            The feed subscription being edited
     * @param selectedMetaData
     *            The meta data of the selected feed's plugin
     */
    public void editFeedSubscription(final FeedSubscriber feedSubscription, final GadgetMetaDataDTO selectedMetaData)
    {
    	content.editFeedSubscription(feedSubscription, selectedMetaData);
    }
}
