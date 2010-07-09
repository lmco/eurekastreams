/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Tests the data format handling utility.
 */
public class DomainFormatUtilityTest
{
    /**
     * Tests splitting a keyword string.
     */
    @Test
    public void testSplitCapabilitiesString()
    {
        String input = " \t ,,, pathologically   horribly,,\tformatted,,keyword\tlist  \t";

        List<BackgroundItem> list = DomainFormatUtility.splitCapabilitiesString(input);

        // TODO develop some nice helpers to make this cleaner
        assertEquals(3, list.size());
        assertTrue(Matchers.hasItem(
                equalInternally(new BackgroundItem("pathologically horribly", BackgroundItemType.CAPABILITY))).matches(
                list));
        assertTrue(Matchers.hasItem(equalInternally(new BackgroundItem("formatted", BackgroundItemType.CAPABILITY)))
                .matches(list));
        assertTrue(Matchers.hasItem(equalInternally(new BackgroundItem("keyword list", BackgroundItemType.CAPABILITY)))
                .matches(list));
    }

    /**
     * Tests splitting an empty keyword string.
     */
    @Test
    public void testSplitCapabilitiesStringEmpty()
    {
        List<BackgroundItem> list = DomainFormatUtility.splitCapabilitiesString("");

        assertTrue(list.isEmpty());
    }

    /**
     * Tests splitting a null keyword string.
     */
    @Test
    public void testSplitCapabilitiesStringNull()
    {
        List<BackgroundItem> list = DomainFormatUtility.splitCapabilitiesString(null);

        assertTrue(list.isEmpty());
    }


    /**
     * Tests building a keyword string.
     */
    @Test
    public void testBuildCapabilitiesString()
    {
        List<BackgroundItem> list = new ArrayList<BackgroundItem>();
        list.add(new BackgroundItem("this", BackgroundItemType.SKILL));
        list.add(new BackgroundItem("that", BackgroundItemType.SKILL));

        assertEquals("this, that", DomainFormatUtility.buildCapabilitiesString(list));
    }

    /**
     * Tests building a keyword string.
     */
    @Test
    public void testBuildCapabilitiesStringEmpty()
    {
        List<BackgroundItem> list = new ArrayList<BackgroundItem>();

        assertEquals("", DomainFormatUtility.buildCapabilitiesString(list));
    }

}
