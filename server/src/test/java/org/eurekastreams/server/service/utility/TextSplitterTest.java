/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.List;

import org.junit.Test;

/**
 * Tests TextSplitter.
 */
public class TextSplitterTest
{
    /**
     * Test.
     */
    @Test
    public void testEmptyString()
    {
        TextSplitter sut = new TextSplitter(5, 5);
        assertTrue(sut.split("").isEmpty());
    }

    /**
     * Test.
     */
    @Test
    public void testBlankString()
    {
        TextSplitter sut = new TextSplitter(5, 5);
        assertTrue(sut.split("             ").isEmpty());
    }

    /**
     * Test.
     */
    @Test
    public void testAllInFirstBlock()
    {
        TextSplitter sut = new TextSplitter(9, 5);
        String val = "1 piece";
        assertPieces(sut.split(val), val);
    }

    /**
     * Test.
     */
    @Test
    public void testAllInFirstBlockWithWhitespace()
    {
        TextSplitter sut = new TextSplitter(9, 5);
        assertPieces(sut.split("        1 piece      "), "1 piece");
    }

    /**
     * Test.
     */
    @Test
    public void testDefaultDelimiters()
    {
        TextSplitter sut = new TextSplitter(10, 10);
        assertPieces(sut.split("This is a test."), "This is...", "...a test.");
    }

    /**
     * Test.
     */
    @Test
    public void testNoBreaks()
    {
        TextSplitter sut = new TextSplitter(9, 5, "<", ">");
        List<String> pieces = sut.split("    ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        assertPieces(pieces, "ABCDEFGH>", "<IJK>", "<LMN>", "<OPQ>", "<RST>", "<UVW>", "<XYZ");
    }

    /**
     * Test.
     */
    @Test
    public void testScan()
    {
        TextSplitter sut = new TextSplitter(13, 10, "<", ">");
        List<String> pieces = sut.split("      aaa  bbb  ccc d  eee fff     ggg hhh i     ");
        assertPieces(pieces, "aaa  bbb>", "<ccc d>", "<eee fff>", "<ggg hhh i");
    }

    /**
     * Asserts that the resulting pieces are as expected.
     *
     * @param actual
     *            The list returned from the SUT.
     * @param expected
     *            The expected list of strings.
     */
    private void assertPieces(final List<String> actual, final String... expected)
    {
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], actual.get(i));
        }
    }
}
