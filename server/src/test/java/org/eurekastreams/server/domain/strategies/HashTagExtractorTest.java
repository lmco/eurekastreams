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
package org.eurekastreams.server.domain.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.commons.test.IsEqualInternally;
import org.junit.Test;

/**
 * Test fixture for HashTagExtractor.
 */
public class HashTagExtractorTest
{
    /**
     * System under test.
     */
    private final HashTagExtractor sut = new HashTagExtractor();

    /**
     * Test extract returns null when no hashtags.
     */
    @Test
    public void testExtractNoHashtags()
    {
        assertNull(sut.extract("I like cheese", 0));
    }

    /**
     * Test extract returns null with null content.
     */
    @Test
    public void testExtractNullContent()
    {
        assertNull(sut.extract(null, 0));
    }

    /**
     * Test extract returns null with empty content.
     */
    @Test
    public void testExtractEmptyContent()
    {
        assertNull(sut.extract("", 0));
    }

    /**
     * Test extract returns the first hashtag when asked to start less than 0.
     */
    @Test
    public void testExtractNullWhenStartingIndexNegative()
    {
        String content = "Did you know that #potatoes are made of #frenchfries?";
        Substring expectedResult = new Substring(9 * 2, 9, "#potatoes");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, -1)));
    }

    /**
     * Test extract finds the first of two hashtags in content.
     */
    @Test
    public void testExtractFirstOfTwoHashtags()
    {
        String content = "Did you know that #potatoes are made of #frenchfries?";
        Substring expectedResult = new Substring(9 * 2, 9, "#potatoes");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 2)));
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 + 1)));
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 + 8)));
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 + 9)));
    }

    /**
     * Test extract finds the second of two hashtags in content.
     */
    @Test
    public void testExtractSecondOfTwoHashtags()
    {
        String content = "Did you know that #potatoes are made of #frenchfries?";
        Substring expectedResult = new Substring(9 * 4 + 4, 9 + 3, "#frenchfries"); // 40, 12
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 * 2 + 1))); // 19
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 * 2 + 7))); // 25
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 * 3 + 3))); // 30
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 * 4 + 3))); // 39
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 9 * 4 + 4))); // 40
    }

    /**
     * Test extract finds the first of two hashtags in content.
     */
    @Test
    public void testExtractNullWhenNoMoreHashtags()
    {
        String content = "Did you know that #potatoes are made of #frenchfries?";
        assertNull(sut.extract(content, 9 * 4 + 5)); // 41
        assertNull(sut.extract(content, 9 * 5 + 7)); // 52
    }

    /**
     * Test extract finds one after a newline.
     */
    @Test
    public void testExtractHashTagAfterNewline()
    {
        String content = "Did you know that\n#potatoes are made of frenchfries?";
        Substring expectedResult = new Substring(9 * 2, 9, "#potatoes");
        Substring result = sut.extract(content, -1);
        assertNotNull(result);
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, result));
    }

    /**
     * Test extract finds one after a tab.
     */
    @Test
    public void testExtractHashTagAfterTab()
    {
        String content = "Did you know that\t#potatoes are made of frenchfries?";
        Substring expectedResult = new Substring(9 * 2, 9, "#potatoes");
        Substring result = sut.extract(content, -1);
        assertNotNull(result);
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, result));
    }

    /**
     * Test extract returns null when asked to start past the length.
     */
    @Test
    public void testExtractNullWhenStartingIndexPastContentLength()
    {
        String content = "Did you know that #potatoes are made of #frenchfries?";
        assertNull(sut.extract(content, 9 * 5 + 8)); // 53
        assertNull(sut.extract(content, 9 * 5 + 9)); // 54
        assertNull(sut.extract(content, 9 * 9 * 9)); // 729
    }

    /**
     * Test extract finds hashtag in content with period at the end of hashtag.
     */
    @Test
    public void testExtractFindsHashtagWithEndingPeriod()
    {
        String content = "test content #foo.";
        Substring expectedResult = new Substring(9 + 4, 4, "#foo"); // 13
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
    }

    /**
     * Test extract finds hashtag in content with a hash in it.
     */
    @Test
    public void testExtractFindsHashtagWithHash()
    {
        String content = "#foo#snuts.";
        Substring expectedResult = new Substring(0, 4, "#foo");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
        assertNull(sut.extract(content, 1));
    }

    /**
     * Test extract finds hashtag in content with a hash in it.
     */
    @Test
    public void testExtractFindsHashtagWithHashAndUnderscore()
    {
        String content = "#foo_#snuts.";
        Substring expectedResult = new Substring(0, 5, "#foo_");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
        assertNull(sut.extract(content, 1));
    }

    /**
     * Test extract finds hashtag with underscore.
     */
    @Test
    public void testExtractFindsHashtagWithUnderscore()
    {
        String content = "test content #foo_bar.";
        Substring expectedResult = new Substring(9 + 4, 8, "#foo_bar"); // 13
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
    }

    /**
     * Test extract ignores a pound found in a hashtag.
     */
    @Test
    public void testExtractIgnoresPoundInUrl()
    {
        String content = "Hello there, check out this link http://somedomain.com/foo#bar - no hashtags here.";
        assertNull(sut.extract(content, 0));
    }

    /**
     * Test extract ignores a pound found in a hashtag and finds a subsequent hashtag.
     */

    @Test
    public void testExtractIgnoresPoundInUrlAndFindsSubsequentHashTag()
    {
        String content = "Hello there, check out this link http://somedomain.com/foo#bar - here's a #hashtag - k?";
        Substring expectedResult = new Substring(9 * 8 + 2, 8, "#hashtag"); // 74
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
    }

    /**
     * Test extract finds a hashtag in the beginning of the content.
     */
    @Test
    public void testExtractFindsHashtagAtBeginning()
    {
        String content = "#Hello there";
        Substring expectedResult = new Substring(0, 6, "#Hello");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
    }

    /**
     * Test extract finds a hashtag as the only content.
     */
    @Test
    public void testExtractFindsSolitaryHashtag()
    {
        String content = "#Hello";
        Substring expectedResult = new Substring(0, 6, "#Hello");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
    }

    /**
     * Test extract finds a hashtag as the only content, but with a period.
     */
    @Test
    public void testExtractFindsSolitaryHashTagWithEndPeriod()
    {
        String content = "#Hello.";
        Substring expectedResult = new Substring(0, 6, "#Hello");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
    }

    /**
     * Test extract finds a hashtag at end with period.
     */
    @Test
    public void testExtractFindsHashTagWithEnd()
    {
        String content = "Hello?  #Hello";
        Substring expectedResult = new Substring(8, 6, "#Hello");
        assertTrue(IsEqualInternally.areEqualInternally(expectedResult, sut.extract(content, 0)));
    }

    /**
     * Test extractAll with no hashtags.
     */
    @Test
    public void testExtractAllNoHashtags()
    {
        String content = "Hello there! What's up?";
        assertEquals(0, sut.extractAll(content).size());
    }

    /**
     * Test extractAll with only a hashtag.
     */
    @Test
    public void testExtractAllJustAHashTag()
    {
        String content = "#HI";
        List<String> hashtags = sut.extractAll(content);
        assertEquals(1, hashtags.size());
        assertTrue(hashtags.contains("#HI"));
    }

    /**
     * Test extractAll with multiple hashtags.
     */
    @Test
    public void testExtractAllMultiple()
    {
        String content = "Hello #HI #There";
        List<String> hashtags = sut.extractAll(content);
        assertEquals(2, hashtags.size());
        assertTrue(hashtags.contains("#HI"));
        assertTrue(hashtags.contains("#There"));
    }

    /**
     * Test extractAll with multiple hashtags.
     */
    @Test
    public void testExtractAllMultiple2()
    {
        String content = "Hello #HI #There.";
        List<String> hashtags = sut.extractAll(content);
        assertEquals(2, hashtags.size());
        assertTrue(hashtags.contains("#HI"));
        assertTrue(hashtags.contains("#There"));
    }
}
