/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests MessageContentExtractor.
 */
public class MessageContentExtractorTest
{
    /** Test data. */
    private static final String BASIC_MARKER = "\r\nFrom: ";

    /** Test data. */
    private static final String REGEX_MARKER = "\r\n-+\\s*Original Message\\s*-+\r\n";

    /** Test data. */
    private static final String TEXT_TO_FIND = "This is the text to find.";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Used for making unique mock names. */
    private int uniqueIndex = 0;

    /** SUT. */
    private MessageContentExtractor sut;

    /**
     * Helper method: builds a multipart email part.
     *
     * @param topLevel
     *            If part should be a Message.
     * @param parts
     *            Child parts to include.
     * @return Multipart part.
     * @throws MessagingException
     *             Won't.
     * @throws IOException
     *             Won't.
     */
    private Part makeMultipart(final boolean topLevel, final Part... parts) throws MessagingException, IOException
    {
        final Part bp = context.mock((topLevel ? Message.class : BodyPart.class), "BodyPart-" + (++uniqueIndex));
        final Multipart mp = new Multipart()
        {
            @Override
            public void writeTo(final OutputStream inOs) throws IOException, MessagingException
            {
            }
        };
        context.checking(new Expectations()
        {
            {
                allowing(bp).getDisposition();
                will(returnValue(null));
                allowing(bp).getContentType();
                will(returnValue("multipart/alternative;\r\nboundary=something"));
                allowing(bp).getContent();
                will(returnValue(mp));
            }
        });
        for (int i = 0; i < parts.length; i++)
        {
            mp.addBodyPart((BodyPart) parts[i]);
        }
        return bp;
    }

    /**
     * Helper method: builds a content email part.
     *
     * @param type
     *            MIME type string.
     * @param isAttachment
     *            If part holds an attachment (vs inline content).
     * @param content
     *            The content.
     * @return The part.
     * @throws MessagingException
     *             Won't.
     * @throws IOException
     *             Won't.
     */
    private Part makeContentPart(final String type, final boolean isAttachment, final Object content)
            throws MessagingException, IOException
    {
        final Part bp = context.mock(BodyPart.class, "BodyPart-" + (++uniqueIndex));
        context.checking(new Expectations()
        {
            {
                allowing(bp).getDisposition();
                will(returnValue(isAttachment ? "attachment" : null));
                allowing(bp).getContentType();
                will(returnValue(type));
                allowing(bp).getContent();
                will(returnValue(content));
            }
        });
        return bp;
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
    }

    /**
     * Tests a successful search and parse.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testSuccess() throws MessagingException, IOException
    {
        sut = new MessageContentExtractor(Collections.singletonList(BASIC_MARKER), null);
        Message msg = (Message) makeMultipart(
                true,
                makeContentPart("text/plain", true, "Not this"),
                makeContentPart("text/plain", false, "  \t\r\n \t \r\nFrom: blah"),
                makeMultipart(
                        false,
                        makeContentPart("text/plain", true, ""),
                        makeContentPart("image/png", false, new byte[12]),
                        makeContentPart("text/plain", true, "From: xyz"),
                        makeMultipart(false, makeContentPart("text/html", false, "<head></head>"),
                                makeContentPart("text/plain", false, TEXT_TO_FIND)),
                        makeContentPart("text/plain", false, "Not this")),
                makeContentPart("text/plain", false, "Not this either"),
                makeContentPart("text/html", false, "<p>Not this</p>"));

        String result = sut.extract(msg);
        context.assertIsSatisfied();
        assertEquals(TEXT_TO_FIND, result);
    }

    /**
     * Tests a search and parse yielding no applicable content.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testNoneFound() throws MessagingException, IOException
    {
        sut = new MessageContentExtractor(Collections.singletonList(BASIC_MARKER), null);
        Message msg = (Message) makeMultipart(
                true,
                makeContentPart("text/plain", true, "Not this"),
                makeContentPart("text/plain", false, "  \t\r\n \t \r\nFrom: blah"),
                makeMultipart(false, makeContentPart("text/plain", true, ""),
                        makeContentPart("image/png", false, new byte[12]),
                        makeContentPart("text/plain", true, "From: xyz"),
                        makeMultipart(false, makeContentPart("text/html", false, "<head></head>"))),
                makeContentPart("text/html", false, "<p>Not this</p>"));

        String result = sut.extract(msg);
        context.assertIsSatisfied();
        assertNull(result);
    }

    /**
     * Tests parsing: 'From' must be at start of line to be a header.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testParse1() throws MessagingException, IOException
    {
        sut = new MessageContentExtractor(Collections.singletonList(BASIC_MARKER), null);
        Message msg = (Message) makeMultipart(
                true,
                makeContentPart("text/plain", false,
                        " From: A\r\n\tFrom: B\r\n  \t\t  From: C\r\n\r\n From: D\r\nFrom: E"));
        String result = sut.extract(msg);
        context.assertIsSatisfied();
        assertEquals("From: A\r\n\tFrom: B\r\n  \t\t  From: C\r\n\r\n From: D", result);
    }

    /**
     * Tests parsing: "Original Message" check.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testParse2() throws MessagingException, IOException
    {
        sut = new MessageContentExtractor(Collections.singletonList(BASIC_MARKER),
                Collections.singletonList(REGEX_MARKER));
        Message msg = (Message) makeMultipart(
                true,
                makeContentPart("text/plain", false,
                        " From: A\r\n\tFrom: B\r\n-----Original Message-----\r\nFrom: C\r\n\r\n From: D\r\nFrom: E"));
        String result = sut.extract(msg);
        context.assertIsSatisfied();
        assertEquals("From: A\r\n\tFrom: B", result);
    }

    /**
     * Common parts of several parsing tests.
     *
     * @param input
     *            String to parse.
     * @param expected
     *            Expected extraction.
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    private void commonParseTest(final String input, final String expected) throws MessagingException, IOException
    {
        sut = new MessageContentExtractor(Arrays.asList("abc", "def"), Arrays.asList("\\d+", "mo+"));
        Message msg = (Message) makeMultipart(true, makeContentPart("text/plain", false, input));
        String result = sut.extract(msg);
        context.assertIsSatisfied();
        assertEquals(expected, result);
    }

    /**
     * Tests parsing.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testParse3() throws MessagingException, IOException
    {
        commonParseTest("asdf jklabcghi111", "asdf jkl");
    }

    /**
     * Tests parsing.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testParse4() throws MessagingException, IOException
    {
        commonParseTest("asdf MOOOOjklabcghi111", "asdf");
    }

    /**
     * Tests parsing.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testParse5() throws MessagingException, IOException
    {
        commonParseTest("asdf jdefklabcghi111mo", "asdf j");
    }

    /**
     * Tests parsing.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testParse6() throws MessagingException, IOException
    {
        commonParseTest("asdf jklabbcghi@@", "asdf jklabbcghi@@");
    }

    /**
     * Tests null lists.
     *
     * @throws IOException
     *             Won't.
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testCtor() throws MessagingException, IOException
    {
        sut = new MessageContentExtractor(null, null);
        Message msg = (Message) makeMultipart(true,
                makeContentPart("text/plain", false, "stuff\r\nmore\r\nDate: 1/1/11"));
        String result = sut.extract(msg);
        context.assertIsSatisfied();
        assertEquals("stuff\r\nmore\r\nDate: 1/1/11", result);
    }

}
