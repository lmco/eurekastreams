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
package org.eurekastreams.commons.search.analysis;

import org.junit.Test;

/**
 * Test fixture for HashTagTextStemmerSearchAnalyzer.
 */
public class HashTagTextStemmerSearchAnalyzerTest extends AnalyzerTestBase
{
    /**
     * System under test.
     */
    private final HashTagTextStemmerSearchAnalyzer analyzer = new HashTagTextStemmerSearchAnalyzer();

    /**
     * Test the analyzer handles hashtags properly.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testParseHandlesHashes() throws Exception
    {
        String html = "#Hello #horse, monkey potato fork,,fork dogs";
        assertAnalyzesTo(analyzer, html,
                new String[] { "monkey", "potato", "fork", "fork", "dog", "#hello", "#horse" });
    }

    /**
     * Test the analyzer handles hashtags in the middle of the word properly.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testParseHandlesHashesInMiddleOfWords() throws Exception
    {
        String html = "#c# c#, c";
        assertAnalyzesTo(analyzer, html, new String[] { "c", "#c#", "c#" });
    }

    /**
     * Test parsing a hashtag with an underscore.
     * 
     * @throws Exception
     *             shouldn't happen
     */
    @Test
    public void testParseHashtagWithUnderscore() throws Exception
    {
        String html = "#test_run";
        assertAnalyzesTo(analyzer, html, new String[] { "#test_run" });
    }

    /**
     * Test the analyzer handles.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testHashOnly() throws Exception
    {
        String html = "#";

        // garbage in, garbage out - this won't return results, who cares?
        assertAnalyzesTo(analyzer, html, new String[] { "#" });
    }

    /**
     * Perform a simple Analyzer test with words that don't stem.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testSimpleParse() throws Exception
    {
        assertAnalyzesTo(analyzer, "hello monkey potato fork,,fork", new String[] { "hello", "monkey", "potato",
                "fork", "fork" });
    }

    /**
     * Test the analyzer does not remove HTML tags, resulting in "p".
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testDoesNotParseHtmlTags() throws Exception
    {
        assertAnalyzesTo(analyzer, "<p>hello monkey potato fork,,fork</p>", new String[] { "p", "hello", "monkey",
                "potato", "fork", "fork", "p" });
    }

    /**
     * Test the analyzer properly stems words.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testStemsAndThrowsOutStopWords() throws Exception
    {
        assertAnalyzesTo(analyzer, "potatoes potatoes runs ran horses geese.  I like the cows and in.", new String[] {
                "potato", "potato", "run", "ran", "hors", "gees", "i", "like", "cow" });
    }
}
