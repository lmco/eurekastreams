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

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Creates a 3 column layout with a header in the left wide column.
 */
public class ThreeColumnLeftWideHeaderLayoutStrategy implements
        LayoutPanelStrategy
{
    /**
     * List of columns.
     */
    List<DropZonePanel> columns = new LinkedList<DropZonePanel>();
    /**
     * List of drop zones.
     */
    List<DropZonePanel> dropZones = new LinkedList<DropZonePanel>();

    /**
     * Public constructor.
     */
    public ThreeColumnLeftWideHeaderLayoutStrategy()
    {
        DropZonePanel leftColumn = DropZonePanel.getTwoThirdColumnDropZone(0);
        leftColumn.addStyleName("multi-zone");
        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.addStyleName("two-thirds-column");

        DropZonePanel headerPanel = DropZonePanel.getFullRowDropZone(0);
        headerPanel.addStyleName("header-zone");
        DropZonePanel leftPanel = DropZonePanel.getFullRowDropZone(1);
        DropZonePanel centerPanel = DropZonePanel.getFullRowDropZone(2);
        DropZonePanel rightPanel = DropZonePanel.getThirdColumnDropZone(3);

        leftColumn.add(headerPanel);
        horzPanel.add(leftPanel);
        horzPanel.add(centerPanel);
        leftColumn.add(horzPanel);

        columns.add(leftColumn);
        columns.add(rightPanel);

        dropZones.add(headerPanel);
        dropZones.add(leftPanel);
        dropZones.add(centerPanel);
        dropZones.add(rightPanel);
    }

    /**
     * Gets columns to add to the panel.
     *
     * @return the columns.
     */
    public List<DropZonePanel> getColumns()
    {
        return columns;
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
