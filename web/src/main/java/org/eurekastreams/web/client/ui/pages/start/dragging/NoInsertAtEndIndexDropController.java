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

import com.allen_sauer.gwt.dnd.client.drop.IndexedDropController;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An Indexed Drop Controller that doesn't allow you to insert after the last
 * item. This is useful for when we have a spacer object at the end of a list so
 * the list doesn't collapse, like in Gadgets.
 */
public class NoInsertAtEndIndexDropController extends IndexedDropController
{
    /**
     * The drop target.
     */
    private IndexedPanel dropTarget;

    /**
     * Default constructor.
     *
     * @param inDropTarget
     *            the drop target.
     */
    public NoInsertAtEndIndexDropController(final IndexedPanel inDropTarget)
    {
        super(inDropTarget);
        this.dropTarget = inDropTarget;
    }

    /**
     * Don't let it insert after the end.
     *
     * @param widget
     *            the widget to insert.
     * @param inBeforeIndex
     *            the index to insert it before.
     */
    @Override
    protected void insert(final Widget widget, final int inBeforeIndex)
    {
        int beforeIndex = inBeforeIndex;
        if (beforeIndex == dropTarget.getWidgetCount())
        {
            beforeIndex--;
        }
        super.insert(widget, beforeIndex);
    }

}
