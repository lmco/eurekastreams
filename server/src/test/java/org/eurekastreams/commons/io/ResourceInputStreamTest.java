/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ResourceInputStream.
 */
public class ResourceInputStreamTest
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
     * The system under test.
     */
    private ResourceInputStream sut;

    /**
     * Mocked InputStream to wrap.
     */
    private InputStream inputStreamMock = context.mock(InputStream.class);

    /**
     * Setup the SUT.
     */
    @Before
    public void setUp()
    {
        sut = new ResourceInputStream(inputStreamMock);
    }

    /**
     * Test available() passes through to the underlying InputStream.
     *
     * @throws IOException
     *             on error
     */
    @Test
    public void availableTest() throws IOException
    {
        final int availableBytes = 3822;

        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).available();
                will(returnValue(availableBytes));
            }
        });

        // invoke
        int result = sut.available();

        // all expectations met?
        context.assertIsSatisfied();

        assertEquals(availableBytes, result);
    }

    /**
     * Test close() passes through to the underlying InputStream.
     *
     * @throws IOException
     *             on error
     */
    @Test
    public void closeTest() throws IOException
    {
        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).close();
            }
        });

        // invoke
        sut.close();

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test mark() passes through to the underlying InputStream.
     */
    @Test
    public void markTest()
    {
        final int readLimit = 1323;

        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).mark(readLimit);
            }
        });

        // invoke
        sut.mark(readLimit);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test markSupportedTest() passes through to the underlying InputStream.
     */
    @Test
    public void markSupportedTest()
    {
        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).markSupported();
                will(returnValue(true));
            }
        });

        // invoke
        assertTrue(sut.markSupported());

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test read() passes through to the underlying InputStream.
     *
     * @throws IOException
     *             on error
     */
    @Test
    public void readTest() throws IOException
    {
        final int readValue = 23324;

        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).read();
                will(returnValue(readValue));
            }
        });

        // invoke
        assertEquals(readValue, sut.read());

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test read(byte[], ) passes through to the underlying InputStream.
     *
     * @throws IOException
     *             on error
     */
    @Test
    public void readTestBytes() throws IOException
    {
        final byte[] bytes = { 'a', 'b' };
        final int readValue = 23324;

        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).read(bytes);
                will(returnValue(readValue));
            }
        });

        // invoke
        assertEquals(readValue, sut.read(bytes));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test read(byte[], offset, length) passes through to the underlying
     * InputStream.
     *
     * @throws IOException
     *             on error
     */
    @Test
    public void readTestBytesOffLength() throws IOException
    {
        final byte[] bytes = { 'a', 'b' };
        final int offset = 2339;
        final int length = 23;
        final int readValue = 23324;

        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).read(bytes, offset, length);
                will(returnValue(readValue));
            }
        });

        // invoke
        assertEquals(readValue, sut.read(bytes, offset, length));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test reset() passes through to the underlying InputStream.
     *
     * @throws IOException
     *             on error
     */
    @Test
    public void reset() throws IOException
    {
        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).reset();
            }
        });

        // invoke
        sut.reset();

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test skip() passes through to the underlying InputStream.
     *
     * @throws IOException
     *             on error
     */
    @Test
    public void skip() throws IOException
    {
        final long skip = 2323L;

        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(inputStreamMock).skip(skip);
            }
        });

        // invoke
        sut.skip(skip);

        // all expectations met?
        context.assertIsSatisfied();
    }
}
