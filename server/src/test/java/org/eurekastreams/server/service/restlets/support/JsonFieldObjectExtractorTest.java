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
package org.eurekastreams.server.service.restlets.support;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

/**
 * Test JsonFieldObjectExtractor.
 */
public class JsonFieldObjectExtractorTest
{
    /** Test data. */
    private static final String FIELD = "field";

    /** SUT. */
    private JsonFieldObjectExtractor sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new JsonFieldObjectExtractor();
    }

    /**
     * Convenience routine: creates the JSON object the SUT operates on.
     *
     * @param fieldContent
     *            Data for the test.
     * @return JSON object.
     */
    private JSONObject makeObject(final String fieldContent)
    {
        String fullJson = "{" + FIELD + ":" + fieldContent + "}";
        return JSONObject.fromObject(fullJson);
    }

    /**
     * Common parts of all success tests.
     *
     * @param inputData
     *            Data for the test.
     * @param requestType
     *            Requested type passed to SUT.
     * @param expectedClass
     *            Expected result type.
     * @return The SUT return value.
     * @throws Exception
     *             Only if test fails.
     */
    private Object coreSuccessTest(final String inputData, final String requestType, final Class expectedClass)
            throws Exception
    {
        Object result = sut.extract(makeObject(inputData), FIELD, requestType);
        assertTrue("Expected result type of " + expectedClass.getName(), result.getClass() == expectedClass);
        return result;
    }

    /**
     * Checks a list for elements having correct types and content.
     *
     * @param uncastCollection
     *            List.
     * @param expected
     *            Expected elements.
     */
    private void assertListEquals(final Object uncastCollection, final Object... expected)
    {
        Collection collection = (Collection) uncastCollection;
        assertEquals("Wrong number of items", expected.length, collection.size());
        int i = 0;
        for (Object o : collection)
        {
            assertEquals("Element " + i + " has wrong type", expected[i].getClass(), o.getClass());
            assertEquals("Element " + i + " has wrong value", expected[i], o);
            i++;
        }
    }

    // ---------- SUCCESSFUL PARSING CASES ----------

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkLong() throws Exception
    {
        assertEquals(42L, coreSuccessTest("42", "LONG", Long.class));
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkInt() throws Exception
    {
        assertEquals(42, coreSuccessTest("42", "INT", Integer.class));
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkString() throws Exception
    {
        assertEquals("A string.", coreSuccessTest("'A string.'", "STRING", String.class));
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkBoolean() throws Exception
    {
        assertEquals(true, coreSuccessTest("true", "BOOLEAN", Boolean.class));
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkObject() throws Exception
    {
        SampleClass result = (SampleClass) coreSuccessTest("{number:42, text:'Hi', items:[{number:88, text:'Bye'}]}",
                "org.eurekastreams.server.service.restlets.support.JsonFieldObjectExtractorTest$SampleClass",
                SampleClass.class);
        assertEquals(42, result.getNumber());
        assertEquals("Hi", result.getText());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        SampleClass inner = result.getItems().get(0);
        assertEquals(88, inner.getNumber());
        assertEquals("Bye", inner.getText());
        assertNull(inner.getItems());
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkListLong() throws Exception
    {
        assertListEquals(coreSuccessTest("[1,3,5,7]", "java.util.ArrayList[LONG]", ArrayList.class), 1L, 3L, 5L, 7L);
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkListInt() throws Exception
    {
        assertListEquals(coreSuccessTest("[1,3,5,7]", "java.util.ArrayList[INT]", ArrayList.class), 1, 3, 5, 7);
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkListString() throws Exception
    {
        assertListEquals(coreSuccessTest("['This','is','a','test.']", "java.util.ArrayList[string]", ArrayList.class),
                "This", "is", "a", "test.");
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkListBoolean() throws Exception
    {
        assertListEquals(
                coreSuccessTest("[true, false, true, true, false]", "java.util.ArrayList[boolean]", ArrayList.class),
                true, false, true, true, false);
    }

    /**
     * Test.
     *
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testOkListObject() throws Exception
    {
        List<SampleClass> results = (List<SampleClass>) coreSuccessTest(
                "[{number:42, text:'Hi', items:[{number:88, text:'Bye'}]},"
                        + "{number:10, text:'Out', items:[{number:100, text:'In'}]}]",
                "java.util.ArrayList["
                        + "org.eurekastreams.server.service.restlets.support.JsonFieldObjectExtractorTest$SampleClass]",
                ArrayList.class);

        SampleClass result = results.get(0);
        assertEquals(42, result.getNumber());
        assertEquals("Hi", result.getText());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        SampleClass inner = result.getItems().get(0);
        assertEquals(88, inner.getNumber());
        assertEquals("Bye", inner.getText());
        assertNull(inner.getItems());

        result = results.get(1);
        assertEquals(10, result.getNumber());
        assertEquals("Out", result.getText());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        inner = result.getItems().get(0);
        assertEquals(100, inner.getNumber());
        assertEquals("In", inner.getText());
        assertNull(inner.getItems());
    }

    /**
     * Class used to test object deserialization.
     */
    public static class SampleClass
    {
        /** Number. */
        private int number;

        /** String. */
        private String text;

        /** Typed list. */
        private List<SampleClass> items;

        /**
         * Constructor.
         */
        public SampleClass()
        {
        }

        /**
         * @return the number
         */
        public int getNumber()
        {
            return number;
        }

        /**
         * @param inNumber
         *            the number to set
         */
        public void setNumber(final int inNumber)
        {
            number = inNumber;
        }

        /**
         * @return the text
         */
        public String getText()
        {
            return text;
        }

        /**
         * @param inText
         *            the text to set
         */
        public void setText(final String inText)
        {
            text = inText;
        }

        /**
         * @return the items
         */
        public List<SampleClass> getItems()
        {
            return items;
        }

        /**
         * @param inItems
         *            the items to set
         */
        public void setItems(final List<SampleClass> inItems)
        {
            items = inItems;
        }
    }

}
