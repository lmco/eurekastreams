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
package org.eurekastreams.server.persistence;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Date;
import java.util.UUID;

import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.PagedSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the gadget mapping in the domain model.
 * 
 */
public class GadgetDefinitionMapperTest extends DomainEntityMapperTest
{
    /**
     * Test instance of the Gadget Mapper that is wired up by Spring.
     */
    @Autowired
    private GadgetDefinitionMapper jpaGadgetDefinitionMapper;

    /**
     * Simple test to be sure that a gadget can be inserted correctly.
     */
    @Test
    public void testInsert()
    {
        String gadgetUrl = "http://www.example.com";
        String uuid = UUID.randomUUID().toString();
        GadgetDefinition testGadgetDefinition = new GadgetDefinition(gadgetUrl, uuid);
        testGadgetDefinition.setCategory(new GalleryItemCategory("somecategory"));
        jpaGadgetDefinitionMapper.insert(testGadgetDefinition);
        assertTrue(testGadgetDefinition.getId() > 0);
        assertEquals(uuid, testGadgetDefinition.getUUID());
    }

    /**
     * Test deleting a gadget def.
     */
    // public void testDelete()
    // {
    // final long gadgetDefId = 1831L;
    // GadgetDefinition gadgetDef = jpaGadgetDefinitionMapper.findById(gadgetDefId);
    // jpaGadgetDefinitionMapper.delete(gadgetDef);
    //
    // gadgetDef = jpaGadgetDefinitionMapper.findById(gadgetDefId);
    //
    // assertTrue("The gadget def was not deleted", gadgetDef == null);
    // }
    /**
     * Test to ensure that the dbunit dataset is correctly constructed.
     */
    @Test
    public void testDBUnityDataSet()
    {
        String gadgetUrl1 = "http://www.example.com/gadget1.xml";
        String gadgetUrl2 = "http://www.example.com/gadget2.xml";

        GadgetDefinition googleGadgetDefinition = jpaGadgetDefinitionMapper.findById(gadgetDefinitionId1);

        GadgetDefinition exampleGadgetDefinition = jpaGadgetDefinitionMapper.findById(gadgetDefinitionId2);

        assertEquals("DB Unit database not correct for gadget " + gadgetDefinitionId1, gadgetUrl1,
                googleGadgetDefinition.getUrl());

        assertEquals("DB Unit database not correct for Example gadget " + gadgetDefinitionId2, gadgetUrl2,
                exampleGadgetDefinition.getUrl());
    }

    /**
     * Tests findbyUUID.
     */
    @Test
    public void findByUUID()
    {
        // UUID of the Example Gadget Definition
        String uuid = "129d4fae-7dec-11d0-a765-00a0c91e6bf6";
        GadgetDefinition gadgetDef = jpaGadgetDefinitionMapper.findByUUID(uuid);
        final long defId = 1831L;
        assertEquals(defId, gadgetDef.getId());

    }

    /**
     * Tests the findSortedGadgetDefinitionsForCategory method.
     */
    @Test
    public void testFindGadgetDefinitionsForCategorySortedByPopularity()
    {
        // verify that it returns results.
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findForCategorySortedByPopularity("News", 0, 1);

        assertEquals(2, results.getPagedSet().size());
        assertEquals(2, results.getPagedSet().size());
        int firstNumberOfUsers = results.getPagedSet().get(0).getNumberOfUsers();
        int secondNumberOfUsers = results.getPagedSet().get(1).getNumberOfUsers();

        assertTrue(firstNumberOfUsers > secondNumberOfUsers);
    }

    /**
     * Tests the findAll method.
     */
    @Test
    public void testFindAllGadgetDefinitions()
    {
        // verify that it returns results.
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findAll(0, 1);

        assertEquals(2, results.getPagedSet().size());
        assertEquals(2, results.getPagedSet().size());
    }

    /**
     * Tests the findSortedGadgetDefinitionsForCategory method.
     */
    @Test
    public void testFindGadgetDefinitionsForCategorySortedByRecent()
    {
        // verify that it returns results.
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findForCategorySortedByRecent("News", 0, 1);
        assertEquals(2, results.getPagedSet().size());
        Date firstDate = results.getPagedSet().get(0).getCreated();
        Date secondDate = results.getPagedSet().get(1).getCreated();

        assertTrue(firstDate.after(secondDate));
    }

    /**
     * Tests that the findSortedGadgetDefinitionsSortedByRecentForCategory method returns only gadget defs of the
     * specified category.
     */
    @Test
    public void testFindGadgetDefinitionsForCategorySortedByRecentReturnsRightCategory()
    {
        // verify that it returns results.
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findForCategorySortedByRecent("News", 0, 1);
        assertEquals(2, results.getPagedSet().size());
        GalleryItemCategory firstCategory = results.getPagedSet().get(0).getCategory();
        GalleryItemCategory secondCategory = results.getPagedSet().get(1).getCategory();

        assertEquals(firstCategory.getName(), "News");
        assertEquals(secondCategory.getName(), "News");
    }

    /**
     * Tests that the findSortedGadgetDefinitionsSortedByPopularityForCategory method returns only gadget defs of the
     * specified category.
     */
    @Test
    public void testFindGadgetDefinitionsForCategorySortedByPopularityReturnsRightCategory()
    {
        // verify that it returns results.
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findForCategorySortedByPopularity("Weather", 0,
                1);
        assertEquals(2, results.getPagedSet().size());
        GalleryItemCategory firstCategory = results.getPagedSet().get(0).getCategory();
        GalleryItemCategory secondCategory = results.getPagedSet().get(1).getCategory();

        assertEquals(firstCategory.getName(), "Weather");
        assertEquals(secondCategory.getName(), "Weather");
    }

    /**
     * Tests that the findSortedGadgetDefinitionsSortedByPopularityForCategory method returns gadget defs of any
     * category when category parameter is empty.
     */
    @Test
    public void testFindGadgetDefinitionsForCategorySortedByPopularityReturnsAll()
    {
        // verify that it returns results.
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findSortedByPopularity(0, 3);
        assertEquals(4, results.getPagedSet().size());

        int firstNumberOfUsers = results.getPagedSet().get(0).getNumberOfUsers();
        int secondNumberOfUsers = results.getPagedSet().get(1).getNumberOfUsers();
        int thirdNumberOfUsers = results.getPagedSet().get(2).getNumberOfUsers();
        int forthNumberOfUsers = results.getPagedSet().get(3).getNumberOfUsers();

        assertTrue(firstNumberOfUsers > secondNumberOfUsers);
        assertTrue(secondNumberOfUsers > thirdNumberOfUsers);
        assertTrue(thirdNumberOfUsers > forthNumberOfUsers);
    }

    /**
     * Tests that the Refresh method updates the gadget user counts appropriately.
     */
    @Test
    public void testRefreshGadgetDefinitionUserCounts()
    {
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findSortedByPopularity(0, 3);
        assertEquals("Size of result set is invalid", 4, results.getPagedSet().size());
        assertEquals("Most popular before refresh is not 4", 4, results.getPagedSet().get(0)
                .getNumberOfUsers());
        assertEquals("Most popular before refresh is not 3", 3, results.getPagedSet().get(1)
                .getNumberOfUsers());
        assertEquals("Most popular before refresh is not 2", 2, results.getPagedSet().get(2)
                .getNumberOfUsers());
        assertEquals("Most popular before refresh is not 1", 1, results.getPagedSet().get(3)
                .getNumberOfUsers());

        jpaGadgetDefinitionMapper.refreshGadgetDefinitionUserCounts();

        getEntityManager().clear();

        PagedSet<GadgetDefinition> refreshedResults = jpaGadgetDefinitionMapper.findSortedByPopularity(0, 3);
        assertEquals("Size of refreshed result set is invalid", 4, refreshedResults.getPagedSet().size());
        assertEquals("Most popular after refresh is not 4", 3, refreshedResults.getPagedSet().get(0)
                .getNumberOfUsers());
        assertEquals("Most popular after refresh is not 4", 2, refreshedResults.getPagedSet().get(1)
                .getNumberOfUsers());
        assertEquals("Most popular after refresh is not 1", 1, refreshedResults.getPagedSet().get(2)
                .getNumberOfUsers());
        assertEquals("Most popular after refresh is not 0", 0, refreshedResults.getPagedSet().get(3)
                .getNumberOfUsers());
    }

    /**
     * Tests that the findSortedGadgetDefinitionsSortedByPopularityForCategory method returns gadget defs of any
     * category when category parameter is empty.
     */
    @Test
    public void testFindGadgetDefinitionsForCategorySortedByRecentReturnsAll()
    {
        // verify that it returns results.
        PagedSet<GadgetDefinition> results = jpaGadgetDefinitionMapper.findSortedByRecent(0, 3);
        assertEquals(4, results.getPagedSet().size());
    }

    /**
     * Test the findOrCreate() method using a theme that is in the database.
     */
    @Test
    public void findByUrlWithExistingUrl()
    {
        GadgetDefinition gd = jpaGadgetDefinitionMapper.findByUrl("http://www.example.com/gadget1.xml");

        assertNotNull("Did not find the gadget def", gd);
    }

    /**
     * Test the findOrCreate() method using a theme that is in the database.
     */
    @Test
    public void findByUrlWithNonExistingUrl()
    {
        GadgetDefinition gd = jpaGadgetDefinitionMapper.findByUrl("http://www.nonexistentgadgetdef.com");

        assertEquals("found the theme , but it should not exist", null, gd);
    }
}
