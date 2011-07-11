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
package org.eurekastreams.server.testing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests TestHelper.
 */
public class TestHelperTest
{
    /**
     * Tests setPrivateField.
     *
     * @throws NoSuchFieldException
     *             Shouldn't.
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws IllegalArgumentException
     *             Shouldn't.
     */
    @Test
    public void testSetPrivateField() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        SomeClass sc = new SomeClass();
        TestHelper.setPrivateField(sc, "hiddenValue", "NEW!");
        assertEquals("NEW!", sc.getTheString());
    }

    /**
     * Tests setPrivateField.
     *
     * @throws NoSuchFieldException
     *             Should.
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws IllegalArgumentException
     *             Shouldn't.
     */
    @Test(expected = NoSuchFieldException.class)
    public void testSetPrivateFieldNotFound() throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException
    {
        SomeClass sc = new SomeClass();
        TestHelper.setPrivateField(sc, "somethingElse", "NEW!");
        assertEquals("NEW!", sc.getTheString());
    }

    /**
     * Class to use for testing setPrivateField.
     */
    static class SomeClass
    {
        /** Private field. */
        private String hiddenValue = "Original stuff";

        /**
         * Just here so Eclipse won't try to make hiddenValue final.
         */
        public void doSomething()
        {
            hiddenValue = "Wrong stuff";
        }

        /**
         * @return The hidden value, but don't follow bean spec to insure SUT is doing it's job of actually going
         *         directly at private data.
         */
        public String getTheString()
        {
            return hiddenValue;
        }
    }
}
