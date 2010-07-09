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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.apache.lucene.store.FSDirectory;
import org.hibernate.search.engine.SearchFactoryImplementor;
import org.hibernate.search.store.FSDirectoryProvider;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for the CompositeDirectoryProvider. Since that class is a factory
 * and must initialize the RAMDirectory using Directory.copy, it's very
 * difficult to test the start() method. The rest can be set using some
 * intrusive protected property mocking.
 */
public class CompositeDirectoryProviderTest
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
     * Test initializing the DirectoryProvider.
     */
    @Test
    public void testInitialize()
    {
        final FSDirectoryProvider fsDirProviderMock = context
                .mock(FSDirectoryProvider.class);

        final String directoryProviderName = "Provider Name";
        final Properties propertiesMock = context.mock(Properties.class);
        final SearchFactoryImplementor searchFactoryMock = context
                .mock(SearchFactoryImplementor.class);
        final FSDirectory fsDirectoryMock = context.mock(FSDirectory.class,
                "fsDirectoryMock");

        CompositeDirectoryProvider sut = new CompositeDirectoryProvider(
                fsDirProviderMock);

        context.checking(new Expectations()
        {
            {
                one(fsDirProviderMock).initialize(directoryProviderName,
                        propertiesMock, searchFactoryMock);

                one(fsDirProviderMock).getDirectory();
                will(returnValue(fsDirectoryMock));
            }
        });

        // call the system under test
        sut
                .initialize(directoryProviderName, propertiesMock,
                        searchFactoryMock);

        // test getDirectory() as well, now that we've set it up
        assertNotNull(sut.getDirectory());
        assertTrue(sut.getDirectory() instanceof CompositeDirectory);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test start initializes the CompositeDirectory.
     *
     * @throws IOException
     *             on I/O error
     */
    @Test
    public void testStart() throws IOException
    {
        final CompositeDirectory directoryMock = context
                .mock(CompositeDirectory.class);

        CompositeDirectoryProvider sut = new CompositeDirectoryProvider();

        // Note: this property is only available for this unit test
        sut.setDirectory(directoryMock);

        context.checking(new Expectations()
        {
            {
                one(directoryMock).initializeFastReadDirectory();
            }
        });

        // call the system under test
        sut.start();

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test stop() closes the CompositeDirectory.
     *
     * @throws IOException
     *             on I/O error
     */
    @Test
    public void testStop() throws IOException
    {
        final CompositeDirectory directoryMock = context
                .mock(CompositeDirectory.class);

        CompositeDirectoryProvider sut = new CompositeDirectoryProvider();

        // Note: this property is only available for this unit test
        sut.setDirectory(directoryMock);

        context.checking(new Expectations()
        {
            {
                one(directoryMock).close();
            }
        });

        // call the system under test
        sut.stop();

        // all expectations met?
        context.assertIsSatisfied();
    }
}
