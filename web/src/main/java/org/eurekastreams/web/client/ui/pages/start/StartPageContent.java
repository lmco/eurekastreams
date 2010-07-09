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
package org.eurekastreams.web.client.ui.pages.start;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StartPageTabReadyEvent;
import org.eurekastreams.web.client.events.ThemeChangedEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.DeletedStartPageTabResponseEvent;
import org.eurekastreams.web.client.events.data.GotStartPageTabsResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedStartTabResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.GadgetMetaDataFetcher;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.model.StartTabsModel;
import org.eurekastreams.web.client.model.ThemeModel;
import org.eurekastreams.web.client.model.requests.AddGadgetToStartPageRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.notifier.UndoDeleteNotification;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.start.dragging.NoInsertAtEndIndexDropController;
import org.eurekastreams.web.client.ui.pages.start.dragging.TabDragHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This widget contains the widgets that make up the start page.
 */
public class StartPageContent extends FlowPanel
{
    /**
     * The tabs.
     */
    private TabContainerPanel tabs = new TabContainerPanel();

    /**
     * JSNI Facade.
     */
    private WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * The new tab button.
     */
    private StartPageTab newTab = new StartPageTab();

    /**
     * Settings button.
     */
    private Hyperlink settings = new Hyperlink("Configure", "");

    /**
     * Primary constructor for the StartPageContent widget.
     */
    public StartPageContent()
    {
        RootPanel.get().addStyleName("themeable");

        final List<StartPageTab> startTabs = new ArrayList<StartPageTab>();

        final FlowPanel thisBuffered = this;

        Session.getInstance().getEventBus().addObserver(GotStartPageTabsResponseEvent.class,
                new Observer<GotStartPageTabsResponseEvent>()
                {
                    public void update(final GotStartPageTabsResponseEvent event)
                    {
                        final List<Tab> tabsResponse = event.getResponse().getTabs(TabGroupType.START);
                        final Set<GadgetDefinition> gadgetDefs = new HashSet<GadgetDefinition>();

                        // Apply the theme.
                        if (event.getResponse().getTheme() != null)
                        {
                            jSNIFacade.setCSS(event.getResponse().getTheme().getCssFile());
                        }
                        else
                        {
                            // default theme
                            jSNIFacade.setCSS("/themes/green_hills.css");
                        }
                        // Add the banner. (the theme takes care of what it looks like).
                        Session.getInstance().getEventBus().notifyObservers(new SetBannerEvent());

                        // Make the tabs draggable. The GadgetTabContent will handle making the gadgets draggable.
                        tabs.makeTabsDraggable(new TabDragHandler(), new NoInsertAtEndIndexDropController(tabs
                                .getTabDropZone()));

                        // Loop through the tabs and add them to the container.
                        for (Tab tab : tabsResponse)
                        {
                            StartPageTab sTab = new StartPageTab(tab);
                            startTabs.add(sTab);
                            tabs.addTab(sTab);

                            for (Gadget gadget : tab.getGadgets())
                            {
                                gadgetDefs.add(gadget.getGadgetDefinition());
                            }
                        }
                        // An empty start page tab represents a new tab button.
                        newTab.setDraggable(false);

                        if (tabsResponse.size() < Person.TAB_LIMIT)
                        {
                            tabs.addTab(newTab);
                        }

                        thisBuffered.add(settings);
                        thisBuffered.add(tabs);
                        settings.addStyleName("configure-tab");

                        tabs.init();

                        if (tabs.getSize() == 2)
                        {
                            ((StartPageTab) tabs.getTab(0)).disableRemove();
                        }

                        // The start page tabs are ready. Let anyone who cares know.
                        for (StartPageTab tab : startTabs)
                        {
                            Session.getInstance().getEventBus().notifyObservers(new StartPageTabReadyEvent(tab));
                        }

                        // Fetch all the gadget def metadata in 1 request.
                        List<GadgetDefinition> gadgetDefList = new ArrayList<GadgetDefinition>();
                        for (GadgetDefinition gDef : gadgetDefs)
                        {
                            gadgetDefList.add(gDef);
                        }
                        GadgetMetaDataFetcher fetcher = new GadgetMetaDataFetcher(gadgetDefList);
                        fetcher.fetchMetaData();

                        thisBuffered.addStyleName("portal-boundary");

                        Session.getInstance().getEventBus().addObserver(ThemeChangedEvent.getEvent(),
                                new Observer<ThemeChangedEvent>()
                                {
                                    public void update(final ThemeChangedEvent arg1)
                                    {
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new UpdateHistoryEvent(new CreateUrlRequest(Page.START)));
                                        Location.reload();
                                    }
                                });

                        // Respond to history changes.
                        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                                new Observer<UpdatedHistoryParametersEvent>()
                                {
                                    public void update(final UpdatedHistoryParametersEvent event)
                                    {
                                        // Note: parameters do not need to be decoded here - that's already done by
                                        // HistoryHandler before it published the event

                                        if (event.getParameters().get("tab") != null)
                                        {
                                            HashMap<String, String> params = new HashMap<String, String>();
                                            params.put("tab", event.getParameters().get("tab"));
                                            params.put("galleryTab", "Apps");
                                            settings.setTargetHistoryToken(Session.getInstance().generateUrl(
                                                    new CreateUrlRequest(Page.GALLERY, params)));
                                        }

                                        if (event.getParameters().get("action").equals("addGadget"))
                                        {
                                            String prefs = event.getParameters().get("prefs");
                                            String url = event.getParameters().get("url");

                                            // Clear current action
                                            event.getParameters().put("action", "");
                                            event.getParameters().put("url", "");
                                            event.getParameters().put("prefs", "");
                                            EventBus.getInstance().notifyObservers(
                                                    new UpdatedHistoryParametersEvent(event.getParameters()));

                                            GadgetModel.getInstance().insert(
                                                    new AddGadgetToStartPageRequest(url, tabsResponse.get(0).getId(),
                                                            prefs));
                                        }
                                        else if (event.getParameters().get("action").equals("setTheme"))
                                        {
                                            // Clear current action
                                            EventBus.getInstance().notifyObservers(
                                                    new UpdatedHistoryParametersEvent(event.getParameters()));
                                            String url = event.getParameters().get("url");
                                            ThemeModel.getInstance().set(url);
                                        }
                                    }
                                }, true);

                        // Respond to new tabs.
                        Session.getInstance().getEventBus().addObserver(InsertedStartTabResponseEvent.class,
                                new Observer<InsertedStartTabResponseEvent>()
                                {
                                    public void update(final InsertedStartTabResponseEvent event)
                                    {
                                        Session.getInstance().getEventBus()
                                                .notifyObservers(new HideNotificationEvent());

                                        newTab.getTextBox().setVisible(false);
                                        newTab.getLabel().setVisible(true);

                                        StartPageTab tab = new StartPageTab(event.getResponse());
                                        tabs.insertTab(tab, event.getResponse().getTabIndex());

                                        // If more than 8 tabs (the new tab counts as 1) disable the new tab.
                                        if (tabs.getSize() >= Person.TAB_LIMIT + 1)
                                        {
                                            tabs.removeTab(newTab.getIdentifier());
                                        }

                                        // If 2 tabs are present (the new tab counts as 1) re-enable the remove.
                                        if (tabs.getSize() == 3)
                                        {
                                            ((StartPageTab) tabs.getTab(0)).enableRemove();
                                        }

                                        // The start page tab is ready.
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new StartPageTabReadyEvent(tab));

                                        Session.getInstance().getEventBus().notifyObservers(
                                                new UpdateHistoryEvent(new CreateUrlRequest("tab", tab.getIdentifier(),
                                                        true)));
                                    }
                                });

                        Session.getInstance().getEventBus().addObserver(DeletedStartPageTabResponseEvent.class,
                                new Observer<DeletedStartPageTabResponseEvent>()
                                {
                                    public void update(final DeletedStartPageTabResponseEvent event)
                                    {
                                        tabs.removeTab(String.valueOf(event.getResponse().getId()));
                                        tabs.addTab(newTab);
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new ShowNotificationEvent(new Notification(new UndoDeleteNotification(
                                                        event.getResponse().getTabName(), new ClickHandler()
                                                        {
                                                            public void onClick(final ClickEvent clickEvent)
                                                            {
                                                                StartTabsModel.getInstance().undoDelete(
                                                                        event.getResponse().getId());
                                                                Session.getInstance().getEventBus().notifyObservers(
                                                                        new HideNotificationEvent());
                                                            }
                                                        }), "")));

                                        // Highlight the first tab
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new UpdateHistoryEvent(new CreateUrlRequest("tab", ((StartPageTab) tabs
                                                        .getTab(0)).getIdentifier(), true)));

                                        // Only one tab left (the new tab counts as 1). Disable remove.
                                        if (tabs.getSize() == 2)
                                        {
                                            ((StartPageTab) tabs.getTab(0)).disableRemove();
                                        }
                                    }
                                });
                    }
                });

        StartTabsModel.getInstance().fetch(null, true);

    }

}
