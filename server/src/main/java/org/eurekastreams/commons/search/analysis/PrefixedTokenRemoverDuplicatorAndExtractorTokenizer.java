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

/**
 * Token filter that finds all tokens containing a string, removes it if it's a prefix, stores the item in a list, then
 * passes it through the chain.
 */
public class PrefixedTokenRemoverDuplicatorAndExtractorTokenizer extends TokenFilter
{
    /**
     * Collection to store the extracted keywords.
     */
    private List<String> extractedKeywords;

    /**
     * The prefix to look for.
     */
    private String oldPrefix;

    /**
     * The prefix to replace.
     */
    private String newPrefix;

    /**
     * Constructor.
     *
     * @param inInput
     *            the input
     * @param inOldPrefix
     *            the prefix to look for
     * @param inNewPrefix
     *            the new prefix to apply to the words added into the extracted keywords
     * @param inExtractedKeywords
     *            list to store the extracted keywords
     */
    public PrefixedTokenRemoverDuplicatorAndExtractorTokenizer(final TokenStream inInput, final String inOldPrefix,
            final String inNewPrefix, final List<String> inExtractedKeywords)
    {
        super(inInput);
        oldPrefix = inOldPrefix;
        newPrefix = inNewPrefix;
        extractedKeywords = inExtractedKeywords;
    }

    /**
     * Get the next token, replacing the prefix string with the replacement string, and if the token begins with the
     * string, store it in the extracted keywords list after the replacement, but return the unprefixed term. If finds
     * an empty token, try again. Return null when no tokens remaining.
     *
     * Example - if we're replacing "foo" with "#":
     *
     * - "foobar" --> returns token with "bar", stores "#bar" in extracted keywords list
     *
     * - "foobarfoobar -> returns token with "bar#bar", stores "#bar#bar" in keywords list
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
            if (termText.contains(oldPrefix))
            {
                // the term contains the character we're looking for - replace all occurrences
                String keyword = termText.replace(oldPrefix, newPrefix);

                if (keyword.startsWith(newPrefix))
                {
                    // and it started with the keyword
                    if (!extractedKeywords.contains(keyword))
                    {
                        // check for the special case where the term only contains the prefix
                        if (keyword.replace(newPrefix, "").length() > 0)
                        {
                            extractedKeywords.add(keyword);
                        }
                    }
                    keyword = keyword.substring(newPrefix.length());
                }
                nextToken.reinit(keyword, 0, keyword.length());
            }
        }
        while (nextToken.termLength() == 0);
        return nextToken;
    }
}
