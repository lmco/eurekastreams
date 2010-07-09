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
package org.eurekastreams.commons.reflection;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.search.modelview.MyModelView;
import org.eurekastreams.commons.search.modelview.MyModelViewWithExceptionThrowingDefaultConstructor;
import org.eurekastreams.commons.search.modelview.MyModelViewWithHiddenDefaultConstructor;
import org.junit.Test;

/**
 * Test fixture for ReflectiveInstantiator.
 */
public class ReflectiveInstantiatorTest
{
    /**
     * Test the instantiateObject() method.
     */
    @Test
    public void testInstantiateObjectWithDefaultConstructor()
    {
        ReflectiveInstantiator sut = new ReflectiveInstantiator();

        Object testModelView = sut.instantiateObject(MyModelView.class);
        assertTrue(testModelView instanceof MyModelView);
    }

    /**
     * Test the instantiateObject() method on a class with a private default
     * constructor.
     */
    @Test
    public void testInstantiateObjectWithHiddenDefaultConstructor()
    {
        ReflectiveInstantiator sut = new ReflectiveInstantiator();

        Object testModelView = sut.instantiateObject(MyModelViewWithHiddenDefaultConstructor.class);
        assertTrue(testModelView instanceof MyModelViewWithHiddenDefaultConstructor);
    }

    /**
     * Test the instantiateObject() method on a class with no default
     * constructor.
     */
    @Test(expected = RuntimeException.class)
    public void testInstantiateObjectWithNoDefaultConstructor()
    {
        ReflectiveInstantiator sut = new ReflectiveInstantiator();

        sut.instantiateObject(MyModelViewWithExceptionThrowingDefaultConstructor.class);
    }
}
