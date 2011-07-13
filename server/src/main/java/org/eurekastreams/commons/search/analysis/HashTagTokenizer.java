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

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.eurekastreams.server.domain.strategies.HashTagExtractor;
import org.eurekastreams.server.domain.strategies.Substring;

/**
 * Tokenizer that helps tokenize hashtags. The input content is assumed to have had octothorpes replaced with
 * HASHTAG_TEMPORARY_REPLACEMENT and underscores replaced with UNDERSCORE_TEMPORARY_REPLACEMENT. A HashtagExtractor is
 * used to pull hashtags out. Those hashtags are stored in the passed-in string collection. After the hashtag is pulled
 * out, the content is split on octothorpe and underscore. The first in the list is set as the token, and the rest are
 * added to the input collection.
 */
public class HashTagTokenizer extends TokenFilter
{
    /**
     * Collection to store the extracted keywords.
     */
    private final List<String> extractedKeywords;

    /**
     * The string used to replace hashtags during tokenizing.
     */
    public static final String HASHTAG_TEMPORARY_REPLACEMENT = "xxxhashtagreplacementxxx";

    /**
     * The string used to replace underscores during tokenizing.
     */
    public static final String UNDERSCORE_TEMPORARY_REPLACEMENT = "xxxunderscorereplacementxxx";

    /**
     * Hashtag extractor - same one that's used on the client.
     */
    private final HashTagExtractor hashTagExtractor;

    /**
     * Constructor.
     * 
     * @param inInput
     *            the input
     * @param inExtractedKeywords
     *            list to store the extracted keywords
     */
    public HashTagTokenizer(final TokenStream inInput, final List<String> inExtractedKeywords)
    {
        super(inInput);
        extractedKeywords = inExtractedKeywords;
        hashTagExtractor = new HashTagExtractor();
    }

    /**
     * Get the next token - remove a pound before any hashtag. Store hashtags in extractedKeywords. Also store any
     * results of splitting on underscores. In that case, don't add the first in the split - return that with the token.
     * 
     * - "#bar" --> returns token with "bar", stores "#bar" in extracted keywords list
     * 
     * - "#bar#snuts -> returns token with "bar", stores "#bar" and "snuts" in keywords list
     * 
     * @param reusableToken
     *            the token to reuse if possible
     * @return the reusable token with the next token - replace all replacement characters and remove it if it's a
     *         prefix
     * @throws IOException
     *             on error
     */
    @Override
    public final Token next(final Token reusableToken) throws IOException
    {
        Token nextToken = null;
        assert reusableToken != null;
        do
        {
            nextToken = input.next(reusableToken);
            if (nextToken == null)
            {
                // no terms left
                return null;
            }

            // there's a term - see if we need to operate on it
            String termText = nextToken.term();

            // put underscores & hashtags back - they've made it past the tokenizing, time to deal with them
            termText = termText.replaceAll(UNDERSCORE_TEMPORARY_REPLACEMENT, "_");
            termText = termText.replaceAll(HASHTAG_TEMPORARY_REPLACEMENT, "#");

            // use the HashTagExtractor to find all of the hashtags
            Substring hashTag = hashTagExtractor.extract(termText, 0);

            if (hashTag != null)
            {
                String hashTagText = hashTag.getContent();
                if (!extractedKeywords.contains(hashTagText))
                {
                    extractedKeywords.add(hashTagText);
                }
            }

            // now split out the # and _ and put the pieces into the extracted keywords list - except the first - that
            // goes with the token
            String newTokenText = "";
            String[] parts = termText.split("[#_]");
            if (parts.length > 0)
            {
                for (int i = 0; i < parts.length; i++)
                {
                    if (parts[i].length() > 0)
                    {
                        if (newTokenText.isEmpty())
                        {
                            newTokenText = parts[i];
                        }
                        else
                        {
                            if (!extractedKeywords.contains(parts[i]))
                            {
                                extractedKeywords.add(parts[i]);
                            }
                        }
                    }
                }
            }

            nextToken.reinit(newTokenText, 0, newTokenText.length());
        }
        while (nextToken.termLength() == 0);
        return nextToken;
    }
}
