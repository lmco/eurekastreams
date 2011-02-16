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
package org.eurekastreams.web.client.ui.pages.start.dragging;

import org.eurekastreams.server.action.request.start.ReorderGadgetRequest;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.start.GadgetPanel;
import org.eurekastreams.web.client.ui.pages.start.layouts.DropZonePanel;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;

/**
 * The gadget drag handler.
 */
public class GadgetDragHandler implements DragHandler
{
    /**
     * The current tab.
     */
    private Long tabId;

    /**
     * Default constructor.
     *
     * @param inTabId
     *            the tab id.
     */
    public GadgetDragHandler(final Long inTabId)
    {
        tabId = inTabId;

        Session.getInstance().getEventBus()
                .addObserver(UpdatedHistoryParametersEvent.class, new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        final String tabName = event.getParameters().get("tab");
                        if (tabName != null)
                        {
                            tabId = Long.valueOf(tabName);
                        }
                    }
                }, true);
    }

    /**
     * What happens when we're done drag and dropping.
     *
     * @param event
     *            the event that fires when dragging ends.
     */
    public void onDragEnd(final DragEndEvent event)
    {

        final GadgetPanel gadgetZone = (GadgetPanel) event.getContext().draggable;
        final DropZonePanel dropPanel = (DropZonePanel) event.getContext().finalDropController.getDropTarget();

        gadgetZone.setDropZone(dropPanel);

        GadgetModel.getInstance().reorder(
                new ReorderGadgetRequest(tabId, new Long(gadgetZone.getGadgetData().getId()), dropPanel
                        .getZoneNumber(), new Integer(dropPanel.getVisibleGadgetPosition(gadgetZone))));

        gadgetZone.rerender();
    }

    /**
     * What happens when we start dragging (nothing).
     *
     * @param event
     *            the event that fires when dragging starts.
     */
    public void onDragStart(final DragStartEvent event)
    {
    }

    /**
     * What happens right before we stop dragging (nothing).
     *
     * @param event
     *            the event that fires right before dragging ends.
     * @throws VetoDragException
     *             thrown to "veto" ending the drag.
     */
    public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException
    {

    }

    /**
     * What happens right before we start dragging. Int his case, we want to widen the spacer widget from 1px to 30 px
     * to make it easier to drop into empty columns.
     *
     * @param event
     *            the event that fires right before dragging starts.
     * @throws VetoDragException
     *             thrown to "veto" starting the drag.
     */
    public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException
    {
    }

}
