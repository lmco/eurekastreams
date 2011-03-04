/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
