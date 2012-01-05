/*
 * Copyright (c) 2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests CommentBodyTransformer.
 */
public class CommentBodyTransformerTest
{
    /** Test data. */
    private static final String TEST_STRING_STANDALONE_TAG = "<br/>abcdef";

    /** Test data. */
    private static final String TEST_STRING_PAIRED_TAGS = "<a href='abc'>def</a>ghijk";

    /** SUT. */
    private CommentBodyTransformer sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new CommentBodyTransformer(null);
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint1()
    {
        assertEquals(5, sut.determineTruncationPoint("abcdefghi", 5));
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint2()
    {
        assertEquals(9, sut.determineTruncationPoint(TEST_STRING_STANDALONE_TAG, 9));
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint3()
    {
        assertEquals(5, sut.determineTruncationPoint(TEST_STRING_STANDALONE_TAG, 5));
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint4()
    {
        assertEquals(5, sut.determineTruncationPoint(TEST_STRING_STANDALONE_TAG, 2));
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint5()
    {
        assertEquals(21, sut.determineTruncationPoint(TEST_STRING_PAIRED_TAGS, 3));
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint6()
    {
        assertEquals(21, sut.determineTruncationPoint(TEST_STRING_PAIRED_TAGS, 15));
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint7()
    {
        assertEquals(21, sut.determineTruncationPoint(TEST_STRING_PAIRED_TAGS, 19));
    }

    /**
     * Tests determining truncation point.
     */
    @Test
    public void testTruncPoint8()
    {
        assertEquals(23, sut.determineTruncationPoint(TEST_STRING_PAIRED_TAGS, 23));
    }
}
