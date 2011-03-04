/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

/**
 * Helper class, returning a list of tokens in order, then null.
 */
public class TokenStreamTestHelper extends TokenStream
{
    /**
     * The tokens to return.
     */
    private List<Token> tokens;

    /**
     * Constructor.
     * 
     * @param inTokens
     *            the tokens to return
     */
    public TokenStreamTestHelper(final List<Token> inTokens)
    {
        tokens = inTokens;
    }

    /**
     * Get the next token in the list, or null.
     * 
     * @param inToken
     *            ignored
     * @return the token
     * @see org.apache.lucene.analysis.TokenStream#next(org.apache.lucene.analysis.Token)
     */
    @Override
    public Token next(final Token inToken)
    {
        if (tokens.size() == 0)
        {
            return null;
        }
        return tokens.remove(0);
    }
}
