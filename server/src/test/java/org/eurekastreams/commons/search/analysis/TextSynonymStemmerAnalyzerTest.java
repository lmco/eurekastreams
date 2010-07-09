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
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test fixture for the TextSynonymStemmerAnalyzer class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-search-test.xml" })
public class TextSynonymStemmerAnalyzerTest extends AnalyzerTestBase
{
    /**
     * System under test.
     */
    private TextSynonymStemmerAnalyzer analyzer = new TextSynonymStemmerAnalyzer();

    /**
     * Test analyzing a single word.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public void testSingleWord() throws Exception
    {
        assertAnalyzesTo(analyzer, "hello?!?!?", new String[] { "hello", "hi", "howdi", "hullo" });
        assertAnalyzesTo(analyzer, "fork?!?!?", new String[] { "fork", "branch", "branch", "crotch", "fork", "furcat",
                "pitchfork", "ramif", "ramifi", "separ" });
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
        assertAnalyzesTo(analyzer, "hello monkey potato fork...fork?!?!?", new String[] { "hello", "hi", "howdi",
                "hullo", "monkey", "fiddl", "imp", "potter", "putter", "rapscallion", "rascal", "scalawag",
                "scallywag", "scamp", "tamper", "tinker", "potato", "murphi", "spud", "tater", "fork", "branch",
                "branch", "crotch", "fork", "furcat", "pitchfork", "ramif", "ramifi", "separ", "fork", "branch",
                "branch", "crotch", "fork", "furcat", "pitchfork", "ramif", "ramifi", "separ" });
    }

    /**
     * Test the analyzer does not remove HTML tags, resulting in "p" and
     * "phosphorus".
     *
     * @throws Exception
     *             on error
     */
    @Test
    public void testDoesNotParseHtmlTags() throws Exception
    {
        assertAnalyzesTo(analyzer, "<p>hello monkey potato fork,,fork</p>", new String[] { "p", "phosphorus", "hello",
                "hi", "howdi", "hullo", "monkey", "fiddl", "imp", "potter", "putter", "rapscallion", "rascal",
                "scalawag", "scallywag", "scamp", "tamper", "tinker", "potato", "murphi", "spud", "tater", "fork",
                "branch", "branch", "crotch", "fork", "furcat", "pitchfork", "ramif", "ramifi", "separ", "fork",
                "branch", "branch", "crotch", "fork", "furcat", "pitchfork", "ramif", "ramifi", "separ", "p",
                "phosphorus" });
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
        assertAnalyzesTo(analyzer, "Paris in the the spring.", new String[] { "pari", "spring", "bounc", "bound",
                "form", "fountain", "give", "jump", "leap", "leap", "outflow", "outpour", "rebound", "recoil", "resil",
                "reverber", "ricochet", "saltat", "springi", "springtim" });
    }
}
