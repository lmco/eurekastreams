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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for TabGroup.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class TabGroupTest
{
    /**
     * Basic test to ensure the setTheme works properly.
     */
    @Test
    public void testAddTabs()
    {
        Tab tab = new Tab("testTab", Layout.THREECOLUMN, new Long(1));
        TabGroup tabGroup = new TabGroup();

        List<Tab> tabs = tabGroup.getTabs();
        tabGroup.addTab(tab);
        assertEquals("addTab() doesn't add a tab to the end of the list", tab.getTabName(), tabGroup.getTabs().get(
                tabs.size() - 1).getTabName());
    }

    /**
     * Test readOnly.
     */
    @Test
    public void testReadOnly()
    {
        TabGroup tg = new TabGroup();
        assertEquals(false, tg.getReadOnly());
        tg.setReadOnly(true);
        assertEquals(true, tg.getReadOnly());
    }
}
