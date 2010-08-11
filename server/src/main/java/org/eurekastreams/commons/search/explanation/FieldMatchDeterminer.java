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
package org.eurekastreams.commons.search.explanation;

import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.eurekastreams.commons.search.modelview.FieldMatch;

/**
 * Class to determine how a search result matched.
 */
public class FieldMatchDeterminer
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.getLog(FieldMatchDeterminer.class);

    /**
     * The fields to analyze.
     */
    private List<String> fieldsToAnalyze;

    /**
     * The analyzer to use to parse the query string.
     */
    private Analyzer searchAnalyzer;

    /**
     * Set the field names to analyze.
     *
     * @param inFieldsToAnalyze
     *            the field names to analyze.
     */
    public void setFieldsToAnalyze(final List<String> inFieldsToAnalyze)
    {
        fieldsToAnalyze = inFieldsToAnalyze;
    }

    /**
     * Set the search analyzer to use to parse the query.
     *
     * @param inSearchAnalyzer
     *            the search analyzer used to parse the query
     */
    public void setSearchAnalyzer(final Analyzer inSearchAnalyzer)
    {
        searchAnalyzer = inSearchAnalyzer;
    }

    /**
     * Parse the input Explanation string to find which of the input search keywords matched with the input Analyzer.
     *
     * @param inExplanationText
     *            the Explanation text returned from the search
     * @param searchText
     *            the search string the user typed
     * @return A Map with the keys representing the fields to analyze and the values as a list of keywords that the user
     *         typed as search parameters that matched the corresponding keyword.
     */
    public FieldMatch determineFieldMatches(final String inExplanationText, final String searchText)
    {
        String explanationText = inExplanationText;

        FieldMatch matchedKeywords = new FieldMatch();
        if (fieldsToAnalyze.size() == 0)
        {
            return matchedKeywords;
        }

        log.debug("Explanation:" + explanationText);

        // Remove the boost values, makes things easier...
        Pattern boostPattern = Pattern.compile("\\^[0-9]+.[0-9]+");
        Matcher boostPatternMatcher = boostPattern.matcher(explanationText);
        explanationText = boostPatternMatcher.replaceAll("");

        // convert the keywords to the analyzed form, then store them in a hashtable of <tokenizedForm, originalKeyword>
        Map<String, String> tokenizedKeywords = tokenizeKeywords(searchText);

        // We now have a Map with the tokenized keyword as the key, the original search word as the value.
        // Start looking through the explanation for the values
        for (String fieldName : fieldsToAnalyze)
        {
            Pattern weightPattern = Pattern.compile("\\sweight\\(" + fieldName + ":(\\w+)\\s",
                    java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.MULTILINE);
            Matcher m = weightPattern.matcher(explanationText);
            boolean result = m.find();
            while (result)
            {
                matchedKeywords.addMatch(fieldName, tokenizedKeywords.get(m.group(1)));
                result = m.find();
            }
        }
        return matchedKeywords;
    }

    /**
     * Tokenize the input search text using the passed-in Analyzer.
     *
     * @param searchText
     *            the search text to parse
     * @return a Map of tokenized-term -> original term
     */
    private Map<String, String> tokenizeKeywords(final String searchText)
    {
        Map<String, String> tokenizedKeywords = new Hashtable<String, String>();
        String[] keywords = searchText.split(" ");
        TokenStream stream;
        for (String keyword : keywords)
        {
            stream = searchAnalyzer.tokenStream(null, new StringReader(keyword));
            Token token = new Token();
            try
            {
                while ((token = stream.next(token)) != null)
                {
                    String tokenizedKeyword = token.term();
                    log.info("Tokenized keyword: " + tokenizedKeyword);
                    tokenizedKeywords.put(tokenizedKeyword, keyword);
                }
            }
            catch (IOException e)
            {
                // do nothing, see if we can continue
                log.error("Error tokenizing the search keyword for explanation: " + keyword, e);
            }
        }
        return tokenizedKeywords;
    }
}
