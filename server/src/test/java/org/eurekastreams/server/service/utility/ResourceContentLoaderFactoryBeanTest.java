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
package org.eurekastreams.server.service.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Test the factory bean. This test may be fragile, since it has to know which of the many behaviors of a Spring
 * resource loader the SUT will actually use and then emulate them.
 */
public class ResourceContentLoaderFactoryBeanTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final String RESOURCE_NAME = "classpath:wherever/file.txt";

    /** Fixture: resourceLoader. */
    private ResourceLoader resourceLoader = context.mock(ResourceLoader.class);

    /** Fixture: resource. */
    private Resource resource = context.mock(Resource.class);


    /** SUT. */
    private ResourceContentLoaderFactoryBean sut;


    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ResourceContentLoaderFactoryBean(RESOURCE_NAME);
        sut.setResourceLoader(resourceLoader);

        context.checking(new Expectations()
        {
            {
                allowing(resourceLoader).getResource(RESOURCE_NAME);
                will(returnValue(resource));
            }
        });
    }

    /**
     * Tests simple getters.
     */
    @Test
    public void testSimpleGetters()
    {
        assertEquals(String.class, sut.getObjectType());
        assertTrue(sut.isSingleton());
    }

    /**
     * Tests when the resource is not found.
     *
     * @throws Exception
     *             Expected.
     */
    @Test(expected = Exception.class)
    public void testNotFound() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(resource).exists();
                will(returnValue(false));
            }
        });

        sut.afterPropertiesSet();

        context.assertIsSatisfied();
    }

    /**
     * Tests successful load.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testSuccess() throws Exception
    {
        final String testValue = makeHugeString();
        final byte[] bytes = testValue.getBytes();
        final File file = context.mock(File.class);
        final InputStream stream = new ByteArrayInputStream(bytes);

        context.checking(new Expectations()
        {
            {
                allowing(resource).exists();
                will(returnValue(true));

                // allowing(resource).getFile();
                // will(returnValue(file));
                // allowing(file).length();
                // will(returnValue((long) bytes.length));

                allowing(resource).getInputStream();
                will(returnValue(stream));
            }
        });

        sut.afterPropertiesSet();

        context.assertIsSatisfied();

        assertEquals(testValue, sut.getObject());
    }

    /**
     * Makes a huge (bigger than the buffer size of the factory bean) string.
     *
     * @return Huge string.
     */
    private String makeHugeString()
    {
        final int targetSize = 64 * 1024;
        final String piece = "This is a test of the emergency broadcasting system.  ";
        StringBuilder sb = new StringBuilder(targetSize + 2 * piece.length());

        while (sb.length() < targetSize)
        {
            sb.append(piece);
        }

        return sb.toString();
    }
}
