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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the CompositeDirectory. The CompositeDirectory does little
 * more than make pass through the Directory operations to the appropriate
 * sub-directory. All write operations must go to both, all read operations must
 * go to the fast-read directory. These tests assert that the CompositeDirectory
 * is properly passing through these operations to the proper sub-directories.
 */
public class CompositeDirectoryTest
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
     * Mocked fast-read Directory for testing.
     */
    private Directory fastReadDirectory = context.mock(Directory.class,
            "fastReadDirectory");

    /**
     * Mocked FSDirectory for testing.
     */
    private Directory persistentDirectory = context.mock(Directory.class,
            "persistentDirectory");

    /**
     * System Under Test.
     */
    private CompositeDirectory sut;

    /**
     * Setup method, creating the CompositeDirectory.
     */
    @Before
    public void setup()
    {
        sut = new CompositeDirectory(fastReadDirectory, persistentDirectory);
    }

    /**
     * Assert that closing the composite directory closes each of the contained
     * Directories.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testclose() throws IOException
    {
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).close();
                one(persistentDirectory).close();
            }
        });

        // call the system under test
        sut.close();

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert that calling createOutput calls that method on both of the
     * Directories, returning a CompositeIndexOutput object to write to so that
     * both Directories are written to.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testcreateOutput() throws IOException
    {
        final String outputName = "Foo";
        final IndexOutput fastReadIndexOutput = context.mock(IndexOutput.class, "fastReadIndexOutput");
        final IndexOutput persistentIndexOutput = context.mock(IndexOutput.class, "persistentIndexOutput");

        System.out.println(fastReadIndexOutput.toString());

        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).createOutput(outputName);
                will(returnValue(fastReadIndexOutput));

                one(persistentDirectory).createOutput(outputName);
                will(returnValue(persistentIndexOutput));
            }
        });

        // call the system under test
        IndexOutput result = sut.createOutput(outputName);

        assertTrue(
                "createOutput should return a CompositeIndexOutput "
                        + "so that all write operations are performed on both Directories",
                result instanceof CompositeIndexOutput);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert deleting a file deletes from both Directories.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testdeleteFile() throws IOException
    {
        final String fileToDelete = "somefile_a";
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).deleteFile(fileToDelete);
                one(persistentDirectory).deleteFile(fileToDelete);
            }
        });

        // call the system under test
        sut.deleteFile(fileToDelete);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert asking the SUT if a file exists passes through to the fast-read
     * Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testfileExists() throws IOException
    {
        final String name = "someFile_b";
        final boolean result = true;
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).fileExists(name);
                will(returnValue(result));
            }
        });

        // call the system under test
        assertEquals(result, sut.fileExists(name));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert checking the file length passes through to the fast-read
     * Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testfileLength() throws IOException
    {
        final long name = 100L;
        final String fileName = "foo_b";

        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).fileLength(fileName);
                will(returnValue(name));
            }
        });

        // call the system under test
        assertEquals(name, sut.fileLength(fileName));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert that checking if a file is modified passes through to the
     * fast-read Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testfileModified() throws IOException
    {
        final String name = "witty_saying_b";
        final long result = 382738L;
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).fileModified(name);
                will(returnValue(result));
            }
        });

        // call the system under test
        assertEquals(result, sut.fileModified(name));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert that asking the Directory for its list passes through to the
     * fast-read Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testlist() throws IOException
    {
        final String[] testList = { "hi", "there", "buddy" };
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).list();
                will(returnValue(testList));
            }
        });

        // call the system under test
        assertSame(testList, sut.list());

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert opening a file for reading passes through to the fast-read
     * Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testopenInputWithName() throws IOException
    {
        final String name = "barbee_q";
        final IndexInput result = context.mock(IndexInput.class);
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).openInput(name);
                will(returnValue(result));
            }
        });

        // call the system under test
        assertSame(result, sut.openInput(name));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert opening a file for reading (with buffer size) passes through to
     * the fast-read Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testopenInputWithNameAndBufferSize() throws IOException
    {
        final String name = "barbee_r";
        final IndexInput result = context.mock(IndexInput.class);
        final int bufferSize = 393;

        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).openInput(name, bufferSize);
                will(returnValue(result));
            }
        });

        // call the system under test
        assertEquals(result, sut.openInput(name, bufferSize));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert that renaming a file passes through to both the fast-read
     * Directory and persistent Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testrenameFile() throws IOException
    {
        final String from = "original_name";
        final String to = "new_name";
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).renameFile(from, to);
                one(persistentDirectory).renameFile(from, to);
            }
        });

        // call the system under test
        sut.renameFile(from, to);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert touching a file passes through to both the fast-read Directory and
     * persistent Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testtouchFile() throws IOException
    {
        final String name = "some_file_a";
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).touchFile(name);
                one(persistentDirectory).touchFile(name);
            }
        });

        // call the system under test
        sut.touchFile(name);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert clearing a file lock passes through to the persistent Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testclearLock() throws IOException
    {
        final String name = "rage_b";
        context.checking(new Expectations()
        {
            {
                one(persistentDirectory).clearLock(name);
            }
        });

        // call the system under test
        sut.clearLock(name);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert asking for a Lock ID passes through to the persistent Directory.
     */
    @Test
    public void testgetLockID()
    {
        final String lockId = "whale_eggs";
        context.checking(new Expectations()
        {
            {
                one(persistentDirectory).getLockID();
                will(returnValue(lockId));
            }
        });

        // call the system under test
        assertEquals(lockId, sut.getLockID());

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert making a Lock passes through to the persistent Directory.
     */
    @Test
    public void testmakeLock()
    {
        final String name = "makes_homer_something_something";
        final Lock result = context.mock(Lock.class);
        context.checking(new Expectations()
        {
            {
                one(persistentDirectory).makeLock(name);
                will(returnValue(result));
            }
        });

        // call the system under test
        assertSame(result, sut.makeLock(name));

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Assert syncing passes through to both the fast-read Directory and
     * persistent Directory.
     *
     * @throws IOException
     *             (shouldn't be called during mock)
     */
    @Test
    public void testsync() throws IOException
    {
        final String name = "foo";
        context.checking(new Expectations()
        {
            {
                one(fastReadDirectory).sync(name);
                one(persistentDirectory).sync(name);
            }
        });

        // call the system under test
        sut.sync(name);

        // all expectations met?
        context.assertIsSatisfied();
    }
}
