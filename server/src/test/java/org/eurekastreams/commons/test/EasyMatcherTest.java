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
package org.eurekastreams.commons.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

/**
 * Tests EasyMatcher.
 */
public class EasyMatcherTest
{
    /**
     * Tests that the matcher correctly passes the object to be tested to the derived class' method.
     */
    @Test
    public void testCorrectTestObject()
    {
        final String testObject = "test";

        EasyMatcher<String> sut = new EasyMatcher<String>()
        {
            @Override
            protected boolean isMatch(final String inTestObject)
            {
                assertSame(testObject, inTestObject);
                return false;
            }
        };

        assertFalse(sut.matches(testObject));
    }

    /**
     * Tests that the matcher correctly passes through the derived class' return value (true).
     */
    @Test
    public void testMatch()
    {
        EasyMatcher<String> sut = new EasyMatcher<String>()
        {
            @Override
            protected boolean isMatch(final String inTestObject)
            {
                return true;
            }
        };

        assertTrue(sut.matches(null));
    }

    /**
     * Tests that the matcher correctly passes through the derived class' return value (false).
     */
    @Test
    public void testNotMatch()
    {
        EasyMatcher<String> sut = new EasyMatcher<String>()
        {
            @Override
            protected boolean isMatch(final String inTestObject)
            {
                return false;
            }
        };

        assertFalse(sut.matches(null));
    }

    /**
     * Tests that null exceptions result in the matcher returning false.
     */
    @Test
    public void testNull()
    {
        EasyMatcher<String> sut = new EasyMatcher<String>()
        {
            @Override
            protected boolean isMatch(final String inTestObject)
            {
                String s = null;
                s.length(); // force a null exception
                return false;
            }
        };

        assertFalse(sut.matches(null));
    }

    /**
     * Tests that cast exceptions result in the matcher returning false.
     */
    @Test
    public void testBadCast()
    {
        EasyMatcher<String> sut = new EasyMatcher<String>()
        {
            @Override
            protected boolean isMatch(final String inTestObject)
            {
                Object o = "string";
                Date d = (Date) o; // force a cast exception
                return false;
            }
        };

        assertFalse(sut.matches(null));
    }
}
