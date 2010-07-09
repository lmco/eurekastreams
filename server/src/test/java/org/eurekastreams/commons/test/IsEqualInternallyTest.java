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
package org.eurekastreams.commons.test;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Tests the "equal internally" matcher (tests the test code). */
public class IsEqualInternallyTest
{
    /** Test data. */
    private static final String HELLO = "hello, world";

    /** A class to be compared for testing. */
    class ComparedClass
    {
        /** A private numeric field. */
        private int number;

        /** A private string field. */
        private String string;

        /** Constructor.
         * @param inNumber Number.
         * @param inString String.
         */
        public ComparedClass(final int inNumber, final String inString)
        {
            this.number = inNumber;
            this.string = inString;
        }

        /** Method to use the field to keep checkstyle happy.
         * @return A message. */
        public String getMessage()
        {
            return string + number;
        }
    }

    /** Another class to be compared for testing. */
    class AnotherComparedClass extends ComparedClass
    {
        /** A private field. */
        private float anotherNumber;

        /** Constructor.
         * @param inNumber Number.
         * @param inString String.
         * @param inAnotherNumber Number.
         */
        public AnotherComparedClass(final int inNumber, final String inString, final float inAnotherNumber)
        {
            super(inNumber, inString);
            this.anotherNumber = inAnotherNumber;
        }

        /** Method to use the field to keep checkstyle happy.
         * @return A message. */
        @Override
        public String getMessage()
        {
            return super.getMessage() + anotherNumber;
        }
    }

    /** The SUT. */
    private IsEqualInternally<ComparedClass> sut;

    /** The object to compare against. */
    ComparedClass referenceObject = new ComparedClass(9, HELLO);

    /**
     * Setup for each test.
     * @throws Exception Never.
     */
    @Before
    public void setUp() throws Exception
    {
        sut = new IsEqualInternally<ComparedClass>(referenceObject);
    }

    /**
     * Cleanup after each test.
     * @throws Exception Never.
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /** Tests that matching objects are correctly reported. */
    @Test
    public void testEqual()
    {
        ComparedClass object2 = new ComparedClass(9, HELLO);

        Assert.assertTrue("Identical objects should match.", sut.matches(object2));
    }

    /** Tests that objects of different types are reported as different. */
    @Test
    public void testNotEqualDifferentTypes()
    {
        Assert.assertFalse("Objects of different types should not match.", sut.matches("blah"));
    }

    /** Tests that and object doesn't match null. */
    @Test
    public void testNotEqualSecondNull()
    {
        Assert.assertFalse("Object should not match null.", sut.matches(null));
    }

    /** Tests that objects with some differing fields are reported as different. */
    @Test
    public void testNotEqualSomeDifferentFields1()
    {
        ComparedClass object2 = new ComparedClass(1, HELLO);
        Assert.assertFalse("Objects with some diffent fields should not match.", sut.matches(object2));
    }

    /** Tests that objects with some differing fields are reported as different. */
    @Test
    public void testNotEqualSomeDifferentFields2()
    {
        ComparedClass object2 = new ComparedClass(9, "hi");
        Assert.assertFalse("Objects with some diffent fields should not match.", sut.matches(object2));
    }

    /** Tests that objects of different types are reported as different. */
    @Test
    public void testNotEqualDerivedTypes()
    {
        final float number = (float) 3.14;
        AnotherComparedClass object2 = new AnotherComparedClass(9, HELLO, number);
        Assert.assertFalse("Objects of different types should not match.", sut.matches(object2));
    }

    /** Tests that null does not match non-null. */
    @Test
    public void testNotEqualFirstNull()
    {
        sut = new IsEqualInternally<ComparedClass>(null);
        ComparedClass object2 = new ComparedClass(9, HELLO);
        Assert.assertFalse("null should not match non-null.", sut.matches(object2));
    }

    /** Tests that null matches null. */
    @Test
    public void testEqualBothNull()
    {
        sut = new IsEqualInternally<ComparedClass>(null);
        Assert.assertTrue("null should match null.", sut.matches(null));
    }
}
