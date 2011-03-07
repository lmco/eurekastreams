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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for PrefixedTokenRemoverAndExtractorTokenizer.
 */
public class PrefixedTokenRemoverAndExtractorTokenizerTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Token stream.
     */
    private TokenStream tokenStream;

    /**
     * Reusable token.
     */
    private final Token reusableToken = context.mock(Token.class, "reusableToken");

    /**
     * Test next() with a token that has a prefix and the prefix in the middle of the word.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithPrefixAndMidReplacement() throws IOException
    {
        List<String> extractedKeywords = new ArrayList<String>();
        final Token token1 = new Token("FOObar123fooFOOfoo", 0, "FOObar123fooFOOfoo".length());
        final Token token2 = new Token("hi", 0, "hi".length());

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token1);
        tokens.add(token2);
        tokenStream = new TokenStreamTestHelper(tokens);

        PrefixedTokenRemoverAndExtractorTokenizer sut = new PrefixedTokenRemoverAndExtractorTokenizer(tokenStream,
                "FOO", "#", extractedKeywords);

        assertSame(token2, sut.next(reusableToken));

        assertEquals("hi", token2.term());
        assertEquals(0, token2.startOffset());
        assertEquals("hi".length(), token2.endOffset());

        assertEquals(1, extractedKeywords.size());
        assertEquals("#bar123foo#foo", extractedKeywords.get(0));
    }

    /**
     * Test next() with content that has a prefix but no replacement.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithPrefixButNoReplacement() throws IOException
    {
        List<String> extractedKeywords = new ArrayList<String>();
        final Token token1 = new Token("FOObar123fooFOfoo", 0, "FOObar123fooFOfoo".length());
        final Token token2 = new Token("hi", 0, "hi".length());

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token1);
        tokens.add(token2);
        tokenStream = new TokenStreamTestHelper(tokens);

        PrefixedTokenRemoverAndExtractorTokenizer sut = new PrefixedTokenRemoverAndExtractorTokenizer(tokenStream,
                "FOO", "#", extractedKeywords);

        assertSame(token2, sut.next(reusableToken));

        assertEquals("hi", token2.term());
        assertEquals(0, token2.startOffset());
        assertEquals("hi".length(), token2.endOffset());

        assertEquals(1, extractedKeywords.size());
        assertEquals("#bar123fooFOfoo", extractedKeywords.get(0));
    }

    /**
     * Test next() with content that doesn't have any prefix.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithNoReplacementCharacter() throws IOException
    {
        runTest("FOO", "#", "FObar123fooOOfoo", "FObar123fooOOfoo");
    }

    /**
     * Test next() with content that doesn't have any prefix, but does have a replacement.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithReplacementButNoPrefix() throws IOException
    {
        runTest("FOO", "#", "bar123fooFOOfoo", "bar123foo#foo");
    }

    /**
     * Perform a test with no token left.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithNoToken() throws IOException
    {
        List<String> extractedKeywords = new ArrayList<String>();
        List<Token> tokens = new ArrayList<Token>();
        tokenStream = new TokenStreamTestHelper(tokens);

        PrefixedTokenRemoverAndExtractorTokenizer sut = new PrefixedTokenRemoverAndExtractorTokenizer(tokenStream,
                "FOO", "#", extractedKeywords);
        assertNull(null, sut.next(reusableToken));
    }

    /**
     * Perform a test with an empty token, followed by a valid token.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithEmptyThenValidToken() throws IOException
    {
        List<String> extractedKeywords = new ArrayList<String>();
        final Token token1 = new Token("", 0, 0);
        final Token token2 = new Token("ABCD", 0, "ABCD".length());

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token1);
        tokens.add(token2);
        tokenStream = new TokenStreamTestHelper(tokens);

        PrefixedTokenRemoverAndExtractorTokenizer sut = new PrefixedTokenRemoverAndExtractorTokenizer(tokenStream,
                "FOO", "#", extractedKeywords);

        assertSame(token2, sut.next(reusableToken));

        assertEquals("ABCD", token2.term());
        assertEquals(0, token2.startOffset());
        assertEquals("ABCD".length(), token2.endOffset());
    }

    /**
     * Perform a test with the input parameters.
     * 
     * @param replaceFrom
     *            the text to replace from
     * @param replaceTo
     *            the text to replace to
     * @param input
     *            the token value
     * @param expectedReturn
     *            the expected token text
     * @throws IOException
     *             on error
     */
    private void runTest(final String replaceFrom, final String replaceTo, final String input,
            final String expectedReturn) throws IOException
    {
        final Sequence sequence = context.sequence("sequence-name");

        List<String> extractedKeywords = new ArrayList<String>();
        final Token returnToken = new Token(input, 0, input.length());

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(returnToken);
        tokenStream = new TokenStreamTestHelper(tokens);

        PrefixedTokenRemoverAndExtractorTokenizer sut = new PrefixedTokenRemoverAndExtractorTokenizer(tokenStream,
                replaceFrom, replaceTo, extractedKeywords);

        assertSame(returnToken, sut.next(reusableToken));

        assertEquals(expectedReturn, returnToken.term());
        assertEquals(0, returnToken.startOffset());
        assertEquals(expectedReturn.length(), returnToken.endOffset());
    }
}
