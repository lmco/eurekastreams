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

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Creates a 3 column layout with a header in the right wide column.
 */
public class ThreeColumnRightWideHeaderLayoutStrategy implements
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
    public ThreeColumnRightWideHeaderLayoutStrategy()
    {
        DropZonePanel rightColumn = DropZonePanel.getTwoThirdColumnDropZone(0);
        rightColumn.addStyleName(StaticResourceBundle.INSTANCE.coreCss().multiZone());
        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().twoThirdsColumn());

        DropZonePanel headerPanel = DropZonePanel.getFullRowDropZone(0);
        headerPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().headerZone());
        DropZonePanel leftPanel = DropZonePanel.getThirdColumnDropZone(1);
        DropZonePanel centerPanel = DropZonePanel.getFullRowDropZone(2);
        DropZonePanel rightPanel = DropZonePanel.getFullRowDropZone(3);

        rightColumn.add(headerPanel);
        horzPanel.add(centerPanel);
        horzPanel.add(rightPanel);
        rightColumn.add(horzPanel);

        columns.add(leftPanel);
        columns.add(rightColumn);

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
