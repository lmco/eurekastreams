/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
 * A criteria for which to sort a person or domain group.
 */
public class ResourceSortCriterion implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1646579071059869146L;

    /**
     * The field to sort by.
     */
    public enum SortField implements Serializable
    {
        /**
         * Sort by the resource's name.
         */
        NAME,

        /**
         * Sort by the date the resource was added to the system.
         */
        DATE_ADDED,

        /**
         * Sort by the number of people following the resource.
         */
        FOLLOWERS_COUNT,

        /**
         * Sort by the number of updates the resource has.
         */
        UPDATES_COUNT
    }

    /**
     * The direction of sort.
     */
    public enum SortDirection implements Serializable
    {
        /**
         * Sort ascending.
         */
        ASCENDING,

        /**
         * Sort descending.
         */
        DESCENDING
    }

    /**
     * The sort field.
     */
    private SortField sortField;

    /**
     * The sort direction.
     */
    private SortDirection sortDirection;

    /**
     * Empty constructor for serialization.
     */
    protected ResourceSortCriterion()
    {
    }

    /**
     * Constructor.
     * 
     * @param inSortField
     *            the field to sort by
     * @param inSortDirection
     *            the direction to sort by
     */
    public ResourceSortCriterion(final SortField inSortField, final SortDirection inSortDirection)
    {
        sortField = inSortField;
        sortDirection = inSortDirection;
    }

    /**
     * Get the sort field.
     * 
     * @return the sortField
     */
    public SortField getSortField()
    {
        return sortField;
    }

    /**
     * Set the sort field.
     * 
     * @param inSortField
     *            the sortField to set
     */
    protected void setSortField(final SortField inSortField)
    {
        this.sortField = inSortField;
    }

    /**
     * Get the sort direction.
     * 
     * @return the sortDirection
     */
    public SortDirection getSortDirection()
    {
        return sortDirection;
    }

    /**
     * Set the sort direction.
     * 
     * @param inSortDirection
     *            the sortDirection to set
     */
    protected void setSortDirection(final SortDirection inSortDirection)
    {
        this.sortDirection = inSortDirection;
    }

}
