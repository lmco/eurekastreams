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

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.solr.analysis.EnglishPorterFilterFactory;

/**
 * Text stemmer analyzer for indexing content with hashtags. Hashtags are preserved and indexed, and the words that are
 * prefixed with them are indexed as both the hashtagged version and non-hashed version.
 */
public class HashTagTextStemmerIndexingAnalyzer extends Analyzer
{
    /**
     * Temporary replacement for hashtags during tokenizing, since '#' is split on and removed by standard tokenizer.
     */
    private static final String INDEXED_HASHTAG_PREFIX = "bbindexedhashtagprefixbb";

    /**
     * Temporary replacement to preserve underscores in hashtags.
     */
    private static final String HASHTAG_UNDERSCORE_REPLACEMENT = "bbunderscorehashtagreplacement";

    /**
     * Tokenize the stream.
     * 
     * @param fieldName
     *            the name of the field
     * @param inReader
     *            the reader
     * @return the stream
     */
    @Override
    public TokenStream tokenStream(final String fieldName, final Reader inReader)
    {
        // collection to hold hashtagged keywords
        List<String> hashTaggedKeywords = new ArrayList<String>();

        // this reader will replace all hashtags with our marker text
        Reader reader = CharacterReplacementStreamBuilder.buildReplacementReader(inReader, '#',
                HashTagTokenizer.HASHTAG_TEMPORARY_REPLACEMENT);
        reader = CharacterReplacementStreamBuilder.buildReplacementReader(reader, '_',
                HashTagTokenizer.UNDERSCORE_TEMPORARY_REPLACEMENT);
        TokenStream result = new StandardTokenizer(reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);

        result = new HashTagTokenizer(result, hashTaggedKeywords);
        result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS);
        result = new EnglishPorterFilterFactory().create(result);
        result = new WordListInjectionTokenizer(hashTaggedKeywords, result);

        return result;
    }
}
