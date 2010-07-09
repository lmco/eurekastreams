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
package org.eurekastreams.commons.search.modelview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Test fixture for FieldMatch.
 */
public class FieldMatchTest
{
    /**
     * Test addMatch, isMatch, getMatchingKeywords.
     */
    @Test
    public void testAddMatchAndIsMatchAndGetMatchingKeywords()
    {
        FieldMatch sut = new FieldMatch();
        sut.addMatch("field1", "foo");
        sut.addMatch("field1", "bar");
        sut.addMatch("field2", "potato");
        sut.addMatch("field2", "potato");
        sut.addMatch("field2", "potato");

        assertTrue(sut.isMatch("field1"));
        assertTrue(sut.isMatch("field2"));
        assertFalse(sut.isMatch("field3"));

        // test they come back alphabetically
        Set<String> keywords = sut.getMatchingKeywords("field1");
        assertEquals(2, keywords.size());
        assertTrue(keywords.contains("foo"));
        assertTrue(keywords.contains("bar"));

        keywords = sut.getMatchingKeywords("field2");
        assertEquals(1, keywords.size());
        assertTrue(keywords.contains("potato"));

        keywords = sut.getMatchingKeywords("field3");
        assertEquals(0, keywords.size());

        assertTrue(sut.getMatchedFieldKeys().contains("field1"));
        assertTrue(sut.getMatchedFieldKeys().contains("field2"));
        assertFalse(sut.getMatchedFieldKeys().contains("field3"));
    }

    /**
     * Test the serialization getter/setter.
     */
    @Test
    public void testProperties()
    {
        FieldMatch sut = new FieldMatch();
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        sut.setMatchedFields(map);
        assertSame(map, sut.getMatchedFields());
    }
}
