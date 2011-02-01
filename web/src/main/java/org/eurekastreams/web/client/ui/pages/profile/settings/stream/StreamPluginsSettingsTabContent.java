/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.profile.settings.stream;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.action.request.feed.DeleteFeedSubscriptionRequest;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamPluginsUpdateCanceledEvent;
import org.eurekastreams.web.client.events.data.DeletedStreamPluginSubscriptionResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamPluginSubscriptionsResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedStreamPluginSubscriptionResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedStreamPluginSubscriptionResponseEvent;
import org.eurekastreams.web.client.jsni.GadgetMetaDataFetcher;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.BaseModel;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.Fetchable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.EditPanel;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * The display for the stream plugin settings tab.
 *
 */
public class StreamPluginsSettingsTabContent extends Composite
{
    /**
     * The action processor.
     */
    ActionProcessor processor;

    /**
     * The flow panel.
     */
    FlowPanel panel = new FlowPanel();

    /**
     * The Settings Container Panel.
     */
    FlowPanel streamPluginSettingsContainer = new FlowPanel();

    /**
     * A container panel for available plugins.
     */
    FlowPanel availablePluginsContainer = new FlowPanel();

    /**
     * A container panel for my plugins.
     */
    FlowPanel feedSubscriptionsContainer = new FlowPanel();

    /**
     * Stores available plugins.
     */
    List<PluginDefinition> availablePlugins = new LinkedList<PluginDefinition>();

    /**
     * Available Plugins Meta Data.
     */
    List<GadgetMetaDataDTO> availablePluginsMetaData = new LinkedList<GadgetMetaDataDTO>();

    /**
     * Feed Subscriptions List.
     */
    List<FeedSubscriber> feedSubscriptions = new LinkedList<FeedSubscriber>();

    /**
     * A map of plugin ids to their corresponding container panels.
     */
    HashMap<Long, FlowPanel> availablePluginsById = new HashMap<Long, FlowPanel>();

    /**
     * List of metadata.
     */
    private List<GadgetMetaDataDTO> metadata;

    /**
     * BaseModel.
     */
    private final BaseModel model;

    /**
     * JSNI facade.
     */
    private final WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * The default constructor.
     *
     * @param inModel
     *            The base model
     */
    public StreamPluginsSettingsTabContent(final BaseModel inModel)
    {

        processor = Session.getInstance().getActionProcessor();

        model = inModel;

        Session.getInstance().getEventBus().addObserver(GotStreamPluginSubscriptionsResponseEvent.class,
                new Observer<GotStreamPluginSubscriptionsResponseEvent>()
                {
                    public void update(final GotStreamPluginSubscriptionsResponseEvent event)
                    {
                        availablePlugins = event.getResponse().getPlugins();
                        feedSubscriptions = event.getResponse().getFeedSubcribers();

                        GadgetMetaDataFetcher gadgetMetaDataFetcher = new GadgetMetaDataFetcher(availablePlugins);
                        gadgetMetaDataFetcher
                                .addOnMetaDataRetrievedCommand(new GadgetMetaDataFetcher.GotGadgetMetaDataCommand()
                                {
                                    public void onGotGadgetMetaData(final List<GadgetMetaDataDTO> inMetadata)
                                    {
                                        metadata = inMetadata;
                                        renderPlugins();
                                        renderFeedSubscriptions();
                                    }
                                });
                        gadgetMetaDataFetcher.fetchMetaData();
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamPluginsUpdateCanceledEvent.class,
                new Observer<StreamPluginsUpdateCanceledEvent>()
                {
                    public void update(final StreamPluginsUpdateCanceledEvent event)
                    {
                        renderInsides();
                    }
                });

        Session.getInstance().getEventBus().addObserver(DeletedStreamPluginSubscriptionResponseEvent.class,
                new Observer<DeletedStreamPluginSubscriptionResponseEvent>()
                {
                    public void update(final DeletedStreamPluginSubscriptionResponseEvent event)
                    {
                        renderInsides();
                        Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                new Notification("Stream plugin has been deleted")));
                    }
                });

        Session.getInstance().getEventBus().addObserver(InsertedStreamPluginSubscriptionResponseEvent.class,
                new Observer<InsertedStreamPluginSubscriptionResponseEvent>()
                {
                    public void update(final InsertedStreamPluginSubscriptionResponseEvent arg1)
                    {
                        renderInsides();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(
                                        new Notification("Stream Plugin has been successfully added.")));
                    }
                });

        Session.getInstance().getEventBus().addObserver(UpdatedStreamPluginSubscriptionResponseEvent.class,
                new Observer<UpdatedStreamPluginSubscriptionResponseEvent>()
                {
                    public void update(final UpdatedStreamPluginSubscriptionResponseEvent arg1)
                    {
                        renderInsides();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification(
                                        "Stream Plugin has been successfully updated.")));
                    }
                });

        renderInsides();

        initWidget(panel);
    }

    /**
     * Render the default view of this tab.
     */
    private void renderInsides()
    {
        panel.clear();
        streamPluginSettingsContainer.clear();
        availablePluginsContainer.clear();
        feedSubscriptionsContainer.clear();
        feedSubscriptionsContainer.setVisible(true);

        streamPluginSettingsContainer.addStyleName("stream-plugins-settings-container");

        availablePluginsContainer.addStyleName("filters");

        getPluginsAndFeedSubscriptions();

        feedSubscriptionsContainer.addStyleName("stream-plugins-feed-subscriptions-container");

        feedSubscriptions.add(new FeedSubscriber());

        streamPluginSettingsContainer.add(availablePluginsContainer);
        streamPluginSettingsContainer.add(feedSubscriptionsContainer);

        panel.add(streamPluginSettingsContainer);

    }

    /**
     * Get all available plugins and feed subscriptions.
     */
    public void getPluginsAndFeedSubscriptions()
    {
        ((Fetchable<String>) model).fetch(Session.getInstance().getUrlViews().get(
                Session.getInstance().getUrlViews().size() - 1), true);
    }

    /**
     * Get the metadata for the plugin.
     *
     * @param plugin
     *            the plugin.
     * @return the metadata.
     */
    private GadgetMetaDataDTO getMetaDataForPlugin(final PluginDefinition plugin)
    {
        for (GadgetMetaDataDTO metadataItem : metadata)
        {
            if (metadataItem.getGadgetDefinition().getId() == plugin.getId())
            {
                return metadataItem;
            }

        }

        return null;
    }

    /**
     * Renders the plugins.
     */
    public void renderPlugins()
    {
        availablePluginsContainer.clear();
        Label availablePluginsHeader = new Label("Available Plugins");
        availablePluginsHeader.addStyleName("header");
        availablePluginsContainer.add(availablePluginsHeader);

        if (availablePlugins.size() > 0)
        {
            availablePluginsMetaData.clear();
            for (PluginDefinition availablePlugin : availablePlugins)
            {
                availablePluginsMetaData.add(getMetaDataForPlugin(availablePlugin));
            }

            sortPluginsMetaData();

            for (final GadgetMetaDataDTO metaDataItem : availablePluginsMetaData)
            {
                FlowPanel filterPanel = new FlowPanel();
                filterPanel.addStyleName("filter");

                FlowPanel listItemPanel = new FlowPanel();
                listItemPanel.addStyleName("stream-list-item");

                FlowPanel labelContainer = new FlowPanel();
                labelContainer.addStyleName("filter-label");
                Label pluginTitle = new Label(metaDataItem.getTitle());

                pluginTitle.addClickHandler(new ClickHandler()
                {
                    public void onClick(final ClickEvent event)
                    {
                        // clear the notification in case there was one left over from adding a prior plugin
                        Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());

                        selectPlugin(metaDataItem);
                    }
                });

                labelContainer.add(pluginTitle);

                listItemPanel.add(new Image(metaDataItem.getThumbnail()));
                listItemPanel.add(labelContainer);

                filterPanel.add(listItemPanel);

                availablePluginsContainer.add(filterPanel);

                availablePluginsById.put(metaDataItem.getGadgetDefinition().getId(), filterPanel);
            }
        }
        else
        {
            availablePluginsContainer.add(new Label("No plugins available"));
        }
    }

    /**
     * Sorts the meta data for the plugins alphabetically.
     */
    private void sortPluginsMetaData()
    {
        for (int i = availablePluginsMetaData.size() - 1; i > 0; i--)
        {
            for (int j = 0; j < i; j++)
            {
                GadgetMetaDataDTO currentPlugin = availablePluginsMetaData.get(j);
                GadgetMetaDataDTO nextPlugin = availablePluginsMetaData.get(j + 1);
                if (currentPlugin.getTitle().compareToIgnoreCase(nextPlugin.getTitle()) > 0)
                {
                    availablePluginsMetaData.set(j, nextPlugin);
                    availablePluginsMetaData.set(j + 1, currentPlugin);
                }
            }
        }
    }

    /**
     * Renders the feed subscriptions.
     */
    public void renderFeedSubscriptions()
    {
        feedSubscriptionsContainer.clear();

        Label feedSubscriptionsHeader = new Label("My Plugins");
        feedSubscriptionsHeader.addStyleName("header");
        feedSubscriptionsContainer.add(feedSubscriptionsHeader);

        if (feedSubscriptions.size() > 0)
        {
            Label feedDescription = new Label("Click on any of the plugins on the left to add them to your stream.");
            feedDescription.addStyleName("description");
            feedSubscriptionsContainer.add(feedDescription);

            int count = 0;
            int size = feedSubscriptions.size();
            // for loop start
            for (final FeedSubscriber feedSubscription : feedSubscriptions)
            {
                if (feedSubscription.getFeed() != null)
                {
                    final GadgetMetaDataDTO metaDataItem = getMetaDataForPlugin(feedSubscription.getFeed().getPlugin());
                    FlowPanel feedContainer = new FlowPanel();
                    feedContainer.addStyleName("stream-plugins-meta-data");

                    if (count == size - 1)
                    {
                        feedContainer.addStyleName("last");
                    }
                    count++;

                    FlowPanel imageContainer = new FlowPanel();
                    imageContainer.addStyleName("image-container");

                    FlowPanel screenshot = new FlowPanel();
                    screenshot.addStyleName("stream-plugins-screenshot");

                    screenshot.add(new Image(metaDataItem.getScreenshot()));

                    imageContainer.add(screenshot);

                    FlowPanel dataContainer = new FlowPanel();
                    dataContainer.setStyleName("gadget-data");

                    EditPanel editControls = new EditPanel(dataContainer, Mode.EDIT_AND_DELETE);

                    editControls.addEditClickHandler(new ClickHandler()
                    {
                        public void onClick(final ClickEvent event)
                        {
                            editFeedSubscription(feedSubscription, metaDataItem);
                        }
                    });

                    editControls.addDeleteClickHandler(new ClickHandler()
                    {
                        public void onClick(final ClickEvent arg0)
                        {
                            if (jSNIFacade.confirm("Are you sure you want to unsubscribe to this feed?"))
                            {
                                ((Deletable<DeleteFeedSubscriptionRequest>) model)
                                        .delete(new DeleteFeedSubscriptionRequest(feedSubscription.getId(), Session
                                                .getInstance().getUrlViews().get(
                                                        Session.getInstance().getUrlViews().size() - 1)));

                            }
                        }
                    });

                    dataContainer.add(editControls);

                    Label title = new Label(feedSubscription.getFeed().getTitle());
                    title.addStyleName("title");

                    FlowPanel sourcePanel = new FlowPanel();
                    sourcePanel.addStyleName("gadget-ext-info");
                    sourcePanel.add(new Label("Source: "));
                    sourcePanel.add(new Anchor(feedSubscription.getFeed().getUrl(),
                            feedSubscription.getFeed().getUrl(), "_new"));

                    dataContainer.add(title);
                    dataContainer.add(sourcePanel);

                    FlowPanel lastUpdatedPanel = new FlowPanel();
                    lastUpdatedPanel.addStyleName("gadget-ext-info");

                    if (feedSubscription.getFeed().getTimeAgo() != null)
                    {
                        lastUpdatedPanel.add(new InlineLabel("Last Updated: "));
                        Label lastUpdateTime = new InlineLabel(feedSubscription.getFeed().getTimeAgo());
                        lastUpdateTime.addStyleName("light");
                        lastUpdatedPanel.add(lastUpdateTime);
                    }
                    if (feedSubscription.getFeed().getIsFeedBroken())
                    {
                        Label brokenFeedIndicator = new InlineLabel("Feed may be broken, please check the source.");
                        brokenFeedIndicator.addStyleName("broken-feed-indicator");
                        lastUpdatedPanel.add(brokenFeedIndicator);
                    }
                    if (lastUpdatedPanel.getWidgetCount() > 0)
                    {
                        dataContainer.add(lastUpdatedPanel);
                    }

                    feedContainer.add(imageContainer);
                    feedContainer.add(dataContainer);

                    feedSubscriptionsContainer.add(feedContainer);
                }
            }

            // for loop end
        }
        else
        {
            Label feedDescription = new Label("No plugins configured. "
                    + "Select an available plugin to publish activity to the stream.");
            feedDescription.setStyleName("description");
            feedSubscriptionsContainer.add(feedDescription);
        }
    }

    /**
     * Selects a plugin from the available plugins, allowing the user to add a new one.
     *
     * @param selectedMetaData
     *            MetaData of the selected plugin
     */
    public void selectPlugin(final GadgetMetaDataDTO selectedMetaData)
    {
        for (FlowPanel pluginPanel : availablePluginsById.values())
        {
            pluginPanel.removeStyleName("active");
        }

        availablePluginsById.get(selectedMetaData.getGadgetDefinition().getId()).addStyleName("active");
        feedSubscriptionsContainer.setVisible(false);

        streamPluginSettingsContainer.clear();
        streamPluginSettingsContainer.add(availablePluginsContainer);

        EditFeedSubscriptionPanel addFeedSubscriptionPanel = new EditFeedSubscriptionPanel(selectedMetaData,
                Method.INSERT);
        streamPluginSettingsContainer.add(addFeedSubscriptionPanel);

        addFeedSubscriptionPanel.add(new StreamPluginConfigurationPanel(selectedMetaData.getGadgetDefinition(), model,
                selectedMetaData));

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
        feedSubscriptionsContainer.setVisible(false);

        streamPluginSettingsContainer.clear();
        streamPluginSettingsContainer.add(availablePluginsContainer);

        EditFeedSubscriptionPanel editFeedSubscriptionPanel = new EditFeedSubscriptionPanel(selectedMetaData,
                Method.UPDATE);
        streamPluginSettingsContainer.add(editFeedSubscriptionPanel);

        editFeedSubscriptionPanel.add(new StreamPluginConfigurationPanel(feedSubscription, model, selectedMetaData));

    }
}
