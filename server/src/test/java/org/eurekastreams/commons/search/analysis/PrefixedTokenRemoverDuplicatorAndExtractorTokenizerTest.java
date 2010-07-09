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
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for PrefixedTokenRemoverDuplicatorAndExtractorTokenizer.
 */
public class PrefixedTokenRemoverDuplicatorAndExtractorTokenizerTest
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
    private final TokenStream tokenStream = context.mock(TokenStream.class);

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
        runTest("FOO", "#", "FOObar123fooFOOfoo", "bar123foo#foo", "#bar123foo#foo");
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
        runTest("FOO", "#", "FObar123fooOOfoo", "FObar123fooOOfoo", null);
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
        runTest("FOO", "#", "bar123fooFOOfoo", "bar123foo#foo", null);
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
        runTest("FOO", "#", "FOObar123fooFOfoo", "bar123fooFOfoo", "#bar123fooFOfoo");
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
        context.checking(new Expectations()
        {
            {
                one(tokenStream).next(reusableToken);
                will(returnValue(null));
            }
        });

        List<String> extractedKeywords = new ArrayList<String>();
        PrefixedTokenRemoverDuplicatorAndExtractorTokenizer
        // line break
        sut = new PrefixedTokenRemoverDuplicatorAndExtractorTokenizer(tokenStream, "FOO", "#", extractedKeywords);
        assertNull(null, sut.next(reusableToken));

        context.assertIsSatisfied();
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
        final Sequence sequence = context.sequence("sequence-name");
        final Token token1 = new Token("", 0, 0);
        final Token token2 = new Token("FOOsnutsBoo", 0, "FOOsnutsBoo".length());

        context.checking(new Expectations()
        {
            {
                one(tokenStream).next(reusableToken);
                will(returnValue(token1));
                inSequence(sequence);

                one(tokenStream).next(reusableToken);
                will(returnValue(token2));
                inSequence(sequence);
            }
        });

        List<String> extractedKeywords = new ArrayList<String>();
        PrefixedTokenRemoverDuplicatorAndExtractorTokenizer
        // line break
        sut = new PrefixedTokenRemoverDuplicatorAndExtractorTokenizer(tokenStream, "FOO", "#", extractedKeywords);

        assertSame(token2, sut.next(reusableToken));

        assertEquals("snutsBoo", token2.term());
        assertEquals(0, token2.startOffset());
        assertEquals("snutsBoo".length(), token2.endOffset());
        assertEquals(1, extractedKeywords.size());
        assertEquals("#snutsBoo", extractedKeywords.get(0));

        context.assertIsSatisfied();
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
     * @param expectedExtractedKeyword
     *            the keyword extracted
     * @throws IOException
     *             on error
     */
    private void runTest(final String replaceFrom, final String replaceTo, final String input,
            final String expectedReturn, final String expectedExtractedKeyword) throws IOException
    {
        final Token returnToken = new Token(input, 0, input.length());

        context.checking(new Expectations()
        {
            {
                one(tokenStream).next(reusableToken);
                will(returnValue(returnToken));
            }
        });

        List<String> extractedKeywords = new ArrayList<String>();
        PrefixedTokenRemoverDuplicatorAndExtractorTokenizer sut
        // line break
        = new PrefixedTokenRemoverDuplicatorAndExtractorTokenizer(tokenStream, replaceFrom, replaceTo,
                extractedKeywords);

        assertSame(returnToken, sut.next(reusableToken));

        assertEquals(expectedReturn, returnToken.term());
        assertEquals(0, returnToken.startOffset());
        assertEquals(expectedReturn.length(), returnToken.endOffset());

        if (expectedExtractedKeyword == null)
        {
            assertEquals(0, extractedKeywords.size());
        }
        else
        {
            assertEquals(1, extractedKeywords.size());
            assertEquals(expectedExtractedKeyword, extractedKeywords.get(0));
        }

        context.assertIsSatisfied();
    }
}
