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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.ResourceSortCriterion.SortDirection;
import org.eurekastreams.server.domain.ResourceSortCriterion.SortField;
import org.junit.Test;

/**
 * Test fixture for ResourceSortCriterion.
 */
public class ResourceSortCriterionTest
{
    /**
     * Test the constructor and getters.
     */
    @Test
    public void testConstructor()
    {
        // test the empty constructor just for coverage.
        ResourceSortCriterion crit = new ResourceSortCriterion();

        crit = new ResourceSortCriterion(SortField.DATE_ADDED, SortDirection.ASCENDING);
        assertEquals(SortField.DATE_ADDED, crit.getSortField());
        assertEquals(SortDirection.ASCENDING, crit.getSortDirection());

        crit = new ResourceSortCriterion(SortField.FOLLOWERS_COUNT, SortDirection.DESCENDING);
        assertEquals(SortField.FOLLOWERS_COUNT, crit.getSortField());
        assertEquals(SortDirection.DESCENDING, crit.getSortDirection());

        crit = new ResourceSortCriterion(SortField.UPDATES_COUNT, SortDirection.DESCENDING);
        assertEquals(SortField.UPDATES_COUNT, crit.getSortField());
        assertEquals(SortDirection.DESCENDING, crit.getSortDirection());

        crit = new ResourceSortCriterion(SortField.NAME, SortDirection.ASCENDING);
        assertEquals(SortField.NAME, crit.getSortField());
        assertEquals(SortDirection.ASCENDING, crit.getSortDirection());
    }

    /**
     * Test the getters/setters.
     */
    @Test
    public void testProperties()
    {
        ResourceSortCriterion crit = new ResourceSortCriterion(SortField.DATE_ADDED, SortDirection.ASCENDING);
        assertEquals(SortField.DATE_ADDED, crit.getSortField());
        assertEquals(SortDirection.ASCENDING, crit.getSortDirection());

        crit.setSortField(SortField.FOLLOWERS_COUNT);
        assertEquals(SortField.FOLLOWERS_COUNT, crit.getSortField());

        crit.setSortField(SortField.UPDATES_COUNT);
        assertEquals(SortField.UPDATES_COUNT, crit.getSortField());

        crit.setSortField(SortField.DATE_ADDED);
        assertEquals(SortField.DATE_ADDED, crit.getSortField());

        crit.setSortField(SortField.NAME);
        assertEquals(SortField.NAME, crit.getSortField());

        crit.setSortDirection(SortDirection.DESCENDING);
        assertEquals(SortDirection.DESCENDING, crit.getSortDirection());

        crit.setSortDirection(SortDirection.ASCENDING);
        assertEquals(SortDirection.ASCENDING, crit.getSortDirection());
    }
}
