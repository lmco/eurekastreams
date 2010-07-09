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
package org.eurekastreams.server.domain;

import java.io.Serializable;

/**
 * This enum describes the different types of Layouts that are available
 * to the tabs on a Tab Group.
 *
 * Each Enumeration describes the number of zones in that layout along
 * with a plain text description.  The View will be responsible for
 * actually laying out the display as well as providing an image url
 * that shows the preview of the layout.
 *
 */
public enum Layout implements Serializable
{
    /**
     * One column layout.
     */
    ONECOLUMN(1, "1-column"),

    /**
     * Two column layout.
     */
    TWOCOLUMN(2, "2-column"),

    /**
     * Two column layout left wide.
     */
    TWOCOLUMNLEFTWIDE(2, "2-column Left Wide"),

    /**
     * Two column layout right wide.
     */
    TWOCOLUMNRIGHTWIDE(2, "2-column Right Wide"),

    /**
     * Three column layout left wide.
     */
    THREECOLUMNLEFTWIDEHEADER(4, "3-column Left Wide Header"),

    /**
     * Three column layout right wide.
     */
    THREECOLUMNRIGHTWIDEHEADER(4, "3-column Right Wide Header"),

    /**
     * Three column layout.
     */
    THREECOLUMN(3, "3-column");

    /**
     * Layout description.
     */
    private String description;

    /**
     * Number of zones in layout.
     */
    private int numberOfZones;

    /**
     * Getter for Description.
     * @return Description of layout.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Setter for description.
     * @param inDescription Description of layout.
     */
    @SuppressWarnings("unused")
    private void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * Getter for number of zones.
     * @return The number of zones.
     */
    public int getNumberOfZones()
    {
        return numberOfZones;
    }

    /**
     * Setter for number of zones.
     * @param inNumberOfZones The number of zones.
     */
    @SuppressWarnings("unused")
    private void setNumberOfZones(final int inNumberOfZones)
    {
        numberOfZones = inNumberOfZones;
    }

    /**
     * Private constructor used to provide access to the
     * enumeration's attributes.
     * @param inNumberofZones - number of zones in the layout
     * @param inDescription - plain text description of the layout.
     */
    private Layout(final int inNumberofZones, final String inDescription)
    {
        this.numberOfZones = inNumberofZones;
        this.description = inDescription;
    }
}
