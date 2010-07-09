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

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for Layout Enum.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class LayoutTest
{

    /**
     * This method tests the names of the Layout definitions.
     */
    @Test
    public void testLayoutNames()
    {
        assertEquals("TWOCOLUMN definition does not have the correct name",
                "1-column", Layout.ONECOLUMN.getDescription());
        assertEquals("TWOCOLUMN definition does not have the correct name",
                "2-column", Layout.TWOCOLUMN.getDescription());
        assertEquals("TWOCOLUMNLEFTWIDE definition does not have the correct name",
                "2-column Left Wide", Layout.TWOCOLUMNLEFTWIDE.getDescription());
        assertEquals("TWOCOLUMNLEFTWIDEHEADER definition does not have the correct name",
                "3-column Left Wide Header", Layout.THREECOLUMNLEFTWIDEHEADER.getDescription());
        assertEquals("TWOCOLUMNLEFTWIDEHEADER definition does not have the correct name",
                "3-column Right Wide Header", Layout.THREECOLUMNRIGHTWIDEHEADER.getDescription());
        assertEquals("TWOCOLUMNRIGHTWIDE definition does not have the correct name",
                "2-column Right Wide", Layout.TWOCOLUMNRIGHTWIDE.getDescription());
        assertEquals("THREECOLUMN definition does not have the correct name",
                "3-column", Layout.THREECOLUMN.getDescription());
    }

    /**
     * This method tests the number of zones configured with each layout
     * definition.
     */
    @Test
    public void testLayoutZones()
    {
        assertEquals(
                "TWOCOLUMN definition does not have the correct number of zones",
                1, Layout.ONECOLUMN.getNumberOfZones());
        assertEquals(
                "TWOCOLUMN definition does not have the correct number of zones",
                2, Layout.TWOCOLUMN.getNumberOfZones());
        assertEquals(
                "TWOCOLUMNLEFTWIDE definition does not have the correct number of zones",
                2, Layout.TWOCOLUMNLEFTWIDE.getNumberOfZones());
        assertEquals(
                "TWOCOLUMNRIGHTWIDE definition does not have the correct number of zones",
                2, Layout.TWOCOLUMNRIGHTWIDE.getNumberOfZones());
        assertEquals(
                "TWOCOLUMNLEFTWIDEHEADER definition does not have the correct number of zones",
                4, Layout.THREECOLUMNLEFTWIDEHEADER.getNumberOfZones());
        assertEquals(
                "TWOCOLUMNLEFTWIDEHEADER definition does not have the correct number of zones",
                4, Layout.THREECOLUMNRIGHTWIDEHEADER.getNumberOfZones());
        assertEquals(
                "THREECOLUMN definition does not have the correct number of zones",
                3, Layout.THREECOLUMN.getNumberOfZones());
    }
}
