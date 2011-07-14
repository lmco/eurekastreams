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
 * Special filter that helps preserve # and _ for hashtags. There are two modes - literal, and non-literal. Literal mode
 * is meant for searching while non-literal is for indexing. Non-literal mode will add extra versions of the text to
 * allow for searching by different ways. For example... in non-literal (indexing) mode, this will happen: #my_hats ->
 * my, hat, #my_hats
 */
public class HashTagTokenizer extends TokenFilter
{
    /**
     * Collection to store the extracted hashtags.
     */
    private final List<String> extractedHashtags;

    /**
     * Collection to store the extracted non-hashtags that will be subject to more parsing.
     */
    private final List<String> extractedNonHashTags;

    /**
     * The string used to replace hashtags during tokenizing.
     */
    public static final String HASHTAG_TEMPORARY_REPLACEMENT = "xxxhashtagreplacementxxx";

    /**
     * The string used to replace underscores during tokenizing.
     */
    public static final String UNDERSCORE_TEMPORARY_REPLACEMENT = "xxxunderscorereplacementxxx";

    /**
     * Work in literal mode - just restore the #'s and _'s.
     */
    private final boolean literalMode;

    /**
     * Hashtag extractor - same one that's used on the client.
     */
    private final HashTagExtractor hashTagExtractor;

    /**
     * Constructor.
     * 
     * @param inInput
     *            the input
     * @param inExtractedHashtags
     *            list to store the extracted hashtags
     * @param inExtractedNonHashTags
     *            the list to store non-hashtags that had to be extracted - these will be further processed
     * @param inLiteralMode
     *            whether to use literal mode, which just passes the tokens back through after putting back the #'s and
     *            _'s
     */
    public HashTagTokenizer(final TokenStream inInput, final List<String> inExtractedHashtags,
            final List<String> inExtractedNonHashTags, final boolean inLiteralMode)
    {
        super(inInput);
        extractedHashtags = inExtractedHashtags;
        extractedNonHashTags = inExtractedNonHashTags;
        hashTagExtractor = new HashTagExtractor();
        literalMode = inLiteralMode;
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

            String newTokenText = "";

            if (literalMode)
            {
                // this is searching, not indexing, so don't explode the content out
                if (termText.contains("#") || termText.contains("_"))
                {
                    if (!extractedHashtags.contains(termText))
                    {
                        // found a word with an underscore or hash - just pass it through the list to avoid being munged
                        // by later filters - this lets people do #foo#bar or foo_bars if they really want.
                        extractedHashtags.add(termText);
                    }
                }
                else
                {
                    // doesn't start with a # - just pass it through
                    newTokenText = termText;
                }
            }
            else
            {
                // this is indexing, not searching, so expand the text into whatever we might want later

                // use the HashTagExtractor to find a hashtag
                Substring hashTag = hashTagExtractor.extract(termText, 0);

                if (hashTag != null)
                {
                    String hashTagText = hashTag.getContent();
                    if (!extractedHashtags.contains(hashTagText))
                    {
                        // add the parsed hashtag into the list
                        extractedHashtags.add(hashTagText);
                    }
                }

                if (termText.contains("#") || termText.contains("_"))
                {
                    // pass it through the list to avoid being munged by later filters - this lets people do #foo#bar or
                    // hi_there if they really want.
                    if (!extractedHashtags.contains(termText))
                    {
                        extractedHashtags.add(termText);
                    }

                    // split the text on # and _ for indexing
                    String[] parts = termText.split("[#_]");
                    for (int i = 0; i < parts.length; i++)
                    {
                        if (parts[i].length() > 0 && !extractedNonHashTags.contains(parts[i]))
                        {
                            extractedNonHashTags.add(parts[i]);
                        }
                    }
                }
                else
                {
                    // simple word - just pass it through
                    newTokenText = termText;
                }
            }
            nextToken.reinit(newTokenText, 0, newTokenText.length());
        }
        while (nextToken.termLength() == 0);
        return nextToken;
    }
}
