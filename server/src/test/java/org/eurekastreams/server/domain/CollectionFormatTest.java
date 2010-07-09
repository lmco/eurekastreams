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
/**
 * 
 */
package org.eurekastreams.server.domain;


import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class CollectionFormatTest
{

    /**
     * for error reporting.
     */
    private String message = "";
    
    /**
     * SUT.
     */
    private CollectionFormat formatter = new CollectionFormat();
    
    /**
     * @throws Exception
     * 		no expected.
     */
    @Before
    public void setUp() throws Exception
    {
    }

    /**
     * @throws Exception
     * 		no expected.
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * test parsing.
     * 
     * type warnings are suppressed intentionally. 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testStringParsing()
    {
        message = "string should be parsed appropriately";

        ArrayList<String> defaultTags = new ArrayList<String>();
        defaultTags.add("TagName1");
        defaultTags.add("TagName2");
        defaultTags.add("TagName3");
        
        // if you get a normal expected string
        Collection actual = formatter.parse("TagName1, TagName2, TagName3");
        assertArrayEquals(message, defaultTags.toArray(), actual.toArray());

        // if you do funky things with the commas or have extra space
        actual = formatter.parse("TagName1, TagName2, TagName3,, ,");
        assertArrayEquals(message, defaultTags.toArray(), actual.toArray());
        
        // if the string is empty
        actual = formatter.parse("");
        assertArrayEquals(message, new String[]{}, actual.toArray());
        
    }
    
    /**
     * test tag string creation.
     */
    @Test
    public void testStringCreation()
    {
        message = "string should be created appropriately";

        // if you get a normal expected string
        Set<String> tagList = new HashSet<String>();
        
        // empty list returns empty string
        String actual = formatter.format(tagList);
        assertEquals(message, "", actual);
        
        // populated list returns populated string
        tagList.add("TagName1");
        tagList.add("TagName2");
        tagList.add("TagName3");
        actual = formatter.format(tagList);
        assertEquals(message, "TagName1, TagName2, TagName3", actual);
    }

}
