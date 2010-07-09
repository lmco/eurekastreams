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
package org.eurekastreams.commons.search.analysis;

import org.junit.Test;

/**
 * Test fixture for the TextStemmerAnalyzer class.
 */
public class TextStemmerAnalyzerTest extends AnalyzerTestBase
{
    /**
     * System under test.
     */
    private TextStemmerAnalyzer analyzer = new TextStemmerAnalyzer();

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
