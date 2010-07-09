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
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.ui.pages.start.GadgetPanel;
import org.eurekastreams.web.client.ui.pages.start.StartPageTab;
import org.eurekastreams.web.client.ui.pages.start.StartPageTabContent;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;

/**
 * The tab selecting drop controller.
 */
public class TabSelectingDropController extends AbstractDropController
{
    /**
     * The tab.
     */
    private StartPageTab tab;
    /**
     * Constructor.
     *
     * @param inTabWidgetDropTarget
     *            the drop target.
     */
    public TabSelectingDropController(final StartPageTab inTabWidgetDropTarget)
    {
        super(inTabWidgetDropTarget);
        tab = inTabWidgetDropTarget;
    }

    /**
     * On Drop.
     *
     * @param context
     *            used to get the draggable and drop target.
     */
    @Override
    public void onDrop(final DragContext context)
    {
        GadgetPanel gadgetZone = (GadgetPanel) context.draggable;
        final StartPageTab sTab = (StartPageTab) context.finalDropController.getDropTarget();

        gadgetZone.makeGadgetUndraggable();
        gadgetZone.removeFromParent();
        ((StartPageTabContent) sTab.getContents()).insertGadgetPanel(gadgetZone, 0, 0);

        GadgetModel.getInstance().reorder(new ReorderGadgetRequest(sTab.getTab().getId(), new Long(gadgetZone
                .getGadgetData().getId()), 0, 0));
    }

    /**
     * Call on enter.
     *
     * @param context
     *            add style to tab.
     */
    @Override
    public void onEnter(final DragContext context)
    {
        tab.addStyleName("dropping");
    }

    /**
     * Call on leave.
     * @param context the context.
     */
    @Override
    public void onLeave(final DragContext context)
    {
        super.onLeave(context);
        tab.removeStyleName("dropping");
    }

}
