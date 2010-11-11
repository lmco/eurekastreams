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
package org.eurekastreams.web.client.ui.pages.start;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.action.request.start.SetGadgetStateRequest.State;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.web.client.events.GadgetAddedToStartPageEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StartPageTabReadyEvent;
import org.eurekastreams.web.client.events.data.DeletedGadgetResponseEvent;
import org.eurekastreams.web.client.events.data.ReorderedGadgetResponseEvent;
import org.eurekastreams.web.client.events.data.UnDeletedGadgetResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedGadgetMinimizedStateResponseEvent;
import org.eurekastreams.web.client.jsni.GadgetMetaDataFetcher;
import org.eurekastreams.web.client.jsni.GadgetRenderer;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.start.dragging.GadgetDragController;
import org.eurekastreams.web.client.ui.pages.start.dragging.GadgetDragHandler;
import org.eurekastreams.web.client.ui.pages.start.dragging.NoInsertAtEndIndexDropController;
import org.eurekastreams.web.client.ui.pages.start.dragging.TabSelectingDropController;
import org.eurekastreams.web.client.ui.pages.start.layouts.DropZonePanel;
import org.eurekastreams.web.client.ui.pages.start.layouts.LayoutPanelStrategy;
import org.eurekastreams.web.client.ui.pages.start.layouts.OneColumnLayoutStrategy;
import org.eurekastreams.web.client.ui.pages.start.layouts.TabLayoutSelectorPanel;
import org.eurekastreams.web.client.ui.pages.start.layouts.ThreeColumnLayoutStrategy;
import org.eurekastreams.web.client.ui.pages.start.layouts.ThreeColumnLeftWideHeaderLayoutStrategy;
import org.eurekastreams.web.client.ui.pages.start.layouts.ThreeColumnRightWideHeaderLayoutStrategy;
import org.eurekastreams.web.client.ui.pages.start.layouts.TwoColumnLayoutStrategy;
import org.eurekastreams.web.client.ui.pages.start.layouts.TwoColumnLeftWideLayoutStrategy;
import org.eurekastreams.web.client.ui.pages.start.layouts.TwoColumnRightWideLayoutStrategy;
import org.eurekastreams.web.client.ui.pages.start.preferences.PortalPreferencePanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.IndexedDropController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The tab content for a start page tab. Lays down all the drop zones and gadgets.
 *
 */
public class StartPageTabContent extends FlowPanel
{
    /**
     * The preference panel.
     */
    private PortalPreferencePanel prefPanel = new PortalPreferencePanel();

    /**
     * Are we rendered?
     */
    private boolean rendered = false;
    /**
     * This panel is unfortunately needed at the bottom of the layout to keep the footer from floating up.
     */
    private FlowPanel breakPanel = new FlowPanel();

    /**
     * the list of gadgets to render.
     */
    private List<Gadget> gadgets;
    /**
     * The list of drop zones to render to.
     */
    private List<DropZonePanel> dropZones = new LinkedList<DropZonePanel>();

    /**
     * The drop zone columns.
     */
    private List<DropZonePanel> columns = new LinkedList<DropZonePanel>();

    /**
     * The default drag sensitivity.
     */
    private static final int DRAG_SENSITIVITY = 10;

    /**
     * The gadget drag controller.
     */
    private PickupDragController gadgetDragController;
    /**
    *
    */
    private GadgetDragHandler gadgetDragHandler;
    /**
     * Absolute panel is needed because gwt-dnd only accepts them.
     */
    private AbsolutePanel layoutContainer = new AbsolutePanel();

    /**
     * Default constructor.
     *
     * @param inTab
     *            the tab.
     */
    public StartPageTabContent(final Tab inTab)
    {
        layoutContainer.addStyleName("start-tab-content-container");
        prefPanel.setPreferenceWidget(new TabLayoutSelectorPanel(inTab.getId(), inTab.getTabLayout()));
        this.add(prefPanel);
        prefPanel.hidePanel();

        renderGadgetContainer(inTab);

        Session.getInstance().getEventBus().addObservers(new Observer()
        {
            public void update(final Object arg1)
            {
                resetEmptyZones();
            }
        }, ReorderedGadgetResponseEvent.class, UnDeletedGadgetResponseEvent.class, GadgetAddedToStartPageEvent.class,
                DeletedGadgetResponseEvent.class, UpdatedGadgetMinimizedStateResponseEvent.class);

        Session.getInstance().getEventBus().addObserver(StartPageTabReadyEvent.class,
                new Observer<StartPageTabReadyEvent>()
                {
                    public void update(final StartPageTabReadyEvent event)
                    {
                        gadgetDragController.registerDropController(new TabSelectingDropController(event.getTab()));
                    }
                });
    }

    /**
     * Render the gadget container. If a new tab is put in here the tab will rerender everything.
     *
     * @param inTab
     *            the tab.
     */
    public void renderGadgetContainer(final Tab inTab)
    {
        rendered = false;
        gadgetDragHandler = new GadgetDragHandler(inTab.getId());
        gadgetDragController = new GadgetDragController(RootPanel.get(), false);
        gadgetDragController.setBehaviorConstrainedToBoundaryPanel(false);
        gadgetDragController.setBehaviorMultipleSelection(false);
        gadgetDragController.setBehaviorDragStartSensitivity(DRAG_SENSITIVITY);
        gadgetDragController.addDragHandler(gadgetDragHandler);
        gadgetDragController.setBehaviorDragProxy(true);

        layoutContainer.clear();
        gadgets = inTab.getGadgets();
        LayoutPanelStrategy strategy = getStrategy(inTab.getTabLayout());
        dropZones = strategy.getDropZones();
        columns = strategy.getColumns();

        for (DropZonePanel column : columns)
        {
            layoutContainer.add(column);
        }

        for (Gadget gadget : gadgets)
        {
            insertGadget(gadget, false);
        }

        for (DropZonePanel dropZone : dropZones)
        {
            dropZone.setSpacer(new FlowPanel());

            IndexedDropController gadgetDropController = new NoInsertAtEndIndexDropController(dropZone);
            gadgetDragController.registerDropController(gadgetDropController);
        }

        this.add(layoutContainer);

        // The break panel is needed for CSS lameness so the footer doesn't
        // float up.
        breakPanel.addStyleName("break");
        layoutContainer.add(breakPanel);
        resetEmptyZones();

    }

    /**
     * Reset empty zones.
     */
    public void resetEmptyZones()
    {
        for (DropZonePanel dropZone : dropZones)
        {

            if (dropZone.getVisibleGadgetCount() == 0)
            {
                dropZone.addStyleName("empty-zone");
            }
            else
            {
                dropZone.setVisible(true);
                dropZone.removeStyleName("empty-zone");
            }
        }
    }

    /**
     * Insert a gadget.
     *
     * @param gadget
     *            the gadget.
     * @param render
     *            whether or not to render it now with shindig.
     */
    public void insertGadget(final Gadget gadget, final boolean render)
    {
        insertGadgetPanel(new GadgetPanel(gadget), gadget.getZoneNumber(), gadget.getZoneIndex());

        if (render)
        {
            GadgetRenderer.getInstance().renderGadget(Long.toString(gadget.getId()));
            if (gadget.getGadgetUserPref() != null && !gadget.getGadgetUserPref().equals(""))
            {
                saveUserPrefs(gadget.getId());
            }
        }
    }

    /**
     * Insert a gadget panel.
     *
     * @param gadgetPanel
     *            the gadget panel.
     * @param zoneNumber
     *            the zone.
     * @param zoneIndex
     *            the index.
     */
    public void insertGadgetPanel(final GadgetPanel gadgetPanel, final int zoneNumber, final int zoneIndex)
    {
        gadgetPanel.makeGadgetDraggable(gadgetDragController);
        dropZones.get(zoneNumber).insertGadget(gadgetPanel, zoneIndex);

        for (DropZonePanel dropZone : dropZones)
        {
            for (GadgetPanel anyGadget : dropZone.getGadgetZones())
            {
                if (anyGadget.getGadgetState().equals(State.MAXIMIZED))
                {
                    gadgetPanel.setVisible(false);
                }
            }
        }
    }

    /**
     * Are any of the gadgets maximized?
     * @return the value.
     */
    public boolean isAnyGadgetMaximized()
    {
        for (DropZonePanel dropZone : dropZones)
        {
            for (GadgetPanel anyGadget : dropZone.getGadgetZones())
            {
                if (anyGadget.getGadgetState().equals(State.MAXIMIZED))
                {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Save the user prefs.
     *
     * @param id
     *            the id of the gadget.
     */
    private static native void saveUserPrefs(final Long id)
    /*-{
           var gadget = $wnd.shindig.container.getGadget(id);
           gadget.setUserPrefs(gadget.userPrefs_);
    }-*/;

    /**
     * Show the tab layout selector.
     */
    public void showTabLayoutSelector()
    {
        prefPanel.showPanel();
    }

    /**
     * Hide the tab layout selector.
     */
    public void hideTabLayoutSelector()
    {
        prefPanel.hidePanel();
    }

    /**
     * Render the gadgets. Has to be done after everything else.
     */
    public void renderGadgets()
    {

        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                if (!rendered)
                {
                    for (Gadget gadget : gadgets)
                    {
                        if (!gadget.isMinimized())
                        {
                            GadgetRenderer.getInstance().renderGadget(Long.toString(gadget.getId()));
                        }
                    }

                    for (DropZonePanel dropZone : dropZones)
                    {
                        for (GadgetPanel gadgetPanel : dropZone.getGadgetZones())
                        {
                            if (gadgetPanel.getGadgetData().isMaximized() != null
                                    && gadgetPanel.getGadgetData().isMaximized())
                            {
                                gadgetPanel.setGadgetState(State.MAXIMIZED);
                            }
                        }
                    }
                }
                rendered = true;
            }
        });

    }

    /**
     * Refresh metadata for all gadgets on this tab.
     */
    public void refreshGadgetMetadata()
    {
        if (gadgets != null && gadgets.size() > 0)
        {
            List<GadgetDefinition> gadgetDefList = new ArrayList<GadgetDefinition>();
            for (Gadget gadget : gadgets)
            {
                gadgetDefList.add(gadget.getGadgetDefinition());
            }
            (new GadgetMetaDataFetcher(gadgetDefList)).fetchMetaData();
        }
    }

    /**
     * Identify the LayoutPanelStrategy that works for the layout.
     *
     * @param layout
     *            the layout to get a strategy for.
     * @return the strategy.
     */
    private LayoutPanelStrategy getStrategy(final Layout layout)
    {
        switch (layout)
        {
        case ONECOLUMN:
            return new OneColumnLayoutStrategy();
        case TWOCOLUMN:
            return new TwoColumnLayoutStrategy();
        case TWOCOLUMNLEFTWIDE:
            return new TwoColumnLeftWideLayoutStrategy();
        case TWOCOLUMNRIGHTWIDE:
            return new TwoColumnRightWideLayoutStrategy();
        case THREECOLUMNLEFTWIDEHEADER:
            return new ThreeColumnLeftWideHeaderLayoutStrategy();
        case THREECOLUMNRIGHTWIDEHEADER:
            return new ThreeColumnRightWideHeaderLayoutStrategy();
        case THREECOLUMN:
            return new ThreeColumnLayoutStrategy();
        default:
            return new OneColumnLayoutStrategy();
        }
    }
}
