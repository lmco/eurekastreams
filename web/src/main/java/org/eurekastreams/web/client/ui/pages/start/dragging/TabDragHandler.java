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

import org.eurekastreams.server.action.request.start.SetTabOrderRequest;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.web.client.model.StartTabsModel;
import org.eurekastreams.web.client.ui.pages.start.StartPageTab;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * This is the tab drag handler for the tab drag and drop.
 */
public class TabDragHandler implements DragHandler
{

    /**
     * Default constructor.
     *
     */
    public TabDragHandler()
    {
    }

    /**
     * What happens when we're done drag and dropping.
     *
     * @param event
     *            the event to fire on drag end.
     */
    public void onDragEnd(final DragEndEvent event)
    {
        StartPageTab tab = (StartPageTab) event.getContext().draggable;
        HorizontalPanel dropPanel = (HorizontalPanel) event.getContext().finalDropController.getDropTarget();

        StartTabsModel.getInstance().reorder(
                new SetTabOrderRequest(TabGroupType.START, tab.getTab().getId(), new Integer(dropPanel
                        .getWidgetIndex(tab))));
    }

    /**
     * What happens when we start dragging (nothing).
     *
     * @param event
     *            the event to fire on drag start.
     */
    public void onDragStart(final DragStartEvent event)
    {
    }

    /**
     * What happens right before we stop dragging (nothing).
     *
     * @param event
     *            the event to fire right before dragging ends.
     * @throws VetoDragException
     *             cancels ending drag.
     */
    public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException
    {
    }

    /**
     * What happens right before we start dragging (nothing).
     *
     * @param event
     *            the event to fire right before dragging starts.
     * @throws VetoDragException
     *             cancels starting drag.
     */
    public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException
    {
    }
}
