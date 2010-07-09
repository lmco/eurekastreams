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
 * Test fixture for the HtmlSynonymStemmerAnalyzer class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-search-test.xml" })
public class HtmlSynonymStemmerAnalyzerTest extends AnalyzerTestBase
{
    /**
     * System under test.
     */
    private HtmlSynonymStemmerAnalyzer analyzer = new HtmlSynonymStemmerAnalyzer();

    /**
     * Test a single word parsing.
     *
     * @throws Exception
     *             on error
     */
    @Test
    public void testSingleWord() throws Exception
    {
        assertAnalyzesTo(analyzer, "<a href=\"http://google.com\">hello</a>?!?!?", new String[] { "hello", "hi",
                "howdi", "hullo" });
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
        assertAnalyzesTo(analyzer,
                "<b>hello</B> <uL><li>monkey</li><li>potato</li></ul> <STronG>fork</Strong>...<lame>fork</lame>?!?!?",
                new String[] { "hello", "hi", "howdi", "hullo", "monkey", "fiddl", "imp", "potter", "putter",
                        "rapscallion", "rascal", "scalawag", "scallywag", "scamp", "tamper", "tinker", "potato",
                        "murphi", "spud", "tater", "fork", "branch", "branch", "crotch", "fork", "furcat", "pitchfork",
                        "ramif", "ramifi", "separ", "fork", "branch", "branch", "crotch", "fork", "furcat",
                        "pitchfork", "ramif", "ramifi", "separ" });
    }

    /**
     * Test the analyzer does remove HTML tags, resulting in no "p" nor
     * "phosphorus".
     *
     * @throws Exception
     *             on error
     */
    @Test
    public void testDoesParseHtmlTags() throws Exception
    {
        assertAnalyzesTo(analyzer, "<p>hello monkey <TABLE>potato</table> fork,,fork</p>", new String[] { "hello",
                "hi", "howdi", "hullo", "monkey", "fiddl", "imp", "potter", "putter", "rapscallion", "rascal",
                "scalawag", "scallywag", "scamp", "tamper", "tinker", "potato", "murphi", "spud", "tater", "fork",
                "branch", "branch", "crotch", "fork", "furcat", "pitchfork", "ramif", "ramifi", "separ", "fork",
                "branch", "branch", "crotch", "fork", "furcat", "pitchfork", "ramif", "ramifi", "separ" });
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
        assertAnalyzesTo(analyzer,
                "<ul>Paris <img alt=\"foo!\" src=\"http://foo.com/bar.jpg\"/>in the the spring.</ul>", new String[] {
                        "pari", "spring", "bounc", "bound", "form", "fountain", "give", "jump", "leap", "leap",
                        "outflow", "outpour", "rebound", "recoil", "resil", "reverber", "ricochet", "saltat",
                        "springi", "springtim" });
    }
}
