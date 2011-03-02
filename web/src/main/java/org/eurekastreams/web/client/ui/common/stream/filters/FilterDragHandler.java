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
package org.eurekastreams.web.client.ui.common.stream.filters;

import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.web.client.model.Reorderable;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The drag handler for stream views.
 *
 */
public class FilterDragHandler implements DragHandler
{
    /**
     * The list panel.
     */
    private FilterListPanel listPanel;


    /**
     * The model.
     */
    private Reorderable<SetStreamOrderRequest> reorderable;

    /**
     * Default constructor.
     *
     * @param inListPanel
     *            the panel.
     * @param inReorderable
     *            the model.
     */
    public FilterDragHandler(final FilterListPanel inListPanel,
            final Reorderable<SetStreamOrderRequest> inReorderable)
    {
        reorderable = inReorderable;
        listPanel = inListPanel;
    }

    /**
     * Gets fired when the drag is finished.
     *
     * @param event
     *            the event.
     */
    public void onDragEnd(final DragEndEvent event)
    {
        listPanel.fixHiddenLine();
        FilterPanel listItem = (FilterPanel) event.getContext().draggable;
        VerticalPanel dropPanel = (VerticalPanel) event.getContext().finalDropController.getDropTarget();

        Integer hiddenLineIndex = listPanel.getHiddenLineIndex();
        Integer itemIndex = new Integer(dropPanel.getWidgetIndex((Widget) listItem));

        if (itemIndex > hiddenLineIndex)
        {
            itemIndex--;
            ((Widget) listItem).addStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
            listPanel.showTextOnHiddenLine();
        }
        else
        {
            ((Widget) listItem).removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hide());
            listPanel.hideTextOnHiddenLine(hiddenLineIndex);
        }

        SetStreamOrderRequest request = new SetStreamOrderRequest(listItem.getItemId(), itemIndex,
                hiddenLineIndex - 1);
        reorderable.reorder(request);

    }

    /**
     * This is not the method you're looking for.
     *
     * @param event
     *            the event.
     */
    public void onDragStart(final DragStartEvent event)
    {
    }

    /**
     * This is not the method you're looking for.
     *
     * @param event
     *            the event.
     * @throws VetoDragException
     *             a veto exception.
     */
    public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException
    {
    }

    /**
     * Gets fired BEFORE we drag. Right now all it does is expands the list.
     *
     * @param event
     *            the event.
     * @throws VetoDragException
     *             a veto exception.
     */
    public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException
    {
        // I'm going to leave this code block in, in case pgm mgnt changes their mind
        // what it does is it restricts the movement of the panel if it's the first
        // and only thing in list.
        // StreamViewPanel listItem = (StreamViewPanel) event.getContext().draggable;
        // VerticalPanel dropPanel = listPanel.getDropPanel();
        // Integer itemIndex = new Integer(dropPanel.getWidgetIndex(listItem));

        // if (listPanel.getHiddenLineIndex() == 1 && itemIndex == 0)
        // throw new VetoDragException();

        listPanel.unhide();
    }

}
