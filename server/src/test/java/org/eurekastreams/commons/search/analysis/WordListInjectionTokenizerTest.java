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

/**
 * Test fixture for WordListInjectionTokenizer.
 */
public class WordListInjectionTokenizerTest
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
     * Test next() with a null token and empty word list.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithNullTokenAndEmptyWordList() throws IOException
    {
        final Token token = new Token(null, 0, 0);
        List<Token> tokens = new ArrayList<Token>();
        tokenStream = new TokenStreamTestHelper(tokens);

        final List<String> wordsToInject = new ArrayList<String>();
        WordListInjectionTokenizer sut = new WordListInjectionTokenizer(wordsToInject, tokenStream);

        assertNull(sut.next(token));
    }

    /**
     * Test next() with a non-null token and no words in the list.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithNonNullTokenAndNoWordList() throws IOException
    {
        final Token token = new Token("foo", 0, 3);
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token);
        tokenStream = new TokenStreamTestHelper(tokens);

        final List<String> wordsToInject = new ArrayList<String>();
        WordListInjectionTokenizer sut = new WordListInjectionTokenizer(wordsToInject, tokenStream);

        assertSame(token, sut.next(token));
        assertEquals("foo", token.term());
        assertEquals(0, token.startOffset());
        assertEquals(3, token.endOffset());
    }

    /**
     * Test next() with a non-null token and words in the list.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithNonNullTokenAndWordsInList() throws IOException
    {
        final Token token = new Token("foo", 0, 3);
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token);
        tokenStream = new TokenStreamTestHelper(tokens);

        final List<String> wordsToInject = new ArrayList<String>();
        wordsToInject.add("FOOOO");
        WordListInjectionTokenizer sut = new WordListInjectionTokenizer(wordsToInject, tokenStream);

        assertSame(token, sut.next(token));
        assertEquals("foo", token.term());
        assertEquals(0, token.startOffset());
        assertEquals(3, token.endOffset());
        assertEquals(1, wordsToInject.size());
    }

    /**
     * Test next() with null token and a word in the list.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void testNextWithNullTokenAndWordInList() throws IOException
    {
        final Token token = new Token(null, 0, 0);
        List<Token> tokens = new ArrayList<Token>();
        tokenStream = new TokenStreamTestHelper(tokens);

        final List<String> wordsToInject = new ArrayList<String>();
        wordsToInject.add("FOOOO");
        WordListInjectionTokenizer sut = new WordListInjectionTokenizer(wordsToInject, tokenStream);

        assertSame(token, sut.next(token));
        assertEquals("FOOOO", token.term());
        assertEquals(0, token.startOffset());
        assertEquals("FOOOO".length(), token.endOffset());
        assertEquals(0, wordsToInject.size());
    }
}
