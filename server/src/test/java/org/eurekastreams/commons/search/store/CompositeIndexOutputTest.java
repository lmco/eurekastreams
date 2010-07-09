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
package org.eurekastreams.commons.search.store;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.store.IndexOutput;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the CompositeIndexOutput class. Assert that all write
 * operations are performed on both Directories, all read operations on the
 * fast-read Directory.
 */
public class CompositeIndexOutputTest
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
     * Mock representing the fast-read Directory.
     */
    private IndexOutput fastReadIndexOutputMock = context.mock(
            IndexOutput.class, "fastReadIndexOutputMock");

    /**
     * Mock representing the persistent Directory.
     */
    private IndexOutput persistentIndexOutputMock = context.mock(
            IndexOutput.class, "persistentIndexOutputMock");

    /**
     * System under test.
     */
    private CompositeIndexOutput sut;

    /**
     * Setup method - instantiate the SUT.
     */
    @Before
    public void setup()
    {
        sut = new CompositeIndexOutput(fastReadIndexOutputMock,
                persistentIndexOutputMock);
    }

    /**
     * Assert that closing the CompositeInputOutput calls close on both of the
     * managed InputOutputs.
     *
     * @throws IOException
     *             on I/O error.
     */
    @Test
    public void testClose() throws IOException
    {
        context.checking(new Expectations()
        {
            {
                one(fastReadIndexOutputMock).close();
                one(persistentIndexOutputMock).close();
            }
        });

        // call the system under test
        sut.close();

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert flushing the CompositeInputOutput calls flush() on both of the
     * managed InputOutputs.
     *
     * @throws IOException
     *             on I/O error
     */
    @Test
    public void testFlush() throws IOException
    {
        context.checking(new Expectations()
        {
            {
                one(fastReadIndexOutputMock).flush();
                one(persistentIndexOutputMock).flush();
            }
        });

        // call the system under test
        sut.flush();

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert getFilePointer asks the fast-read Directory for the value.
     */
    @Test
    public void testGetFilePointer()
    {
        final long filePointer = 48382L;
        context.checking(new Expectations()
        {
            {
                one(fastReadIndexOutputMock).getFilePointer();
                will(returnValue(filePointer));
            }
        });

        // call the system under test
        assertEquals(filePointer, sut.getFilePointer());

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert length() asks the fast-read Directory for the value.
     *
     * @throws IOException
     *             on I/O error
     */
    @Test
    public void testLength() throws IOException
    {
        final long len = 233832L;
        context.checking(new Expectations()
        {
            {
                one(fastReadIndexOutputMock).length();
                will(returnValue(len));
            }
        });

        // call the system under test
        assertEquals(len, sut.length());

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert seek(pos) passes through to both managed Directories.
     *
     * @throws IOException
     *             on I/O error
     */
    @Test
    public void testSeek() throws IOException
    {
        final long pos = 3829L;
        context.checking(new Expectations()
        {
            {
                one(fastReadIndexOutputMock).seek(pos);
                one(persistentIndexOutputMock).seek(pos);
            }
        });

        // call the system under test
        sut.seek(pos);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert writeByte(byte) passes through to both managed Directories.
     *
     * @throws IOException
     *             on I/O error
     */
    @Test
    public void testWriteByte() throws IOException
    {
        final byte data = 'a';
        context.checking(new Expectations()
        {
            {
                one(fastReadIndexOutputMock).writeByte(data);
                one(persistentIndexOutputMock).writeByte(data);
            }
        });

        // call the system under test
        sut.writeByte(data);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert writeBytes(byte,offset,length) passes through to both managed
     * Directories.
     *
     * @throws IOException
     *             on I/O error
     */
    @Test
    public void testWriteBytes() throws IOException
    {
        final byte[] data = { 'a', 'c', 'd', 'c', 'b', 'a', 'g' };
        final int offset = 0;
        final int len = 1;
        context.checking(new Expectations()
        {
            {
                one(fastReadIndexOutputMock).writeBytes(data, offset, len);
                one(persistentIndexOutputMock).writeBytes(data, offset, len);
            }
        });

        // call the system under test
        sut.writeBytes(data, offset, len);

        // all expectations met?
        context.assertIsSatisfied();
    }
}
