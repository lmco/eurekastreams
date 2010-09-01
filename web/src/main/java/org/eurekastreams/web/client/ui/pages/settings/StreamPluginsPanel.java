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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.action.request.gallery.GetGalleryItemsRequest;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.web.client.events.GotGadgetMetaDataEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.DeletedPluginDefinitionResponseEvent;
import org.eurekastreams.web.client.events.data.GotPluginDefinitionModelResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPluginDefinitionResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.GadgetMetaDataFetcher;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.PluginDefinitionModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.PluginMetaDataRenderer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Stream plugins panel.
 */
public class StreamPluginsPanel extends FlowPanel
{

    /**
     * JSNI Facade.
     */
    WidgetJSNIFacadeImpl jsniFacade = new WidgetJSNIFacadeImpl();

    /**
     * The panel.
     */
    private FlowPanel panel = new FlowPanel();

    /**
     * Gadget tab.
     */
    private PagedListPanel pluginTab = null;

    /**
     * Gadget from index.
     */
    private int pluginsFrom = 0;
    /**
     * Gadget to index.
     */
    private int pluginsTo = 0;
    /**
     * Gadget total number.
     */
    private int pluginsTotal = 0;

    /**
     * Add Gadget button.
     */
    private Hyperlink addPlugin;

    /**
     * Container for the gallery tabs.
     */
    private FlowPanel galleryPortalContainer = new FlowPanel();
    /**
     * Container for the add/edit panels.
     */
    private FlowPanel galleryAddOrEditContainer = new FlowPanel();

    /**
     * Stream plugins panel.
     */
    public StreamPluginsPanel()
    {
        this.clear();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("tab", "Plugins");
        params.put("action", "newPlugin");
        addPlugin =  new Hyperlink("Add Plugin", Session.getInstance().generateUrl(
                new CreateUrlRequest(params)));

        RootPanel.get().addStyleName("gallery");
        panel.addStyleName("gallery-master");
        panel.addStyleName("plugins-master");
        galleryAddOrEditContainer.addStyleName("settings-tab");
        this.add(panel);
        addPlugin.addStyleName("add-gadget");

        panel.add(galleryPortalContainer);
        panel.add(galleryAddOrEditContainer);

        galleryPortalContainer.add(addPlugin);

        // Calling this gadgets so it works with a GadgetMetaDataRenderer. Do NOT change to plugins.
        pluginTab = new PagedListPanel("gadgets");
        galleryPortalContainer.add(pluginTab);

        pluginTab.addSet("All", PluginDefinitionModel.getInstance(), new PluginMetaDataRenderer(),
                new GetGalleryItemsRequest("recent", "", 0, 0));
        pluginTab.addSet("Internet Services", PluginDefinitionModel.getInstance(), new PluginMetaDataRenderer(),
                new GetGalleryItemsRequest("recent", "Internet Services", 0, 0));

        Session.getInstance().getEventBus().addObserver(GotGadgetMetaDataEvent.class,
                new Observer<GotGadgetMetaDataEvent>()
                {
                    public void update(final GotGadgetMetaDataEvent event)
                    {
                        pluginTab.render(new PagedSet<GadgetMetaDataDTO>(pluginsFrom, pluginsTo, pluginsTotal, event
                                .getMetadata()), "There are no plugins in this category.");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotPluginDefinitionModelResponseEvent.class,
                new Observer<GotPluginDefinitionModelResponseEvent>()
                {
                    public void update(final GotPluginDefinitionModelResponseEvent event)
                    {
                        pluginsFrom = event.getResponse().getFromIndex();
                        pluginsTo = event.getResponse().getToIndex();
                        pluginsTotal = event.getResponse().getTotal();

                        if (pluginsTotal == 0)
                        {
                            Session.getInstance().getEventBus().notifyObservers(
                                    new GotGadgetMetaDataEvent(new LinkedList<GadgetMetaDataDTO>()));
                        }
                        else
                        {
                            GadgetMetaDataFetcher fetcher = // \n
                            new GadgetMetaDataFetcher(event.getResponse().getPagedSet());
                            fetcher.fetchMetaData();
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(InsertedPluginDefinitionResponseEvent.class,
                new Observer<InsertedPluginDefinitionResponseEvent>()
                {
                    public void update(final InsertedPluginDefinitionResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.SETTINGS, "tab", "Plugins")));
                        pluginTab.reload();
                        Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                new Notification("Stream Plugin has been successfully added")));
                    }
                });


        Session.getInstance().getEventBus().addObserver(DeletedPluginDefinitionResponseEvent.class,
                new Observer<DeletedPluginDefinitionResponseEvent>()
                {
                    public void update(final DeletedPluginDefinitionResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                new Notification("Stream Plugin has been successfully deleted")));
                    }
                });

        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {

                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (event.getParameters().get("action").equals("newPlugin"))
                        {
                            galleryPortalContainer.setVisible(false);
                            galleryAddOrEditContainer.setVisible(true);
                            galleryAddOrEditContainer.clear();

                            String defaultCategory = null;
                            String defaultUrl = "";
                            String id = "";

                            FormBuilder form = new FormBuilder("Submit a Plugin",
                                    PluginDefinitionModel.getInstance(), Method.INSERT);
			    form.turnOffChangeCheck();

                            form.setOnCancelHistoryToken(Session.getInstance().generateUrl(
                                    new CreateUrlRequest(Page.SETTINGS, "tab", "Plugins")));
                            form.addFormElement(new ValueOnlyFormElement("id", id));
                            form
                                    .addWidget(new HTML(
                                "<span class='gallery-upload-note'><strong>Please Note:</strong><br />"
                                + "Please be sure your XML file includes the required fields. You will not be able to "
                                + "upload the XML without the required fields.</span>"));
                            form.addFormDivider();

                            List<String> categories = new LinkedList<String>();
                            categories.add("Internet Services");
                            form.addFormElement(new BasicDropDownFormElement("Category", "category", categories,
                                    defaultCategory, "", true));

                            form.addFormDivider();

                            form.addFormElement(new BasicTextBoxFormElement("Plugin XML:", "url", defaultUrl,
                                    "Enter the link to the xml file", true));

                            form.addFormDivider();

                            galleryAddOrEditContainer.add(form);

                        }
                        else
                        {
                            galleryAddOrEditContainer.setVisible(false);
                            galleryPortalContainer.setVisible(true);
                            addPlugin.setVisible(true);
                        }
                    }

                }, true);

    }
}
