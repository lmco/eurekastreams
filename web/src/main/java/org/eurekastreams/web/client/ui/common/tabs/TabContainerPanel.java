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
package org.eurekastreams.web.client.ui.common.tabs;

import java.util.HashMap;

import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.ui.Session;

import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IndexedPanel;

/**
 * Manages tabs. Stores the state in the history. Supports dragging and droppin.
 * 
 */
public class TabContainerPanel extends AbsolutePanel
{
    /**
     * The tab contents.
     */
    FlowPanel tabContents = new FlowPanel();
    /**
     * The dictionary of tabs.
     */
    HashMap<String, SimpleTab> tabs = new HashMap<String, SimpleTab>();
    /**
     * The active tab.
     */
    SimpleTab activeTab = null;
    /**
     * The first tab. Dictionaries aren't indexed.
     */
    String firstTab = null;

    /**
     * The tab drop zone.
     */
    HorizontalPanel tabDropZone = new HorizontalPanel();
    /**
     * The tab boundary panel. Absolute panel is needed because gwt-dnd only accepts them.
     */
    AbsolutePanel tabBoundaryPanel = new AbsolutePanel();

    /**
     * The tab drag controller.
     */
    private PickupDragController tabDragController;
    /**
     * The default drag sensitivity.
     */
    private static final int DRAG_SENSITIVITY = 10;

    /**
     * Are we draggable?
     */
    private boolean draggable = false;

    /**
     * The history token key. Default to "tab".
     */
    private String key;

    /**
     * Default constructor.
     */
    public TabContainerPanel()
    {
        this("tab");
    }

    /**
     * Constructor specifiying the key, or the history token key associated with this tab container.
     * 
     * @param inKey
     *            the history token key.
     */
    public TabContainerPanel(final String inKey)
    {
        key = inKey;
        tabBoundaryPanel.setWidth("100%");
        tabBoundaryPanel.add(tabDropZone);
        tabBoundaryPanel.addStyleName("tab-container");
        this.addStyleName("tab-container-parent");

        this.add(tabBoundaryPanel);
        this.add(tabContents);
    }

    /**
     * init *MUST* be called after all the tabs are added. This can not be done in the constructor because it looks into
     * the history to see which tab to select. This obviously can't be done until we have all the tabs.
     */
    public void init()
    {
        if (null != firstTab)
        {
            activateTab(firstTab);
        }

        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (null != event.getParameters().get(key))
                        {
                            switchToTab(event.getParameters().get(key));
                        }
                        else
                        {
                            switchToTab(firstTab);
                        }
                    }
                }, true);
    }

    /**
     * Make the tabs draggable. Just give me a draghandler so I know what to do when its over.
     * 
     * @param dragHandler
     *            the draghandler.
     * @param dropController
     *            the drop controller.
     */
    public void makeTabsDraggable(final DragHandler dragHandler, final DropController dropController)
    {
        draggable = true;
        tabDragController = new PickupDragController(tabBoundaryPanel, false);
        tabDragController.setBehaviorConstrainedToBoundaryPanel(true);
        tabDragController.setBehaviorMultipleSelection(false);
        tabDragController.setBehaviorDragStartSensitivity(DRAG_SENSITIVITY);
        tabDragController.addDragHandler(dragHandler);
        tabDragController.registerDropController(dropController);
    }

    /**
     * Get the tab drop zone. May be necessary for making drop controllers.
     * 
     * @return the tab drop zone.
     */
    public IndexedPanel getTabDropZone()
    {
        return tabDropZone;
    }

    /**
     * Switch to a tab by it's unique identifier.
     * 
     * @param identifier
     *            the tab's identifier.
     */
    public void switchToTab(final String identifier)
    {
        if (activeTab == null || !activeTab.getIdentifier().equals(identifier))
        {
            if (tabs.containsKey(identifier))
            {
                activateTab(identifier);
            }
            else if (firstTab != null)
            {
                activateTab(firstTab);
            }
        }
    }

    /**
     * Selects and activates a given tab, if possible.
     * 
     * @param tab
     *            the tab name/identifier to activate
     */
    private void activateTab(final String tab)
    {
        if (activeTab != null)
        {
            activeTab.unSelect();
        }

        activeTab = tabs.get(tab);
        activeTab.select();

        tabContents.clear();
        tabContents.add(activeTab.getContents());
    }

    /**
     * Add a tab.
     * 
     * @param tab
     *            the tab.
     */
    public void addTab(final SimpleTab tab)
    {
        insertTab(tab, tabs.size());
    }

    /**
     * Insert a tab. Like add a tab only with an index.
     * 
     * @param tab
     *            the tab.
     * @param index
     *            the index.
     */
    public void insertTab(final SimpleTab tab, final int index)
    {
        if (firstTab == null)
        {
            firstTab = tab.getIdentifier();
        }
        tabs.put(tab.getIdentifier(), tab);
        tabDropZone.insert(tab, index);

        if (draggable)
        {
            tab.makeTabDraggable(tabDragController);
        }

        tab.init(key);
    }

    /**
     * Gets the number of tabs.
     * 
     * @return the tabs.
     */
    public int getSize()
    {
        return tabs.size();
    }

    /**
     * Remove a tab by identifier.
     * 
     * @param identifier
     *            the identifier.
     */
    public void removeTab(final String identifier)
    {
        tabDropZone.remove(tabs.get(identifier));
        tabs.remove(identifier);
    }

    /**
     * Gets a tab.
     * 
     * @param index
     *            the index.
     * @return the tab.
     */
    public SimpleTab getTab(final int index)
    {
        return (SimpleTab) tabDropZone.getWidget(index);
    }
}
