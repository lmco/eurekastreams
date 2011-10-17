/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.services.views;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.servlet.View;

/**
 * Tests BeanNameViewResolver.
 */
public class BeanNameViewResolverTest
{
    /** Test data. */
    private static final String BEAN_NAME = "BeanName";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** The context from which this service can load view beans. */
    private final BeanFactory beanFactory = context.mock(BeanFactory.class);

    /** SUT. */
    private BeanNameViewResolver sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new BeanNameViewResolver(beanFactory);
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testNotFound() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).containsBean(BEAN_NAME);
                will(returnValue(false));
            }
        });
        assertNull(sut.resolveViewName(BEAN_NAME, null));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testWrongType() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).containsBean(BEAN_NAME);
                will(returnValue(true));
                allowing(beanFactory).getBean(BEAN_NAME);
                will(returnValue("This is not a view bean"));
            }
        });
        assertNull(sut.resolveViewName(BEAN_NAME, null));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testValid() throws Exception
    {
        final Object bean = context.mock(View.class);
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).containsBean(BEAN_NAME);
                will(returnValue(true));
                allowing(beanFactory).getBean(BEAN_NAME);
                will(returnValue(bean));
            }
        });
        assertSame(bean, sut.resolveViewName(BEAN_NAME, null));
        context.assertIsSatisfied();
    }
}
