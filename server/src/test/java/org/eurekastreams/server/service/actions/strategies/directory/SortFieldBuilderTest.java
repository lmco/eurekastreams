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
package org.eurekastreams.server.service.actions.strategies.directory;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Sort;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.server.domain.ResourceSortCriteria;
import org.eurekastreams.server.domain.ResourceSortCriterion;
import org.eurekastreams.server.domain.ResourceSortCriterion.SortDirection;
import org.eurekastreams.server.domain.ResourceSortCriterion.SortField;

/**
 * Test fixture for SortFieldBuilder.
 */
public class SortFieldBuilderTest
{
    /**
     * The system under test.
     */
    private SortFieldBuilder sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new SortFieldBuilder();
    }

    /**
     * Test getSort() by date added, ascending.
     */
    @Test
    public void testGetSortByDateAdded()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.DATE_ADDED, SortDirection.ASCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("dateAdded", sort.getSort()[0].getField());
        assertEquals(false, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by date added, descending.
     */
    @Test
    public void testGetSortByDateAddedDescending()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.DATE_ADDED, SortDirection.DESCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("dateAdded", sort.getSort()[0].getField());
        assertEquals(true, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by followers count, ascending.
     */
    @Test
    public void testGetSortByFollowersCount()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.FOLLOWERS_COUNT, SortDirection.ASCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("followersCount", sort.getSort()[0].getField());
        assertEquals(false, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by followers count added.
     */
    @Test
    public void testGetSortByFollowersCountDescending()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.FOLLOWERS_COUNT, SortDirection.DESCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("followersCount", sort.getSort()[0].getField());
        assertEquals(true, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by name, ascending.
     */
    @Test
    public void testGetSortByName()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.NAME, SortDirection.ASCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("byName", sort.getSort()[0].getField());
        assertEquals(false, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by name, descending.
     */
    @Test
    public void testGetSortByNameDescending()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.NAME, SortDirection.DESCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("byName", sort.getSort()[0].getField());
        assertEquals(true, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by updates count, ascending.
     */
    @Test
    public void testGetSortByUpdatesCount()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.UPDATES_COUNT, SortDirection.ASCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("updatesCount", sort.getSort()[0].getField());
        assertEquals(false, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by date updates count, descending.
     */
    @Test
    public void testGetSortByUpdatesCountDescending()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.UPDATES_COUNT, SortDirection.DESCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("updatesCount", sort.getSort()[0].getField());
        assertEquals(true, sort.getSort()[0].getReverse());
    }

    /**
     * Test getSort() by name ascending, updates descending.
     */
    @Test
    public void testGetSortByNameAscendingAndUpdatesDescending()
    {
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.NAME, SortDirection.ASCENDING));
        sortCrit.add(new ResourceSortCriterion(SortField.UPDATES_COUNT, SortDirection.DESCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        Sort sort = sut.getSort(criteria);
        assertEquals(2, sort.getSort().length);

        // name, ascending
        assertEquals("byName", sort.getSort()[0].getField());
        assertEquals(false, sort.getSort()[0].getReverse());

        // updates count, descending
        assertEquals("updatesCount", sort.getSort()[1].getField());
        assertEquals(true, sort.getSort()[1].getReverse());
    }

    /**
     * Test overriding the sort method with setOverridingSortCriteria.
     */
    @Test
    public void testGetSortByFollowersCountDescendingFromOverride()
    {
        // setup the override as followersCount, descending
        List<ResourceSortCriterion> overridingSortCrit = new ArrayList<ResourceSortCriterion>();
        overridingSortCrit.add(new ResourceSortCriterion(SortField.FOLLOWERS_COUNT, SortDirection.DESCENDING));
        sut.setOverridingSortCriteria(new ResourceSortCriteria(overridingSortCrit));

        // now try different input criteria - name
        List<ResourceSortCriterion> sortCrit = new ArrayList<ResourceSortCriterion>();
        sortCrit.add(new ResourceSortCriterion(SortField.NAME, SortDirection.ASCENDING));
        ResourceSortCriteria criteria = new ResourceSortCriteria(sortCrit);

        // sort should come back as followers count, descending, ignoring the input criteria
        Sort sort = sut.getSort(criteria);
        assertEquals(1, sort.getSort().length);
        assertEquals("followersCount", sort.getSort()[0].getField());
        assertEquals(true, sort.getSort()[0].getReverse());
    }
}
