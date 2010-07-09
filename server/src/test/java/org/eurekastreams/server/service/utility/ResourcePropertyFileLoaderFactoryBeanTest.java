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
import java.util.Properties;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


/**
 * Tests the factory bean.
 */
public class ResourcePropertyFileLoaderFactoryBeanTest
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
    private ResourcePropertyFileLoaderFactoryBean sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ResourcePropertyFileLoaderFactoryBean(RESOURCE_NAME);
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
        assertEquals(Properties.class, sut.getObjectType());
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
        final String key1 = "my.property.name.1";
        final String value1 = "PropertyValue1";
        final String key2 = "my.property.name.2";
        final String value2 = "PropertyValue2";

        final byte[] bytes = (key1 + "=" + value1 + "\n" + key2 + "=" + value2).getBytes();
        final File file = context.mock(File.class);
        final InputStream stream = new ByteArrayInputStream(bytes);

        context.checking(new Expectations()
        {
            {
                allowing(resource).exists();
                will(returnValue(true));

                allowing(resource).getFile();
                will(returnValue(file));
                allowing(file).length();
                will(returnValue((long) bytes.length));

                allowing(resource).getInputStream();
                will(returnValue(stream));
            }
        });

        sut.afterPropertiesSet();

        context.assertIsSatisfied();

        Properties props = (Properties) sut.getObject();
        assertEquals(value1, props.get(key1));
        assertEquals(value2, props.get(key2));
    }
}
