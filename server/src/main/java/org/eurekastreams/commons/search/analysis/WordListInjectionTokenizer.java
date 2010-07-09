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
 * Tokenizer that adds words from an input list.
 */
public class WordListInjectionTokenizer extends TokenFilter
{
    /**
     * Word list to add to the stream.
     */
    private List<String> wordList;

    /**
     * Constructor.
     *
     * @param inWordList
     *            the list of words to add to the token stream
     * @param inInput
     *            the token stream to parse
     */
    public WordListInjectionTokenizer(final List<String> inWordList, final TokenStream inInput)
    {
        super(inInput);
        wordList = inWordList;
    }

    /**
     * Return the next token in the stream, or a word from the list if the next token is null and remove the word from
     * the list. If there's no token and the word list is null, return null.
     *
     * @param reusableToken
     *            the token to reuse if possible
     * @throws IOException
     *             on error
     * @return the next token in the stream, or a word from the list if the next token is null and remove the word from
     *         the list. If there's no token and the word list is null, return null.
     */
    @Override
    public final Token next(final Token reusableToken) throws IOException
    {
        assert reusableToken != null;
        Token token = input.next(reusableToken);
        if (token == null && wordList.size() > 0)
        {
            String word = wordList.get(0);
            wordList.remove(0);
            reusableToken.reinit(word, 0, word.length());
            token = reusableToken;
        }

        return token;
    }
}
