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
package org.eurekastreams.server.service.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


/**
 * Tests CollectionAugmenter.
 */
public class CollectionAugmenterTest
{
    /** Test data. */
    private static final String CARROT = "carrot";

    /** Test data. */
    private static final String BANANA = "banana";

    /** Test data. */
    private static final String APPLE = "apple";

    /**
     * Tests augmenting.
     */
    @Test
    public void testAddListToList()
    {
        List<String> main = new ArrayList<String>();
        main.add(APPLE);

        List<String> extra = new ArrayList<String>();
        main.add(BANANA);
        main.add(CARROT);

        new CollectionAugmenter(main, extra);

        assertEquals(3, main.size());
        assertEquals(APPLE, main.get(0));
        assertEquals(BANANA, main.get(1));
        assertEquals(CARROT, main.get(2));
    }

    /**
     * Tests augmenting.
     */
    @Test
    public void testAddItemToList()
    {
        List<String> main = new ArrayList<String>();
        main.add(APPLE);

        new CollectionAugmenter(main, BANANA);

        assertEquals(2, main.size());
        assertEquals(APPLE, main.get(0));
        assertEquals(BANANA, main.get(1));
    }

    /**
     * Tests augmenting.
     */
    @Test
    public void testAddSetToSet()
    {
        Set<String> main = new HashSet<String>();
        main.add(APPLE);

        Set<String> extra = new HashSet<String>();
        main.add(BANANA);
        main.add(CARROT);

        new CollectionAugmenter(main, extra);

        assertEquals(3, main.size());
        assertTrue(main.contains(APPLE));
        assertTrue(main.contains(BANANA));
        assertTrue(main.contains(CARROT));
    }

    /**
     * Tests augmenting.
     */
    @Test
    public void testAddItemToSet()
    {
        Set<String> main = new HashSet<String>();
        main.add(APPLE);

        new CollectionAugmenter(main, BANANA);

        assertEquals(2, main.size());
        assertTrue(main.contains(APPLE));
        assertTrue(main.contains(BANANA));
    }
}
