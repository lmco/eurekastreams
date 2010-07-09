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
import static junit.framework.Assert.assertTrue;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PD mapper test.
 * 
 */
public class PluginDefinitionMapperTest extends DomainEntityMapperTest
{

    /**
     * The subject under test.
     */
    @Autowired
    private PluginDefinitionMapper sut;

    /**
     * Test to make sure plugin mapper returns a plugin from it's parent.
     */
    @Test
    public void testThatPluginMapperIsTemplatedWell()
    {
        PluginDefinition plugin = sut.findByUrl("http://www.google1.com");
        assertEquals((Long) (9L), plugin.getUpdateFrequency());
        assertEquals(0, plugin.getNumberOfUsers());
    }

    /**
     * Test the overwritten Delete.
     */
    @Test
    public void testDelete()
    {
        PagedSet<PluginDefinition> beforeDelete = sut.findSortedByRecent(1, 9);

        assertEquals(3, beforeDelete.getTotal());

        PluginDefinition shouldHaveThisPlugin = sut.findByUrl("http://www.google1.com");

        sut.delete(shouldHaveThisPlugin);

        PagedSet<PluginDefinition> afterDelete = sut.findSortedByRecent(1, 9);

        assertEquals(2, afterDelete.getTotal());
    }
    
    /**
     * Test the overwritten update count.
     */
    @Test
    public void testUpdateCount()
    {
        PluginDefinition pluginCountTester = sut.findById(1L);
        assertEquals(0, pluginCountTester.getNumberOfUsers());

        pluginCountTester = sut.findById(2L);
        assertEquals(0, pluginCountTester.getNumberOfUsers());
        
        pluginCountTester = sut.findById(3L);
        assertEquals(0, pluginCountTester.getNumberOfUsers());
        
        
        sut.refreshGadgetDefinitionUserCounts();
        
        PluginDefinition pluginCountTesterAfter = sut.findById(1L);
        assertEquals(3, pluginCountTesterAfter.getNumberOfUsers());
        
        pluginCountTesterAfter = sut.findById(2L);
        assertEquals(2, pluginCountTesterAfter.getNumberOfUsers());
        
        pluginCountTesterAfter = sut.findById(3L);
        assertEquals(1, pluginCountTesterAfter.getNumberOfUsers());
        
    }
    

    /**
     * Tests the findSortedGadgetDefinitionsForCategory method.
     */
    @Test
    public void testFindPluginDefinitionsForCategorySortedByPopularity()
    {
        sut.refreshGadgetDefinitionUserCounts();
        
        // verify that it returns results.
        PagedSet<PluginDefinition> results = sut.findForCategorySortedByPopularity("All", 0, 9);

        assertEquals(3, results.getPagedSet().size());

        int firstNumberOfUsers = results.getPagedSet().get(0).getNumberOfUsers();
        int secondNumberOfUsers = results.getPagedSet().get(1).getNumberOfUsers();

        assertTrue(firstNumberOfUsers > secondNumberOfUsers);
    }
    
    /**
     * make sure that it returns it's name.
     */
    @Test
    public void testGetDomainEntityName()
    {
        assertEquals("PluginDefinition", sut.getDomainEntityName());
    }
}
