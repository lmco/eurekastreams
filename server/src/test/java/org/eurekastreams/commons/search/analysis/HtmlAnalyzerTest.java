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
 * Test fixture for HtmlAnalyzer.
 */
public class HtmlAnalyzerTest extends AnalyzerTestBase
{
    /**
     * System under test.
     */
    private HtmlAnalyzer analyzer = new HtmlAnalyzer();

    /**
     * Perform a simple Analyzer test with words that don't stem.
     *
     * @throws Exception
     *             on error
     */
    @Test
    public void testSimpleParse() throws Exception
    {
        String html = "<foo>hello</foo> horse <a href=\"http://google.com/heynow\">monkey <B>potato</b></a>";
        html += " <STRONG>fork,,fork</strong>";
        assertAnalyzesTo(analyzer, html, new String[] { "hello", "horse", "monkey", "potato", "fork", "fork" });
    }
}
