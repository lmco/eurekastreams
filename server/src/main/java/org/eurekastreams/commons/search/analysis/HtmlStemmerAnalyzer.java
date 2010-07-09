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

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.solr.analysis.EnglishPorterFilterFactory;
import org.apache.solr.analysis.HTMLStripStandardTokenizerFactory;

/**
 * Analyzer that parses out HTML tags, then indexes lower-cased stems of the
 * significant words in the text.
 */
public class HtmlStemmerAnalyzer extends Analyzer
{
    /**
     * Tokenize the stream.
     *
     * @param fieldName
     *            the name of the field
     * @param reader
     *            the reader
     * @return the stream
     */
    @Override
    public TokenStream tokenStream(final String fieldName, final Reader reader)
    {
        TokenStream tokenStream = new HTMLStripStandardTokenizerFactory().create(reader);
        TokenStream result = new StandardFilter(tokenStream);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS);
        result = new EnglishPorterFilterFactory().create(result);
        return result;
    }
}
