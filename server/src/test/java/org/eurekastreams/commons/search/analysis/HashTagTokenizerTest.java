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
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Test fixture for HashTagTokenizer.
 */
public class HashTagTokenizerTest
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
        List<String> expectedList = new ArrayList<String>();
        expectedList.add("#bar123foo");
        expectedList.add("foo");
        runTest("#bar123foo#foo", "bar123foo", expectedList);
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
        runTest("FObar123fooOOfoo", "FObar123fooOOfoo", null);
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
        List<String> expectedList = new ArrayList<String>();
        expectedList.add("foo");
        runTest("bar123foo#foo", "bar123foo", expectedList);
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
        runTest("#bar123fooFOfoo", "bar123fooFOfoo", Collections.singletonList("#bar123fooFOfoo"));
    }

    /**
     * Test next() with content that has a prefix but no replacement.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithUnderscoreReplacment() throws IOException
    {
        List<String> expectedExtractedKeywords = new ArrayList<String>();
        expectedExtractedKeywords.add("#bar123_fooFOfoo");
        expectedExtractedKeywords.add("fooFOfoo");
        runTest("#bar123_fooFOfoo", "bar123", expectedExtractedKeywords);
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
        List<Token> tokens = new ArrayList<Token>();
        tokenStream = new TokenStreamTestHelper(tokens);

        List<String> extractedKeywords = new ArrayList<String>();
        HashTagTokenizer sut = new HashTagTokenizer(tokenStream, extractedKeywords);
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
        final Token token1 = new Token("", 0, 0);
        final Token token2 = new Token(HashTagTokenizer.HASHTAG_TEMPORARY_REPLACEMENT + "snutsBoo", 0,
                (HashTagTokenizer.HASHTAG_TEMPORARY_REPLACEMENT + "snutsBoo").length());
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token1);
        tokens.add(token2);
        tokenStream = new TokenStreamTestHelper(tokens);

        List<String> extractedKeywords = new ArrayList<String>();
        HashTagTokenizer sut = new HashTagTokenizer(tokenStream, extractedKeywords);

        assertSame(token2, sut.next(reusableToken));

        assertEquals("snutsBoo", token2.term());
        assertEquals(0, token2.startOffset());
        assertEquals("snutsBoo".length(), token2.endOffset());
        assertEquals(1, extractedKeywords.size());
        assertEquals("#snutsBoo", extractedKeywords.get(0));
    }

    /**
     * Perform a test with the input parameters.
     * 
     * @param input
     *            the token value
     * @param expectedReturn
     *            the expected token text
     * @param expectedExtractedKeywords
     *            the keywords extracted
     * @throws IOException
     *             on error
     */
    private void runTest(final String input, final String expectedReturn, final List<String> expectedExtractedKeywords)
            throws IOException
    {
        String text = input.replace("#", HashTagTokenizer.HASHTAG_TEMPORARY_REPLACEMENT);
        text = input.replace("_", HashTagTokenizer.UNDERSCORE_TEMPORARY_REPLACEMENT);

        System.out.println("TEXT: " + text);

        final Token returnToken = new Token(text, 0, text.length());
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(returnToken);
        tokenStream = new TokenStreamTestHelper(tokens);

        List<String> extractedKeywords = new ArrayList<String>();
        HashTagTokenizer sut = new HashTagTokenizer(tokenStream, extractedKeywords);

        assertSame(returnToken, sut.next(reusableToken));

        assertEquals(expectedReturn, returnToken.term());
        assertEquals(0, returnToken.startOffset());
        assertEquals(expectedReturn.length(), returnToken.endOffset());

        if (expectedExtractedKeywords == null)
        {
            assertEquals(0, extractedKeywords.size());
        }
        else
        {
            assertEquals(expectedExtractedKeywords.size(), extractedKeywords.size());
            for (int i = 0; i < expectedExtractedKeywords.size(); i++)
            {
                assertEquals(expectedExtractedKeywords.get(i), extractedKeywords.get(i));
            }
        }
    }
}
