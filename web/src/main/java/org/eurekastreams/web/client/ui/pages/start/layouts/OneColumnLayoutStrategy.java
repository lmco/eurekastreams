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
package org.eurekastreams.web.client.ui.pages.start.layouts;

import java.util.LinkedList;
import java.util.List;


/**
 * Creates a 1 row layout.
 */
public class OneColumnLayoutStrategy implements LayoutPanelStrategy
{
    /**
     * List of drop zones.
     */
    List<DropZonePanel> dropZones = new LinkedList<DropZonePanel>();

    /**
     * Public constructor.
     */
    public OneColumnLayoutStrategy()
    {
        dropZones.add(DropZonePanel.getFullRowDropZone(0));
    }

    /**
     * Gets columns to add to the panel.
     *
     * @return the columns.
     */
    public List<DropZonePanel> getColumns()
    {
        return dropZones;
    }

    /**
     * Gets drop zones to add gadgets too.
     *
     * @return the drop zones.
     */
    public List<DropZonePanel> getDropZones()
    {
        return dropZones;
    }
}
